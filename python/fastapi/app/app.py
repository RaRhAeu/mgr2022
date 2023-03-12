import asyncio
import base64

import bcrypt

from fastapi import FastAPI
from aiohttp import ClientSession
from fastapi.responses import ORJSONResponse

from app.schemas import UserPasswordModel
from sqlalchemy import text
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker
from sqlalchemy.pool import AsyncAdaptedQueuePool

app = FastAPI(title="Python API")

engine = create_async_engine("postgresql+asyncpg://postgres:postgres@postgres:5432/postgres", echo_pool=True,
                             echo=True,
                             pool_size=10, max_overflow=0, poolclass=AsyncAdaptedQueuePool)

async_session = async_sessionmaker(engine, expire_on_commit=False)

default_response = ORJSONResponse({"status": "ok"})

QUERY = text("SELECT pg_sleep(0.5)")


@app.get("/s1")
def get_simple_response():
    return default_response


@app.get("/s2")
async def get_response_from_db():
    """SELECT pg_sleep(0.5):"""
    async with async_session() as session:
        await session.execute(QUERY)
        await session.commit()
    return default_response


@app.post("/s3")
def encrypt_user_password(model: UserPasswordModel):
    """Bcrypt 12 iterations"""
    salt = bcrypt.gensalt(rounds=12)
    _hash = bcrypt.hashpw(model.password.encode("utf-8"), salt)
    base_hash = base64.b64encode(_hash)
    return ORJSONResponse({"password": base_hash.decode("utf-8")})


@app.get("/s4")
async def get_aggregated_response():
    """Return aggregated responses from s1,s2,s3"""

    async with ClientSession() as session:
        tasks = [
            _get_scenario_response(session, "GET", scenario="s1"),
            _get_scenario_response(session, "GET", scenario="s2"),
            _get_scenario_response(session, "POST", scenario="s3", json={"password": "test-password"}),
        ]
        results = await asyncio.gather(*tasks)
    return ORJSONResponse({**results[0], **results[1], **results[2]})


async def _get_scenario_response(session: ClientSession, method, scenario, **kwargs):
    async with session.request(method, url=f"http://app-server:8000/{scenario}", **kwargs) as response:
        assert response.status == 200
        data = await response.json()
        return data

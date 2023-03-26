
import base64
import multiprocessing

import asyncpg
import bcrypt
from datetime import datetime, timezone
from fastapi import FastAPI

from fastapi.responses import ORJSONResponse, PlainTextResponse, Response

from app.schemas import UserPasswordModel

MAX_POOL_SIZE = 100 // multiprocessing.cpu_count()
MIN_POOL_SIZE = max(int(MAX_POOL_SIZE / 2), 1)



async def setup_database():
    return await asyncpg.create_pool(
        user="postgres",
        password="postgres",
        database="postgres",
        host="postgres",
        port=5432,
        min_size=MIN_POOL_SIZE,
        max_size=MAX_POOL_SIZE,
    )

app = FastAPI()


@app.on_event("startup")
async def connect_db():
    app.state.connection_pool = await setup_database()


@app.on_event("shutdown")
async def disconnect_db():
    await app.state.connection_pool.close()


@app.get("/s1")
def get_plaintext_response():
    return PlainTextResponse(b"Hello world")


@app.get("/s2")
def get_json_response():
    """SELECT pg_sleep(0.5):"""
    return ORJSONResponse({"status": "ok", "now": datetime.now(tz=timezone.utc)})


@app.post("/s3")
def encrypt_user_password(model: UserPasswordModel):
    """Bcrypt 12 iterations"""
    salt = bcrypt.gensalt(rounds=12)
    _hash = bcrypt.hashpw(model.password.encode("utf-8"), salt)
    base_hash = base64.b64encode(_hash)
    return ORJSONResponse({"password": base_hash.decode("utf-8")})


@app.get("/s4")
async def get_database_response():
    """Run SELECT pg_sleep(0.5) query"""
    async with app.state.connection_pool.acquire() as connection:
        await connection.fetch("SELECT 1 FROM pg_sleep(0.5)")
    return Response(status_code=200)

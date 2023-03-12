import base64
import requests
import bcrypt
from flask import Flask, jsonify, request
from concurrent.futures import ThreadPoolExecutor, as_completed
from sqlalchemy import create_engine, QueuePool, text
from sqlalchemy.orm import sessionmaker


from pydantic import ValidationError

from app.schemas import UserPasswordModel

app = Flask(__name__)


engine = create_engine("postgresql+psycopg2://postgres:postgres@postgres:5432/postgres", echo_pool=True, echo=True,
                       pool_size=10, max_overflow=0, poolclass=QueuePool)

session_maker = sessionmaker(autocommit=False, autoflush=False, bind=engine)

QUERY = text("SELECT pg_sleep(0.5)")


@app.get("/s1")
def get_simple_response():
    """Return {'status'; 'ok'}"""
    return jsonify({"status": "ok"})


@app.get("/s2")
def get_response_from_db():
    """SELECT pg_sleep(1):"""
    with session_maker() as session:
        session.execute(QUERY)
        session.commit()
    return jsonify({"status": "ok"})


@app.post("/s3")
def encrypt_user_password():
    """Bcrypt 12 iterations"""
    try:
        model = UserPasswordModel.parse_raw(request.data)
    except ValidationError as e:
        return jsonify(e.errors(), status=422)
    salt = bcrypt.gensalt(rounds=12)
    _hash = bcrypt.hashpw(model.password.encode("utf-8"), salt)
    base_hash = base64.b64encode(_hash)
    return jsonify({"password": base_hash.decode("utf-8")})


@app.get("/s4")
def get_aggregated_response():
    """Return aggregated responses from s1,s2,s3"""
    with ThreadPoolExecutor(max_workers=3) as executor:
        with requests.Session() as session:
            f1 = executor.submit(
                get_service_response, session, "GET", scenario="s1",
            )
            f2 = executor.submit(get_service_response, session, "GET", scenario="s2")
            f3 = executor.submit(
                get_service_response, session, "POST", scenario="s3", json={"password": "test-password"}
            )
            d = {}
            for r in as_completed((f1, f2, f3)):
                d.update(r.result())
    return jsonify(d)


def get_service_response(session, method, scenario, **kwargs):
    response = session.request(method, url=f"http://app-server:8000/{scenario}", **kwargs)
    assert response.status_code == 200
    return response.json()

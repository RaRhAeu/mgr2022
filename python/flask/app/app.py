import base64
import requests
import gevent
import bcrypt
from flask import Flask, jsonify, request

from app.schemas import UserPasswordModel

app = Flask(__name__)


@app.get("/s1")
def get_simple_response():
    """Return {'status'; 'ok'}"""
    return jsonify({"status": "ok"})


@app.get("/s2")
def get_response_from_db():
    """SELECT pg_sleep(1):"""


@app.get("/s3")
def encrypt_user_password():
    """Bcrypt 12 iterations"""
    model = UserPasswordModel.parse_raw(request.data)
    salt = bcrypt.gensalt(rounds=12)
    _hash = bcrypt.hashpw(model.password.encode("utf-8"), salt)
    base_hash = base64.b64encode(_hash)
    return jsonify({"password": base_hash.decode("utf-8")})


@app.get("/s4")
def get_aggregated_response():
    """Return aggregated responses from s1,s2,s3"""
    with requests.Session() as session:
        tasks = [
            gevent.spawn(get_service_response, session, "GET", scenario="s1"),
            gevent.spawn(get_service_response, session, "GET", scenario="s1"),
            gevent.spawn(get_service_response, session, "POST", scenario="s3", json={"password": "test-password"}),
        ]
        gevent.wait(tasks)
    return jsonify({**tasks[0].value, **tasks[1].value, **tasks[2].value})


def get_service_response(session, method, scenario, **kwargs):
    response = session.request(method, url=f"http://app-server/{scenario}", **kwargs)
    assert response.status_code == 200
    return response.json()

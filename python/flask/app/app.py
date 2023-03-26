import base64
import bcrypt
from flask import Flask, jsonify, request
from flask.json.provider import JSONProvider

import multiprocessing
from datetime import datetime, timezone
from psycopg2.pool import ThreadedConnectionPool

from app.schemas import UserPasswordModel, schema_loads, schema_dumps


class ORJSONProvider(JSONProvider):

    def dumps(self, obj):
        return schema_dumps(obj)

    def loads(self, obj):
        return schema_loads(obj)

app = Flask(__name__)
app.json = ORJSONProvider(app)




query = "SELECT 1 FROM pg_sleep(0.5)"

MAX_POOL_SIZE = 100 // (2 * multiprocessing.cpu_count() + 1)
MIN_POOL_SIZE = max(int(MAX_POOL_SIZE / 2), 1)


pool = ThreadedConnectionPool(
    minconn=MIN_POOL_SIZE,
    maxconn=MAX_POOL_SIZE,
    database="postgres",
    user="postgres",
    password="postgres",
    host="postgres",
    port=5432,
)


@app.get("/s1")
def get_plaintext_response():
    return b"Hello, World!", {"Content-Type": "text/plain"}


@app.get("/s2")
def get_json_response():
    return jsonify({"status": "ok", "now": datetime.now(tz=timezone.utc)})


@app.post("/s3")
def encrypt_user_password():
    """Bcrypt 12 iterations"""
    model = UserPasswordModel.parse_raw(request.data)
    salt = bcrypt.gensalt(rounds=12)
    _hash = bcrypt.hashpw(model.password.encode("utf-8"), salt)
    base_hash = base64.b64encode(_hash)
    return jsonify({"password": base_hash.decode("utf-8")})


@app.get("/s4")
def get_database_response():
    conn = pool.getconn()
    cursor = conn.cursor()
    cursor.execute(query)
    cursor.fetchone()
    pool.putconn(conn)
    return '', 200
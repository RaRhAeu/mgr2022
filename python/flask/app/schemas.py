import orjson
from pydantic import BaseModel as _BaseModel
from pydantic.json import pydantic_encoder


def schema_loads(obj):
    return orjson.loads(obj)


def schema_dumps(v, *, default=pydantic_encoder):
    # orjson.dumps returns bytes, to match standard json.dumps we need to decode
    return orjson.dumps(v, default=default).decode("utf-8")


class BaseModel(_BaseModel):

    class Config:
        json_loads = schema_loads
        json_dumps = schema_dumps


class UserPasswordModel(BaseModel):
    password: str

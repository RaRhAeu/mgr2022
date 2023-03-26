FROM python:3.11.2-slim

WORKDIR /app


RUN pip install -U pip
RUN pip install fastapi uvloop uvicorn bcrypt orjson asyncpg gunicorn

COPY ./python/fastapi/gunicorn_conf.py .
COPY ./python/fastapi/app ./app

CMD ["gunicorn", "app.asgi:app", "-k", "uvicorn.workers.UvicornWorker", "-c", "gunicorn_conf.py"]
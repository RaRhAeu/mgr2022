FROM python:3.11.2-slim

WORKDIR /app

RUN apt-get update && apt-get install libpq-dev gcc -y \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

RUN pip install -U pip && pip install flask orjson pydantic gunicorn bcrypt psycopg2

COPY ./python/flask/gunicorn_conf.py .
COPY ./python/flask/app ./app

CMD ["gunicorn", "app.wsgi:app", "-c", "gunicorn_conf.py"]
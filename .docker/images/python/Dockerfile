FROM python:3.9.4-slim

RUN pip install --user -U pip

COPY ./requirements.txt .

RUN pip install -r ./requirements.txt

COPY ./app /app

WORKDIR /app

# CMD []
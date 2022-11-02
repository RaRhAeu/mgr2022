FROM python:3.9.4-slim

ARG USERNAME=appuser
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ARG WORKDIR=/app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN adduser -u $USER_UID --disabled-password --gecos "" $USERNAME && chown -R $USERNAME $WORKDIR
USER $USERNAME


RUN apt update && apt install -y gcc libuv-dev libpq \
    && rm -rf /var/lib/apt/lists/* \

RUN pip install --user -U pip


COPY ./python/fastapi/requirements.txt .

RUN pip install -r ./requirements.txt

COPY ./python/fastapi/app /app

CMD ["uvicorn", "app.asgi:app", "--host", "0.0.0.0", "--port", "8000", "--loop", "uvloop", "--http", "h11", "--log-level", "info"]
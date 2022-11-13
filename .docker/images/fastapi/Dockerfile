FROM python:3.9.4-slim

ARG USERNAME=appuser
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ARG WORKDIR=/app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

WORKDIR $WORKDIR

RUN apt update && apt install -y gcc libuv1-dev libpq-dev

RUN adduser -u $USER_UID --disabled-password --gecos "" $USERNAME && chown -R $USERNAME $WORKDIR
USER $USERNAME
ENV PATH $PATH:/home/$USERNAME/.local/bin


RUN pip install --user -U pip


COPY ./python/fastapi/requirements.txt .

RUN pip install -r ./requirements.txt

COPY ./python/fastapi/app ./app

#CMD ["uvicorn", "app.asgi:app", "--host", "0.0.0.0", "--port", "8000", "--loop", "uvloop", "--http", "h11", "--log-level", "info"]
CMD ["gunicorn", "app.asgi:app", "--workers", "9", "--worker-class", "uvicorn.workers.UvicornWorker", "--timeout", "120", "--log-level", "info", "--bind", "0.0.0.0:8000"]
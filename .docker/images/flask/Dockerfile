FROM python:3.9.4-slim

ARG USERNAME=appuser
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ARG WORKDIR=/app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

WORKDIR $WORKDIR

RUN apt update && apt install -y gcc libev-dev libpq-dev

RUN adduser -u $USER_UID --disabled-password --gecos "" $USERNAME && chown -R $USERNAME $WORKDIR
USER $USERNAME
ENV PATH $PATH:/home/$USERNAME/.local/bin

RUN pip install -U pip

COPY ./python/flask/requirements.txt .

RUN pip install -r ./requirements.txt

COPY ./python/flask/app ./app

CMD ["python", "./app/wsgi.py"]
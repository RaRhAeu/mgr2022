FROM python:3.11.2-slim

ARG USERNAME=appuser
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ARG WORKDIR=/app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV POETRY_VIRTUALENVS_IN_PROJECT true
ENV POETRY_VERSION 1.4.0

WORKDIR $WORKDIR


RUN apt update && apt install -y curl gcc libpq-dev \
    && apt clean && rm -rf /var/lib/apt/lists/*

RUN adduser -u $USER_UID --disabled-password --gecos "" $USERNAME && chown -R $USERNAME $WORKDIR
USER $USERNAME
ENV PATH /app/.venv/bin:$PATH:/home/$USERNAME/.local/bin

RUN pip install --user -U pip

RUN curl -sSL https://install.python-poetry.org | python3 -

COPY ./python/flask/poetry.lock ./python/flask/pyproject.toml ./
RUN poetry install --only=main --no-root

COPY ./python/flask/app ./app


CMD ["gunicorn", "app.wsgi:app", "--workers", "1", "--threads", "50", "--timeout", "120", "--log-level", "info", "--bind", "0.0.0.0:8000"]
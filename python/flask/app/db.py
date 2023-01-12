from contextlib import contextmanager

from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy import create_engine
from sqlalchemy.pool import QueuePool


engine = create_engine(
    "postgresql+psycopg2://postgres:postgres@postgres:5432/postgres",
    poolclass=QueuePool,
    pool_size=10,
    max_overflow=0,
    pool_recycle=1200,
    # echo=True,
    echo_pool=True,
)

Session = sessionmaker(
    bind=engine, autoflush=False, autocommit=False, expire_on_commit=True
)

session = scoped_session(Session)


@contextmanager
def db_session():
    try:
        yield session
        session.commit()
    except:  # noqa E772
        session.rollback()
        raise


import asyncpg


class DB:

    def __init__(self, **params):
        self.params = params
        self._pool = None

    async def connect(self):
        self._pool = await asyncpg.create_pool(**self.params)

    async def fetch_row(self, query: str):
        async with self._pool.acquire() as connection:
            # async with connection.transaction():
            result = await connection.fechrow(query)
            return result

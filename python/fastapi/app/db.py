import asyncpg


class DB:

    def __init__(self, **params):
        self.params = params
        self._pool = None

    async def connect(self):
        self._pool = await asyncpg.create_pool(**self.params)

    async def execute(self, query: str):
        async with self._pool.acquire() as connection:
            # async with connection.transaction():
            await connection.execute(query)

    async def disconnect(self):
        await self._pool.close()
import os
from abc import abstractmethod
from typing import Any


class AbstractSecretProvider:

    @abstractmethod
    def get(self, name: str) -> Any:
        raise NotImplementedError


class ApiSecretProvider(AbstractSecretProvider):

    def get(self, name: str):
        # return request.get(f"https://config-service/{name}")
        pass


class EnvSecretProvider(AbstractSecretProvider):

    def get(self, name: str) -> Any:
        return os.getenv(name)


provider = EnvSecretProvider() if os.getenv("ENV") == "DEV" else ApiSecretProvider()

"""
config.yaml
ENV=DEV
{"secret": "/users/123432/postgres"}
"""
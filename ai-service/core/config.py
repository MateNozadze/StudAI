from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    model_name: str = "gemma4:e4b"
    temperature: float = 0.7

    class Config:
        env_file = ".env"
        extra = "ignore"

settings = Settings()
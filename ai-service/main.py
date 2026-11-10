from fastapi import FastAPI
from api.routes.quiz_routes import router as quiz_router

app = FastAPI(
    title="StudyAI - Python AI Service",
    description="Python AI Microservice for StudyPilot Platform",
    version="1.0.0"
)

app.include_router(quiz_router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
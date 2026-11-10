from fastapi import APIRouter, HTTPException
from models.schemas import QuizRequest, QuestionResponse
from services.quiz_service import quiz_service
from typing import List

router = APIRouter(prefix="/api/ai", tags=["AI Quiz Core"])

@router.post("/generate-quiz", response_model=List[QuestionResponse])
def generate_quiz(request: QuizRequest):
    try:
        return quiz_service.generate_quiz(request)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
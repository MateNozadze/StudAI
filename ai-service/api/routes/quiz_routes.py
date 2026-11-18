import traceback
from fastapi import APIRouter, HTTPException
from models.schemas import QuizRequest as QuizGenerateRequest, QuestionResponse as QuizResponse
from services.quiz_service import generate_quiz_with_agent

router = APIRouter(prefix="/api/quiz", tags=["AI Agent"])

@router.post("/generate", response_model=list[QuizResponse])
def generate_quiz_endpoint(request: QuizGenerateRequest):
    try:
        quiz_result = generate_quiz_with_agent(request)
        return quiz_result
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))

@router.delete("/{quiz_id}")
def delete_quiz_endpoint(quiz_id: str):
    try:
        
        return {"status": "success", "message": f"Quiz {quiz_id} deleted successfully"}
    except Exception as e:
        raise HTTPException(status_code=404, detail="Quiz not found")
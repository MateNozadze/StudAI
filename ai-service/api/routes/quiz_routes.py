from fastapi import APIRouter, HTTPException
from models.schemas import QuizRequest as QuizGenerateRequest, QuestionResponse as QuizResponse
from services.quiz_service import generate_quiz_with_agent

router = APIRouter(prefix="/ai", tags=["AI Agent"])

@router.post("/generate-quiz", response_model=QuizResponse)
def generate_quiz_endpoint(request: QuizGenerateRequest):

    """
    Endpoint to generate a quiz based on provided study material and question count.
    """
   
    try:
        quiz_result = generate_quiz_with_agent(
            material=request.material,
            question_count=request.question_count
        )
        return quiz_result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
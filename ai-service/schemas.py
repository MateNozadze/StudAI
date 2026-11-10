from pydantic import BaseModel, Field
from typing import List

class QuizRequest(BaseModel):
    material: str
    questionCount: int = Field(default=5, ge=1, le=20)

class QuestionResponse(BaseModel):
    question: str
    options: List[str]
    correctAnswer: str
    explanation: str
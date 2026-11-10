from pydantic import BaseModel, Field
from typing import List

class QuizRequest(BaseModel):
    material: str = Field(..., description="Study material text")
    questionCount: int = Field(default=5, ge=1, le=20, description="Number of questions from 1 to 20")
    difficulty: str = Field(default="medium", description="Difficulty level")


class QuestionResponse(BaseModel):
    question: str
    options: List[str]
    correctAnswer: str
    explanation: str

    model_config = {
        "json_schema_extra": {
            "example": {
                "question": "What is Spring Boot?",
                "options": ["Framework", "Database", "Browser", "Operating System"],
                "correctAnswer": "Framework",
                "explanation": "Spring Boot accelerates the development of Java applications."
            }
        }
    }
from models.schemas import QuizRequest, QuestionResponse
from typing import List
from chains.quiz_chain import get_quiz_chain

class QuizService:
    def __init__(self):
        self.chain = get_quiz_chain()

    def generate_quiz(self, request: QuizRequest) -> List[QuestionResponse]:
        result = self.chain.invoke({
            "material": request.material,
            "questionCount": request.questionCount
        })

        return result.questions

quiz_service = QuizService()
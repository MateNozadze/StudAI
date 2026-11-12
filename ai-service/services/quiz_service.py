from httpcore import request

from models.schemas import QuizRequest, QuestionResponse
from typing import List
from agents.study_agent.graph import study_graph  

class QuizService:
    def __init__(self):
        pass

    def generate_quiz(self, request: QuizRequest) -> List[QuestionResponse]:
        """
        Generates a quiz based on the provided study material and question.
        """

        initial_state = {
            "material_content": request.material,
            "messages": [],
            "question_count": request.questionCount,
            "quiz_data": None,
            "current_step": "started"
        }
        
        print("--- [Service] Running Study Graph Agent ---")
        
        final_state = study_graph.invoke(initial_state)

        quiz_data = final_state.get("quiz_data")
        
        if not quiz_data:
            raise ValueError("Agent failed to generate quiz data.")
        
        return quiz_data.get("questions", [])
    

def generate_quiz_with_agent(request: QuizRequest):
    return quiz_service.generate_quiz(request)

quiz_service = QuizService()
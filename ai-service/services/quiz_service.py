from typing import List
from models.schemas import QuizRequest, QuestionResponse
from agents.study_agent.graph import study_graph
from core.ollama_manager import temporary_ollama_server


class QuizService:
    def __init__(self):
        pass

    def generate_quiz(self, request: QuizRequest) -> List[QuestionResponse]:
        """
        Generates a quiz based on the provided study material and question count.
        Automatically manages the Ollama server lifecycle.
        """
        initial_state = {
            "material": request.material,
            "subject": request.subject,
            "questionCount": request.questionCount,
            "difficulty": request.difficulty,
            "questions": []
        }

        print("--- [Service] Running Study Graph Agent ---")

        with temporary_ollama_server():
            final_state = study_graph.run(initial_state)

        questions = final_state.get("questions", [])

        if not questions:
            raise ValueError("Agent failed to generate quiz questions.")

        return questions


def generate_quiz_with_agent(request: QuizRequest):
    return quiz_service.generate_quiz(request)


quiz_service = QuizService()
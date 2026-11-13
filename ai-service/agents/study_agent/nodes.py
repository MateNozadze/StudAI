from typing import Dict, Any
from chains.quiz_chain import get_quiz_chain
from .state import StudyState

def analyze_material_node(state: StudyState) -> Dict[str, Any]:
    """
    Step 1: Analyzes the study material and prepares it for quiz generation.
    """
    content = state.get("material", "")
    print("--- [Agent] Analyzing Material ---")
    print(f"Material successfully received. Length: {len(content)} characters.")
    
    return {
        "material": content
    }

def generate_quiz_node(state: StudyState) -> Dict[str, Any]:
    """
    Step 2: Uses get_quiz_chain() to generate a quiz.
    """
    content = state.get("material", "")
    print("--- [Agent] Generating Quiz via Ollama & LangChain ---")
    
    if not content:
        raise ValueError("Material content is empty in state!")
    
    quiz_chain = get_quiz_chain()
    question_count = state.get("questionCount", 3)
    
    result = quiz_chain.invoke({
        "material": content,
        "questionCount": question_count
    })
    
    if hasattr(result, "model_dump"):
        quiz_data = result.model_dump()
    elif hasattr(result, "dict"):
        quiz_data = result.dict()
    else:
        quiz_data = dict(result)
        
    questions_list = quiz_data.get("questions", []) if isinstance(quiz_data, dict) else quiz_data
    
    return {
        "questions": questions_list
    }
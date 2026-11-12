from typing import Dict, Any
from chains.quiz_chain import get_quiz_chain
from .state import StudyState

def analyze_material_node(state: StudyState) -> Dict[str, Any]:

    """
    Step 1: Analyzes the study material and prepares it for quiz generation.
    """
   
    content = state.get("material_content", "")
    print("--- [Agent] Analyzing Material ---")
    
    analysis_message = f"Material successfully received. Length: {len(content)} characters."
    updated_messages = state.get("messages", []) + [{"role": "assistant", "content": analysis_message}]
    
    return {
        "messages": updated_messages,
        "current_step": "material_analyzed"
    }


def generate_quiz_node(state: StudyState) -> Dict[str, Any]:
    """
    Step 2: Uses get_quiz_chain() to generate a quiz.
    """
    content = state.get("material_content", "")
    print("--- [Agent] Generating Quiz via Ollama & LangChain ---")
    
    if not content:
        raise ValueError("Material content is empty in state!")
    
    quiz_chain = get_quiz_chain()
    
    question_count = state.get("question_count", 3)
    
    result = quiz_chain.invoke({
        "material": content,
        "questionCount": question_count
    })
    
    quiz_data = result.dict() if hasattr(result, "dict") else dict(result)
    
    return {
        "quiz_data": quiz_data,
        "current_step": "quiz_generated"
    }
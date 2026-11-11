from langchain_ollama import ChatOllama
from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import PydanticOutputParser
from pydantic import BaseModel
from typing import List
from models.schemas import QuestionResponse
from core.config import settings

class QuizOutputList(BaseModel):
    questions: List[QuestionResponse]

def get_quiz_chain():
    llm = ChatOllama(
        model=settings.model_name,
        temperature=0.7,
        n_gpu_layers = 0,
        num_ctx=4096,
        num_predict=2048,
        n_ctx = 2048,
    )
    
    parser = PydanticOutputParser(pydantic_object=QuizOutputList)

    prompt = PromptTemplate(
        template=(
            "You are a professional AI educational assistant.\n"
            "Based on the provided study material, generate exactly {questionCount} multiple-choice test questions.\n"
            "Each question must strictly contain: 'question', 'options' (exactly 4 choices), 'correctAnswer', and 'explanation'.\n"
            "Ensure you return a complete, valid, and properly closed JSON structure adhering to the schema.\n\n"
            "Study Material:\n{material}\n\n"
            "{format_instructions}"
        ),
        input_variables=["material", "questionCount"],
        partial_variables={"format_instructions": parser.get_format_instructions()},
    )

    return prompt | llm | parser
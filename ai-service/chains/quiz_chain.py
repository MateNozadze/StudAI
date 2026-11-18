import os
from typing import List
from pydantic import BaseModel, Field
from langchain_ollama import ChatOllama
from langchain_core.prompts import ChatPromptTemplate
from models.schemas import QuestionResponse
from core.config import settings


class QuizOutputList(BaseModel):
    questions: List[QuestionResponse] = Field(
        description="List of generated multiple-choice questions strictly matching the requested count."
    )


def get_quiz_chain():
    llm = ChatOllama(
        model=settings.model_name,
        base_url=os.getenv("OLLAMA_BASE_URL", "http://localhost:11434"),
        temperature=0.25,
        num_gpu=-1,
        num_ctx=4096,
        num_predict=2048,
        num_thread=8,
        format="json",
        repeat_penalty=1.1,
    )

    prompt = ChatPromptTemplate.from_messages([
        (
            "system",
            "You are a precise educational assessment generator. "
            "CRITICAL RULE: You must generate the quiz questions, options, and explanations in the **exact same language** "
            "in which the provided study material is written (e.g., if the material is in Georgian, generate everything in Georgian). "
            "Generate strictly valid JSON matching the required schema. "
            "Each question must include: 'question', 'options' (array of exactly 4 strings), "
            "'correctAnswer' (exact match to one of the options), and 'explanation'. "
            "Do not output markdown blocks, greetings, or extra commentary."
        ),
        (
            "human",
            "Generate exactly {questionCount} multiple-choice questions based on the following study material.\n\n"
            "Study Material:\n{material}"
        ),
    ])

    # with_structured_output იყენებს Ollama-ს ნატიურ JSON სქემის ვალიდაციას (Grammar-constrained sampling).
    # ეს გამორიცხავს პარსინგის შეცდომებს და ამცირებს პრომპტის ზომას (არ სჭირდება format_instructions).
    structured_llm = llm.with_structured_output(
        schema=QuizOutputList,
        method="json_schema"
    )

    return prompt | structured_llm

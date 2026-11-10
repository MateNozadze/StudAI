from fastapi import FastAPI, HTTPException
from schemas import QuizRequest, QuestionResponse
from typing import List

app = FastAPI(title="StudyAI - Python AI Service")

@app.post("/api/ai/generate-quiz", response_model=List[QuestionResponse])
def generate_quiz(request: QuizRequest):
    try:
        # აქ მოგვიანებით ჩაჯდება LangChain-ის და LLM-ის რეალური ლოგიკა.
        # ჯერჯერობით ვაბრუნებთ ტესტურ (stub) მონაცემებს, რომ კავშირი შევამოწმოთ.
        
        print(f"Received material length: {len(request.material)} chars")
        print(f"Requested question count: {request.questionCount}")

        dummy_questions = []
        for i in range(1, request.questionCount + 1):
            dummy_questions.append(
                QuestionResponse(
                    question=f"Test Question #{i}: What does Spring Boot do?",
                    options=["Automatic configuration", "Frontend only", "Deletes the database", "Nothing"],
                    correctAnswer="Automatic configuration",
                    explanation="Spring Boot reduces boilerplate code."
                )
            )
            
        return dummy_questions

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
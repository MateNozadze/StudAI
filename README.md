# StudyPilot AI

A web platform that generates quizzes from study material using AI and stores each user's progress.

## Features
- Registration and login (JWT authentication)
- Quiz generation from study text (questions, 4 options, explanations) in the language of the material
- Quizzes stored in the database: list and delete them
- Take a quiz, with the score and selected answers saved
- Profile: statistics and quiz history

## Architecture
| Service | Technology | Role |
|---|---|---|
| `frontend` | HTML, CSS, JavaScript (nginx) | User interface |
| `studai-backend` | Java, Spring Boot, Spring Security, PostgreSQL | Business logic, authentication, database |
| `ai-service` | Python, FastAPI, LangGraph, LangChain, Ollama | Quiz generation with a local model |

Browser → Spring Boot (`:8080`) → FastAPI (`:8000`) → Ollama (`:11434`)

## Running with Docker
1. Install and start [Ollama](https://ollama.com) on the host, then pull the model: `ollama pull gemma4:e4b`
2. `cp .env.example .env` and set `POSTGRES_PASSWORD` and `JWT_SECRET` (at least 64 characters)
3. `docker compose up --build`
4. Open `http://localhost:5500`

If the container cannot reach Ollama, set `OLLAMA_HOST=0.0.0.0` on the host.

## Main endpoints
| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Register / log in |
| POST | `/api/quizzes/generate` | Generate and save a quiz |
| GET | `/api/quizzes/all`, `/api/quizzes/{id}` | List quizzes / get one quiz |
| POST | `/api/quizzes/{id}/submit` | Grade a quiz and save the result |
| DELETE | `/api/quizzes/{id}` | Delete a quiz |
| GET | `/api/quizzes/attempts`, `/api/users/me` | History / profile |

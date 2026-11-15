FROM python:3.11-slim

WORKDIR /app

# ჯერ ვაკოპირებთ dependencies, რომ Docker-მა ქეშირება გამოიყენოს
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# ვაკოპირებთ დანარჩენ კოდს
COPY . .

EXPOSE 8000
# თუ შენს აპლიკაციას main.py ჰქვია და შიგნით app ობიექტია:
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
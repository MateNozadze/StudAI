document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        localStorage.removeItem('token');
        window.location.href = 'login.html';
    });

    let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];
    renderQuizzesList();

    const quizForm = document.getElementById('quizForm');
    quizForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const title = document.getElementById('materialTitle').value.trim();
        const count = parseInt(document.getElementById('questionCount').value, 10);
        const text = document.getElementById('materialText').value.trim();
        const generateBtn = document.getElementById('generateBtn');

        if (!title || !text) {
            alert('გთხოვთ მიუთითოთ სათაური და ჩააკოპიროთ ტექსტი!');
            return;
        }

        generateBtn.disabled = true;
        generateBtn.textContent = 'AI აანალიზებს და გენერირებს ქვიზს... ⏳';

        try {
            const response = await fetch('http://127.0.0.1:8000/api/quiz/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    material: text,
                    subject: title,
                    questionCount: count,
                    difficulty: "medium"
                })
            });

            if (!response.ok) {
                throw new Error('გენერაციისას მოხდა შეცდომა სერვერზე.');
            }

            const data = await response.json();
            
            const newQuiz = {
                id: Date.now(),
                title: title,
                date: new Date().toLocaleDateString('ka-GE'),
                questions: data
            };

            userQuizzes.push(newQuiz);
            localStorage.setItem('userQuizzes', JSON.stringify(userQuizzes));

            document.getElementById('materialTitle').value = '';
            document.getElementById('materialText').value = '';
            renderQuizzesList();

            alert('ქვიზი წარმატებით დაგენერირდა AI-ს მიერ!');
        } catch (error) {
            console.error(error);
            alert('ვერ მოხერხდა სერვერთან დაკავშირება. დარწმუნდი, რომ პითონის სერვისი ჩართულია!');
        } finally {
            generateBtn.disabled = false;
            generateBtn.textContent = 'ქვიზის გენერაცია 🚀';
        }
    });
});

// გლობალური ფუქნციები ღილაკებისთვის (HTML-დან რომ იძახებდეს)

function renderQuizzesList() {
    const container = document.getElementById('quizzesContainer');
    if (!container) return;

    let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];

    if (userQuizzes.length === 0) {
        container.innerHTML = `<p class="empty-text">ჯერ არ გაქვს გენერირებული ქვიზები.</p>`;
        return;
    }

    container.innerHTML = userQuizzes.map(quiz => `
        <div class="quiz-item-card">
            <div class="quiz-card-content" onclick="openQuiz(${quiz.id})" style="cursor: pointer;">
                <h3>${quiz.title}</h3>
                <p>შექმნილია: ${quiz.date}</p>
                <span class="quiz-badge">კითხვები: ${quiz.questions.length}</span>
            </div>
            <div class="quiz-card-actions">
                <button type="button" class="btn-solid btn-sm" onclick="openQuiz(${quiz.id})">გახსნა →</button>
                <button type="button" class="btn-delete btn-sm" onclick="deleteQuiz(${quiz.id})">🗑️ წაშლა</button>
            </div>
        </div>
    `).join('');
}

function openQuiz(quizId) {
    let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];
    const quiz = userQuizzes.find(q => q.id === quizId);
    if (!quiz) return;

    document.getElementById('uploadSection').style.display = 'none';
    document.getElementById('quizzesListSection').style.display = 'none';
    document.getElementById('singleQuizView').style.display = 'block';
    document.getElementById('activeQuizTitle').textContent = quiz.title;

    const qContainer = document.getElementById('activeQuizQuestions');
    qContainer.innerHTML = quiz.questions.map((q, index) => `
        <div class="quiz-question-box">
            <p><strong>კითხვა ${index + 1}:</strong> ${q.question}</p>
            <ul class="quiz-options">
                ${q.options.map((opt) => `<li>${opt}</li>`).join('')}
            </ul>
        </div>
    `).join('');
}

function deleteQuiz(quizId) {
    if (confirm('ნამდვილად გინდა ამ ქვიზის წაშლა?')) {
        let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];
        userQuizzes = userQuizzes.filter(q => q.id !== quizId);
        localStorage.setItem('userQuizzes', JSON.stringify(userQuizzes));
        renderQuizzesList();
    }
}

// უკან დაბრუნების ღილაკის მოსმენა
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('backToListBtn')?.addEventListener('click', () => {
        document.getElementById('singleQuizView').style.display = 'none';
        document.getElementById('uploadSection').style.display = 'block';
        document.getElementById('quizzesListSection').style.display = 'block';
        renderQuizzesList();
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    renderQuizzesList(token);

    // უკან დაბრუნების ღილაკი სიის ხედზე
    document.getElementById('backToListBtn')?.addEventListener('click', () => {
        document.getElementById('singleQuizView').style.display = 'none';
        document.getElementById('quizzesListSection').style.display = 'block';
        renderQuizzesList();
    });
});

async function renderQuizzesList(token = localStorage.getItem('token')) {
    const container = document.getElementById('quizzesContainer');
    if (!container) return;

    let userQuizzes = [];
    try {
        const res = await fetch('http://localhost:8080/api/quizzes/all', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.status === 401 || res.status === 403) {
            localStorage.removeItem('token');
            window.location.href = 'login.html';
            return;
        }
        userQuizzes = await res.json();
    } catch (e) {
        container.innerHTML = `<p class="empty-text">სერვერთან კავშირი ვერ მოხერხდა.</p>`;
        return;
    }

    if (userQuizzes.length === 0) {
        container.innerHTML = `<p class="empty-text">ჯერ არ გაქვს გენერირებული ქვიზები.</p>`;
        return;
    }

    container.innerHTML = userQuizzes.map(quiz => `
        <div class="quiz-item-card">
            <div class="quiz-card-content" onclick="location.href='take-quiz.html?id=${quiz.id}'" style="cursor: pointer;">
                <h3>${quiz.title}</h3>
                <span class="quiz-badge">კითხვები: ${quiz.questions.length}</span>
            </div>
            <div class="quiz-card-actions">
                <button type="button" class="btn-solid btn-sm" onclick="location.href='take-quiz.html?id=${quiz.id}'">გახსნა →</button>
                <button type="button" class="btn-delete btn-sm" onclick="deleteQuiz(${quiz.id})">🗑️ წაშლა</button>
            </div>
        </div>
    `).join('');
}

function openQuiz(quizId) {
    let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];
    const quiz = userQuizzes.find(q => q.id === quizId);
    if (!quiz) return;

    document.getElementById('quizzesListSection').style.display = 'none';
    document.getElementById('singleQuizView').style.display = 'block';
    document.getElementById('activeQuizTitle').textContent = quiz.title;

    const qContainer = document.getElementById('activeQuizQuestions');
    qContainer.innerHTML = quiz.questions.map((q, qIndex) => `
        <div class="quiz-question-box" data-question-index="${qIndex}" style="margin-bottom: 20px; padding: 20px; border-radius: 12px; background: #fff; border: 1px solid var(--border-color);">
            <p><strong>კითხვა ${qIndex + 1}:</strong> ${q.question}</p>
            <div class="quiz-options" style="margin-top: 10px; display: flex; flex-direction: column; gap: 8px;">
                ${q.options.map((opt) => `
                    <label class="option-label" style="display: flex; align-items: center; gap: 10px; padding: 10px; border: 1px solid var(--border-color); border-radius: 8px; cursor: pointer;">
                        <input type="radio" name="question_${qIndex}" value="${opt}">
                        <span>${opt}</span>
                    </label>
                `).join('')}
            </div>
        </div>
    `).join('') + `
        <button type="button" id="submitAnswersBtn" class="btn-solid" onclick="checkQuizAnswers(${quiz.id})" style="margin-top: 20px;">პასუხების გადამოწმება 🎯</button>
        <div id="quizResultArea" style="margin-top: 20px; font-weight: bold; font-size: 1.2rem;"></div>
    `;
}

function checkQuizAnswers(quizId) {
    let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];
    const quiz = userQuizzes.find(q => q.id === quizId);
    if (!quiz) return;

    let score = 0;
    const totalQuestions = quiz.questions.length;

    quiz.questions.forEach((q, qIndex) => {
        const selectedOption = document.querySelector(`input[name="question_${qIndex}"]:checked`);
        const questionBox = document.querySelector(`[data-question-index="${qIndex}"]`);

        if (selectedOption) {
            const userChoice = selectedOption.value;
            if (userChoice === q.correctAnswer) {
                score++;
                questionBox.style.borderLeft = "5px solid #22c55e";
            } else {
                questionBox.style.borderLeft = "5px solid #ef4444";
            }
        } else {
            questionBox.style.borderLeft = "5px solid #eab308";
        }
    });

    const resultArea = document.getElementById('quizResultArea');
    resultArea.textContent = `შენი შედეგი: ${score} / ${totalQuestions} სწორი პასუხი (${Math.round((score/totalQuestions)*100)}%)`;
}

async function deleteQuiz(quizId) {
    if (!confirm('ნამდვილად გინდა ამ ქვიზის წაშლა?')) return;
    const token = localStorage.getItem('token');
    const res = await fetch(`http://localhost:8080/api/quizzes/${quizId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) {
        alert('წაშლა ვერ მოხერხდა');
        return;
    }
    renderQuizzesList(token);
}
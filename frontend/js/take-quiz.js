document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const quizId = urlParams.get("id");

    if (!quizId) {
        alert("ქვიზის ID ვერ მოიძებნა!");
        window.location.href = "quizzes.html";
        return;
    }

    const quizTitle = document.getElementById("quizTitle");
    const questionsContainer = document.getElementById("questionsContainer");
    const submitQuizForm = document.getElementById("submitQuizForm");
    const submitQuizBtn = document.getElementById("submitQuizBtn");
    const resultContainer = document.getElementById("resultContainer");

    let currentQuizData = null;

    // 1. ქვიზის დეტალების წამოღება GET /api/quizzes/{id}
    try {
        const response = await fetch(`http://localhost:8080/api/quizzes/${quizId}`, {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("ქვიზის ჩატვირთვა ვერ მოხერხდა");

        currentQuizData = await response.json();
        quizTitle.textContent = currentQuizData.title || "ქვიზი";

        if (!currentQuizData.questions || currentQuizData.questions.length === 0) {
            questionsContainer.innerHTML = `<p class="empty-text">კითხვები არ მოიძებნა ამ ქვიზში.</p>`;
            return;
        }

        // კითხვებისა და ვარიანტების რენდერი
        questionsContainer.innerHTML = "";
        currentQuizData.questions.forEach((q, qIndex) => {
            const questionBox = document.createElement("div");
            questionBox.className = "quiz-question-box";
            
            let optionsHtml = `<ul class="quiz-options">`;
            q.options.forEach((opt, optIndex) => {
                optionsHtml += `
                    <li>
                        <label class="option-label">
                            <input type="radio" name="question_${q.id}" value="${opt}" required>
                            <span>${opt}</span>
                        </label>
                    </li>
                `;
            });
            optionsHtml += `</ul>`;

            questionBox.innerHTML = `
                <p><strong>კითხვა ${qIndex + 1}:</strong> ${q.question}</p>
                ${optionsHtml}
            `;
            questionsContainer.appendChild(questionBox);
        });

        submitQuizBtn.style.display = "block";

        // თუ ტესტი უკვე გავლილია: ვაჩვენებთ წინა შედეგს და ფორმას ვმალავთ, სანამ "ხელახლა გავლას" არ დააჭერს
        const lastRes = await fetch(`http://localhost:8080/api/quizzes/${quizId}/last-attempt`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (lastRes.status === 200) {
            const last = await lastRes.json();
            questionsContainer.style.display = "none";
            submitQuizBtn.style.display = "none";
            resultContainer.innerHTML = `
                <div class="dashboard-card" style="text-align: center;">
                    <h3>ეს ტესტი უკვე გავლილი გაქვს</h3>
                    <p style="font-size: 18px; font-weight: bold;">ბოლო შედეგი: ${last.score} / ${last.totalQuestions} (${Math.round(last.percentage)}%)</p>
                    <p style="color: var(--text-muted);">${new Date(last.completedAt).toLocaleString('ka-GE')}</p>
                    <button type="button" id="retakeBtn" class="btn-solid" style="margin-top: 16px;">ხელახლა გავლა 🔄</button>
                </div>
            `;
            document.getElementById("retakeBtn").addEventListener("click", () => {
                resultContainer.innerHTML = "";
                questionsContainer.style.display = "block";
                submitQuizBtn.style.display = "block";
            });
        }

    } catch (error) {
        console.error(error);
        quizTitle.textContent = "შეცდომა ქვიზის ჩატვირთვისას";
    }

    // 2. პასუხების გაგზავნა POST /api/quizzes/{id}/submit
    submitQuizForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(submitQuizForm);
        const answers = {};
        
        currentQuizData.questions.forEach(q => {
            answers[q.id] = formData.get(`question_${q.id}`);
        });

        const submitPayload = { answers: answers };

        try {
            submitQuizBtn.disabled = true;
            submitQuizBtn.textContent = "მოწმდება...";

            const response = await fetch(`http://localhost:8080/api/quizzes/${quizId}/submit`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(submitPayload)
            });

            if (!response.ok) throw new Error("შედეგის გაგზავნა ვერ მოხერხდა");

            const result = await response.json();
            
            // შედეგების გამოტანა
            submitQuizBtn.style.display = "none";
            resultContainer.innerHTML = `
                <div class="dashboard-card" style="background: #f0fdf4; border-color: #bbf7d0; text-align: center;">
                    <h3 style="color: #16a34a; margin-bottom: 8px;">ტესტი დასრულდა! 🎉</h3>
                    <p style="font-size: 18px; font-weight: bold;">შენი ქულა: ${result.correctAnswers} / ${result.totalQuestions} (${Math.round(result.scorePercentage)}%)</p>
                    <p style="color: var(--text-muted); margin-top: 8px;">${result.message || ''}</p>
                    <button type="button" class="btn-solid" onclick="window.location.href='quizzes.html'" style="margin-top: 16px;">ქვიზების სიაში დაბრუნება</button>
                </div>
            `;

        } catch (error) {
            alert(error.message);
            submitQuizBtn.disabled = false;
            submitQuizBtn.textContent = "პასუხების გაგზავნა 🎯";
        }
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const quizForm = document.getElementById('quizForm');
    
    if (!quizForm) {
        console.error("შეცდომა: #quizForm ელემენტი ვერ მოიძებნა HTML-ში!");
        return;
    }

    quizForm.addEventListener('submit', async (e) => {
        // აუცილებლად აჩერებს გვერდის გადატვირთვას
        e.preventDefault(); 
        console.log("ფორმის გაგზავნა დაიწყო...");

        const titleInput = document.getElementById('materialTitle');
        const countSelect = document.getElementById('questionCount');
        const textInput = document.getElementById('materialText');
        const generateBtn = document.getElementById('generateBtn');

        const title = titleInput ? titleInput.value.trim() : '';
        const count = countSelect ? parseInt(countSelect.value, 10) : 2;
        const text = textInput ? textInput.value.trim() : '';

        if (!title || !text) {
            alert('გთხოვთ მიუთითოთ სათაური და ჩააკოპიროთ ტექსტი!');
            return;
        }

        if (generateBtn) {
            generateBtn.disabled = true;
            generateBtn.textContent = 'AI აანალიზებს და გენერირებს ქვიზს... ⏳';
        }

        try {
            console.log("იგზავნება მოთხოვნა სერვერზე...", { title, count, text });
            console.log("ლოკალურიდან წამოღებული ტოკენი:", token);
            const response = await fetch('http://localhost:8080/api/quizzes/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    subject: title,
                    questionCount: count,
                    materialContent: text
                })
            });

            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('token');
                window.location.href = 'login.html';
                return;
            }

            if (!response.ok) {
                throw new Error(`სერვერის შეცდომა: ${response.status}`);
            }

            const newQuizFromBackend = await response.json();
            console.log("მიღებულია პასუხი სერვერიდან:", newQuizFromBackend);

            // ვინახავთ localStorage-ში, რომ quizzes.html-მა დაინახოს
            let userQuizzes = JSON.parse(localStorage.getItem('userQuizzes')) || [];
            userQuizzes.push({
                id: newQuizFromBackend.id || Date.now(),
                title: newQuizFromBackend.title || title,
                date: new Date().toLocaleDateString('ka-GE'),
                questions: newQuizFromBackend.questions || []
            });
            localStorage.setItem('userQuizzes', JSON.stringify(userQuizzes));

            alert('ქვიზი წარმატებით დაგენერირდა!');
            window.location.href = 'quizzes.html';

        } catch (error) {
            console.error("გენერაციის შეცდომა:", error);
            alert('ვერ მოხერხდა სერვერთან დაკავშირება. იხილე კონსოლი (F12).');
        } finally {
            if (generateBtn) {
                generateBtn.disabled = false;
                generateBtn.textContent = 'ქვიზის გენერაცია 🚀';
            }
        }
    });
});
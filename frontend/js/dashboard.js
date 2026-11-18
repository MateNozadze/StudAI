document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const quizForm = document.getElementById('quizForm');
    
    if (!quizForm) {
        console.error("Error: #quizForm element not found in HTML!");
        return;
    }

    quizForm.addEventListener('submit', async (e) => {
        e.preventDefault(); 
        console.log("Quiz generation form submitted. Preparing to send request to backend...");

        const titleInput = document.getElementById('materialTitle');
        const countSelect = document.getElementById('questionCount');
        const textInput = document.getElementById('materialText');
        const generateBtn = document.getElementById('generateBtn');

        const title = titleInput ? titleInput.value.trim() : '';
        const count = countSelect ? parseInt(countSelect.value, 10) : 2;
        const text = textInput ? textInput.value.trim() : '';

        if (!title || !text) {
            alert('Please enter a title and paste the text!');
            return;
        }

        if (generateBtn) {
            generateBtn.disabled = true;
            generateBtn.textContent = 'Generating Quiz...';
        }

        try {
            console.log("Preparing to send request to backend...", { title, count, text });
            console.log("Retrieved token from local storage:", token);
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
                throw new Error(`Server error: ${response.status}`);
            }

            const newQuizFromBackend = await response.json();
            console.log("Received response from backend:", newQuizFromBackend);

            alert('Quiz generated successfully!');
            window.location.href = 'quizzes.html';

        } catch (error) {
            console.error("Generation error:", error);
            alert('Failed to connect to the server. Please check the console (F12).');
        } finally {
            if (generateBtn) {
                generateBtn.disabled = false;
                generateBtn.textContent = 'Generate Quiz';
            }
        }
    });
});
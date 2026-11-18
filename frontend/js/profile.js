document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const API = 'http://localhost:8080/api';
    const get = async (path) => {
        const res = await fetch(`${API}${path}`, { headers: { 'Authorization': `Bearer ${token}` } });
        if (res.status === 401 || res.status === 403) {
            localStorage.removeItem('token');
            window.location.href = 'login.html';
            throw new Error('unauthorized');
        }
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
    };
    const esc = (s) => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

    try {
        const [me, quizzes, attempts] = await Promise.all([
            get('/users/me'), get('/quizzes/all'), get('/quizzes/attempts')
        ]);

        document.getElementById('displayName').textContent = me.username;
        document.getElementById('displayEmail').textContent = me.email;
        document.getElementById('userAvatar').textContent = (me.username || 'U')[0].toUpperCase();
        document.getElementById('usernameInput').value = me.username;
        document.getElementById('emailInput').value = me.email;

        document.getElementById('statQuizzes').textContent = quizzes.length;
        document.getElementById('statMaterials').textContent = attempts.length;
        const avg = attempts.length
            ? Math.round(attempts.reduce((sum, a) => sum + a.percentage, 0) / attempts.length)
            : 0;
        document.getElementById('statAvgScore').textContent = `${avg}%`;

        const list = document.getElementById('historyList');
        list.innerHTML = attempts.length === 0
            ? '<p class="empty-text">You have not taken any quizzes yet.</p>'
            : attempts.map(a => `
                <div class="quiz-item-card">
                    <div class="quiz-card-content">
                        <h3>${esc(a.quizTitle)}</h3>
                        <p>${new Date(a.completedAt).toLocaleString('ka-GE')}</p>
                        <span class="quiz-badge">${a.score} / ${a.totalQuestions} (${Math.round(a.percentage)}%)</span>
                    </div>
                    <div class="quiz-card-actions">
                        <button type="button" class="btn-solid btn-sm" onclick="location.href='take-quiz.html?id=${a.quizId}'">open →</button>
                    </div>
                </div>`).join('');
    } catch (e) {
        console.error(e);
    }
});

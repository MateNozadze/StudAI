const API_URL = 'http://localhost:8080/api/auth';


document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const response = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        })
    });

    if (response.ok) {
        const data = await response.json();
        localStorage.setItem('token', data.token);
        window.location.href = 'index.html';
    } else {
        alert('wrong email or password');
    }
});


document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const password = document.getElementById('password').value;
    if (password !== document.getElementById('confirmPassword').value) {
        return alert('Passwords do not match!');
    }

    const response = await fetch(`${API_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            password: password
        })
    });

    if (response.ok) {
        alert('Registration successful!');
        window.location.href = 'login.html';
    } else {
        alert('Registration failed. Please try again.');
    }
});

document.querySelectorAll('.toggle-password').forEach(button => {
    button.addEventListener('click', () => {
        const targetId = button.getAttribute('data-target');
        const input = document.getElementById(targetId);
        if (!input) return;

        if (input.type === 'password') {
            input.type = 'text';
            button.innerHTML = eyeClosedSVG;
        } else {
            input.type = 'password';
            button.innerHTML = eyeOpenSVG;
        }
    });
});
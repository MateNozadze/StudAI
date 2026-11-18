document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const heroButtons = document.getElementById('heroButtons');

    if (heroButtons) {
        if (token) {
            heroButtons.innerHTML = `
                <a href="dashboard.html" class="btn-solid btn-lg">Go to Dashboard</a>
            `;
        } else {
            heroButtons.innerHTML = `
                <a href="register.html" class="btn-solid btn-lg">Get Started for Free</a>
                <a href="#features" class="btn-outline btn-lg">How It Works?</a>
            `;
        }
    }
});
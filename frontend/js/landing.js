document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const heroButtons = document.getElementById('heroButtons');

    if (heroButtons) {
        if (token) {
            // თუ მომხმარებელი შესულია
            heroButtons.innerHTML = `
                <a href="dashboard.html" class="btn-solid btn-lg">სამუშაო სივრცეში გადასვლა 🚀</a>
            `;
        } else {
            // თუ სტუმარია
            heroButtons.innerHTML = `
                <a href="register.html" class="btn-solid btn-lg">დაიწყე უფასოდ</a>
                <a href="#features" class="btn-outline btn-lg">როგორ მუშაობს?</a>
            `;
        }
    }
});
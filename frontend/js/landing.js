document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const navActions = document.getElementById('navActions');
    const heroButtons = document.getElementById('heroButtons');

    if (token && navActions && heroButtons) {
        navActions.innerHTML = `
            <a href="dashboard.html" class="btn-solid">სამუშაო სივრცე</a>
            <button type="button" id="logoutBtn" class="btn-outline">გასვლა</button>
        `;
        heroButtons.innerHTML = `
            <a href="dashboard.html" class="btn-solid btn-lg">სამუშაო სივრცეში გადასვლა</a>
        `;

        document.getElementById('logoutBtn')?.addEventListener('click', () => {
            localStorage.removeItem('token');
            window.location.reload();
        });
    }
}); 

// მაუსის მოძრაობის ანიმაცია ჰერო სექციაზე
const heroSection = document.querySelector('.hero-section');
if (heroSection) {
    heroSection.addEventListener('mousemove', (e) => {
        const rect = heroSection.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        // ვცვლით spotlight-ის პოზიციას მაუსის კოორდინატების მიხედვით
        heroSection.style.setProperty('--mouse-x', `${x}px`);
        heroSection.style.setProperty('--mouse-y', `${y}px`);
    });
}
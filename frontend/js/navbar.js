document.addEventListener("DOMContentLoaded", async () => {
    const headerPlaceholder = document.getElementById("header-placeholder");
    if (!headerPlaceholder) return;

    try {
        const response = await fetch("components/header.html");
        if (!response.ok) throw new Error("Header-ის ჩატვირთვა ვერ მოხერხდა");
        
        headerPlaceholder.innerHTML = await response.text();

        const token = localStorage.getItem("token");
        const authNav = document.getElementById("authNav");
        const guestNav = document.getElementById("guestNav");

        if (token) {
            authNav.style.display = "flex";
            guestNav.style.display = "none";
        } else {
            authNav.style.display = "none";
            guestNav.style.display = "flex";
        }

        const currentPage = window.location.pathname.split("/").pop() || "index.html";
        const navLinks = headerPlaceholder.querySelectorAll("a[data-page]");
        
        navLinks.forEach(link => {
            if (link.getAttribute("data-page") === currentPage) {
                link.classList.remove("btn-outline");
                link.classList.add("btn-solid");
            }
        });

        const logoutBtn = document.getElementById("logoutBtn");
        if (logoutBtn) {
            logoutBtn.addEventListener("click", () => {
                localStorage.clear();
                window.location.href = "login.html";
            });
        }

    } catch (error) {
        console.error("Navbar error:", error);
    }
});
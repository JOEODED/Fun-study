const API_BASE = "https://fun-study-backend.onrender.com/api";

document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const errorMsg = document.getElementById("errorMsg");
  errorMsg.textContent = "";

  try {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      const message = await response.text();
      errorMsg.textContent = message || "Login failed.";
      return;
    }

    const user = await response.json();
    localStorage.setItem("userEmail", user.email);
    window.location.href = "dashboard.html"; // we'll build this next
  } catch (err) {
    errorMsg.textContent = "Could not reach the server. Is the backend running?";
  }
});

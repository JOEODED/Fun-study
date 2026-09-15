const API_BASE = "https://fun-study-backend.onrender.com/api";

document.getElementById("registerForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const name = document.getElementById("name").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const errorMsg = document.getElementById("errorMsg");
  errorMsg.textContent = "";

  try {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password })
    });

    if (!response.ok) {
      const message = await response.text();
      errorMsg.textContent = message || "Could not create account.";
      return;
    }

    const user = await response.json();
    localStorage.setItem("userEmail", user.email);
    window.location.href = "dashboard.html";
  } catch (err) {
    errorMsg.textContent = "Could not reach the server. Is the backend running?";
  }
});

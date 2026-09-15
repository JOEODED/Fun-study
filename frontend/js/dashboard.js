const API_BASE = "https://fun-study-backend.onrender.com/api";
const userEmail = localStorage.getItem("userEmail");

if (!userEmail) {
  window.location.href = "index.html";
}

document.getElementById("userEmail").textContent = userEmail;

document.getElementById("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem("userEmail");
  window.location.href = "index.html";
});

async function loadDashboard() {
  try {
    const response = await fetch(`${API_BASE}/dashboard`, {
      headers: { "X-User-Email": userEmail }
    });

    if (!response.ok) {
      console.error("Dashboard fetch failed:", await response.text());
      return;
    }

    const stats = await response.json();

    document.getElementById("totalCourses").textContent = stats.totalCourses;
    document.getElementById("pendingAssignments").textContent = stats.pendingAssignments;
    document.getElementById("completedAssignments").textContent = stats.completedAssignments;
    document.getElementById("upcomingDeadlines").textContent = stats.upcomingDeadlines;
    document.getElementById("totalStudyHours").textContent = stats.totalStudyHours;

    const progress = stats.overallProgress;
    document.getElementById("overallProgress").textContent = progress + "%";

    const ring = document.getElementById("progressRing");
    const circumference = 327; // 2 * π * 52 (matches the SVG circle radius)
    const offset = circumference - (progress / 100) * circumference;
    ring.style.strokeDashoffset = offset;

  } catch (err) {
    console.error("Could not reach server:", err);
  }
}

loadDashboard();

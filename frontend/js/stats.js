const API_BASE = "http://localhost:8080/api";
const userEmail = localStorage.getItem("userEmail");

if (!userEmail) window.location.href = "index.html";
document.getElementById("userEmail").textContent = userEmail;

document.getElementById("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem("userEmail");
  window.location.href = "index.html";
});

const headers = { "X-User-Email": userEmail };
const chartEl = document.getElementById("weeklyChart");

async function loadStats() {
  try {
    const res = await fetch(`${API_BASE}/dashboard`, { headers });
    if (!res.ok) {
      chartEl.innerHTML = `<p class="empty-row">Could not load statistics.</p>`;
      return;
    }
    const data = await res.json();

    document.getElementById("statCourses").textContent = data.totalCourses ?? "-";
    document.getElementById("statPending").textContent = data.pendingAssignments ?? "-";
    document.getElementById("statCompleted").textContent = data.completedAssignments ?? "-";
    document.getElementById("statMinutes").textContent =
      data.totalStudyHours !== undefined ? `${data.totalStudyHours} hrs` : "-";

    // The backend doesn't currently return a day-by-day breakdown,
    // so we show an honest empty state rather than fake zero bars.
    chartEl.innerHTML = `<p class="empty-row">Daily breakdown isn't available yet — only totals are tracked right now.</p>`;
  } catch (err) {
    chartEl.innerHTML = `<p class="empty-row">Could not reach the server. Is the backend running?</p>`;
  }
}

loadStats();


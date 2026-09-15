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
    document.getElementById("statMinutes").textContent = data.totalStudyMinutes ?? "-";

    renderChart(data.weeklyStudyMinutes || {});
  } catch (err) {
    chartEl.innerHTML = `<p class="empty-row">Could not reach the server. Is the backend running?</p>`;
  }
}

function renderChart(weekData) {
  const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
  const values = days.map(d => weekData[d] || 0);
  const max = Math.max(...values, 1);

  chartEl.innerHTML = days.map((day, i) => {
    const heightPct = Math.round((values[i] / max) * 100);
    return `
      <div class="bar-col">
        <span class="bar-value">${values[i]}</span>
        <div class="bar" style="height: ${heightPct}%;"></div>
        <span class="bar-label">${day}</span>
      </div>
    `;
  }).join("");
}

loadStats();

const API_BASE = "https://fun-study-backend.onrender.com/api";
const userEmail = localStorage.getItem("userEmail");

if (!userEmail) window.location.href = "index.html";
document.getElementById("userEmail").textContent = userEmail;

document.getElementById("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem("userEmail");
  window.location.href = "index.html";
});

const headers = {
  "Content-Type": "application/json",
  "X-User-Email": userEmail
};

const STUDY_SECONDS = 25 * 60;
const BREAK_SECONDS = 5 * 60;
const RING_CIRCUMFERENCE = 615.75; // 2 * π * 98 (matches the SVG circle radius)

const timerCard = document.getElementById("timerCard");
const timerBadge = document.getElementById("timerBadge");
const timerDisplay = document.getElementById("timerDisplay");
const timerSub = document.getElementById("timerSub");
const ringProgress = document.getElementById("ringProgress");
const startBtn = document.getElementById("startBtn");
const pauseBtn = document.getElementById("pauseBtn");
const resetBtn = document.getElementById("resetBtn");
const courseSelect = document.getElementById("courseSelect");
const sessionsTodayEl = document.getElementById("sessionsToday");
const totalMinutesEl = document.getElementById("totalMinutes");

let secondsLeft = STUDY_SECONDS;
let totalSecondsForMode = STUDY_SECONDS;
let isStudyMode = true;
let intervalId = null;
let sessionsToday = 0;

function formatTime(totalSeconds) {
  const m = Math.floor(totalSeconds / 60).toString().padStart(2, "0");
  const s = (totalSeconds % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
}

function updateDisplay() {
  timerDisplay.textContent = formatTime(secondsLeft);
  timerBadge.textContent = isStudyMode ? "Study Session" : "Break";
  timerCard.classList.toggle("mode-break", !isStudyMode);

  if (intervalId) {
    timerSub.textContent = isStudyMode ? "Stay focused..." : "Relax for a bit";
  } else if (secondsLeft === totalSecondsForMode) {
    timerSub.textContent = "Ready when you are";
  } else {
    timerSub.textContent = "Paused";
  }

  const elapsed = totalSecondsForMode - secondsLeft;
  const progress = elapsed / totalSecondsForMode;
  const offset = RING_CIRCUMFERENCE * (1 - progress);
  ringProgress.style.strokeDashoffset = offset;
}

function tick() {
  secondsLeft--;
  updateDisplay();

  if (secondsLeft <= 0) {
    clearInterval(intervalId);
    intervalId = null;

    if (isStudyMode) {
      logCompletedSession(25);
      sessionsToday++;
      sessionsTodayEl.textContent = sessionsToday;
      alert("Study session complete! Time for a break.");
    } else {
      alert("Break's over! Ready for another study session?");
    }

    isStudyMode = !isStudyMode;
    totalSecondsForMode = isStudyMode ? STUDY_SECONDS : BREAK_SECONDS;
    secondsLeft = totalSecondsForMode;
    updateDisplay();
    startBtn.disabled = false;
    pauseBtn.disabled = true;
  }
}

startBtn.addEventListener("click", () => {
  if (intervalId) return;
  intervalId = setInterval(tick, 1000);
  startBtn.disabled = true;
  pauseBtn.disabled = false;
  updateDisplay();
});

pauseBtn.addEventListener("click", () => {
  clearInterval(intervalId);
  intervalId = null;
  startBtn.disabled = false;
  pauseBtn.disabled = true;
  updateDisplay();
});

resetBtn.addEventListener("click", () => {
  clearInterval(intervalId);
  intervalId = null;
  isStudyMode = true;
  totalSecondsForMode = STUDY_SECONDS;
  secondsLeft = STUDY_SECONDS;
  updateDisplay();
  startBtn.disabled = false;
  pauseBtn.disabled = true;
});

async function logCompletedSession(durationMinutes) {
  const courseId = courseSelect.value ? parseInt(courseSelect.value) : null;
  try {
    await fetch(`${API_BASE}/sessions`, {
      method: "POST",
      headers,
      body: JSON.stringify({ durationMinutes, courseId })
    });
    loadTotalMinutes();
  } catch (err) {
    console.error("Could not log session:", err);
  }
}

async function loadCourses() {
  try {
    const res = await fetch(`${API_BASE}/courses`, { headers });
    const courses = await res.json();
    courseSelect.innerHTML = `<option value="">No specific course</option>` +
      courses.map(c => `<option value="${c.id}">${c.courseCode} — ${c.courseName}</option>`).join("");
  } catch (err) {
    console.error("Could not load courses:", err);
  }
}

async function loadTotalMinutes() {
  try {
    const res = await fetch(`${API_BASE}/sessions/total-minutes`, { headers });
    const data = await res.json();
    totalMinutesEl.textContent = data.totalMinutes;
  } catch (err) {
    totalMinutesEl.textContent = "-";
  }
}

updateDisplay();
loadCourses();
loadTotalMinutes();

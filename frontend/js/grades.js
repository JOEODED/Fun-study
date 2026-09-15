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

const tbody = document.getElementById("gradesBody");
const modal = document.getElementById("gradeModal");
const form = document.getElementById("gradeForm");
const courseSelect = document.getElementById("courseSelect");
const gpaValue = document.getElementById("gpaValue");

async function loadCourses() {
  try {
    const res = await fetch(`${API_BASE}/courses`, { headers });
    const courses = await res.json();
    courseSelect.innerHTML = `<option value="">Select a course...</option>` +
      courses.map(c => `<option value="${c.id}">${c.courseCode} — ${c.courseName}</option>`).join("");
  } catch (err) {
    console.error("Could not load courses:", err);
  }
}

async function loadGrades() {
  try {
    const res = await fetch(`${API_BASE}/grades`, { headers });
    const grades = await res.json();

    if (grades.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="empty-row">No grades yet — click "Add Grade" to get started.</td></tr>`;
    } else {
      tbody.innerHTML = grades.map(g => `
        <tr>
          <td>${g.course ? g.course.courseCode : "-"}</td>
          <td>${g.assignmentScore}</td>
          <td>${g.testScore}</td>
          <td>${g.examScore}</td>
          <td>${g.totalScore.toFixed(1)}</td>
          <td>${g.letterGrade || "-"}</td>
        </tr>
      `).join("");
    }

    loadGPA();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-row">Could not load grades. Is the backend running?</td></tr>`;
  }
}

async function loadGPA() {
  try {
    const res = await fetch(`${API_BASE}/grades/gpa`, { headers });
    const data = await res.json();
    gpaValue.textContent = data.gpa.toFixed(2);
  } catch (err) {
    gpaValue.textContent = "-";
  }
}

function openAdd() {
  form.reset();
  modal.classList.remove("hidden");
}

function closeModal() {
  modal.classList.add("hidden");
}

document.getElementById("addGradeBtn").addEventListener("click", openAdd);
document.getElementById("cancelBtn").addEventListener("click", closeModal);

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const payload = {
    courseId: parseInt(courseSelect.value),
    assignmentScore: parseFloat(document.getElementById("assignmentScore").value),
    testScore: parseFloat(document.getElementById("testScore").value),
    examScore: parseFloat(document.getElementById("examScore").value)
  };

  try {
    const res = await fetch(`${API_BASE}/grades`, { method: "POST", headers, body: JSON.stringify(payload) });
    if (!res.ok) {
      alert(await res.text());
      return;
    }
    closeModal();
    loadGrades();
  } catch (err) {
    alert("Could not reach the server.");
  }
});

loadCourses().then(loadGrades);

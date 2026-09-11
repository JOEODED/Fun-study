const API_BASE = "http://localhost:8080/api";
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

const tbody = document.getElementById("coursesBody");
const modal = document.getElementById("courseModal");
const form = document.getElementById("courseForm");
const modalTitle = document.getElementById("modalTitle");

async function loadCourses() {
  try {
    const res = await fetch(`${API_BASE}/courses`, { headers });
    const courses = await res.json();

    if (courses.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="empty-row">No courses yet — click "Add Course" to get started.</td></tr>`;
      return;
    }

    tbody.innerHTML = courses.map(c => `
      <tr>
        <td>${c.courseCode}</td>
        <td>${c.courseName}</td>
        <td>${c.lecturer || "-"}</td>
        <td>${c.creditUnits}</td>
        <td>${c.semester || "-"}</td>
        <td class="row-actions">
          <button onclick="openEdit(${c.id}, '${c.courseCode}', '${c.courseName}', '${c.lecturer || ""}', ${c.creditUnits}, '${c.semester || ""}')">Edit</button>
          <button class="delete-btn" onclick="deleteCourse(${c.id})">Delete</button>
        </td>
      </tr>
    `).join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-row">Could not load courses. Is the backend running?</td></tr>`;
  }
}

function openAdd() {
  modalTitle.textContent = "Add Course";
  form.reset();
  document.getElementById("courseId").value = "";
  modal.classList.remove("hidden");
}

function openEdit(id, code, name, lecturer, units, semester) {
  modalTitle.textContent = "Edit Course";
  document.getElementById("courseId").value = id;
  document.getElementById("courseCode").value = code;
  document.getElementById("courseName").value = name;
  document.getElementById("lecturer").value = lecturer;
  document.getElementById("creditUnits").value = units;
  document.getElementById("semester").value = semester;
  modal.classList.remove("hidden");
}

function closeModal() {
  modal.classList.add("hidden");
}

document.getElementById("addCourseBtn").addEventListener("click", openAdd);
document.getElementById("cancelBtn").addEventListener("click", closeModal);

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("courseId").value;
  const payload = {
    courseCode: document.getElementById("courseCode").value,
    courseName: document.getElementById("courseName").value,
    lecturer: document.getElementById("lecturer").value,
    creditUnits: parseInt(document.getElementById("creditUnits").value),
    semester: document.getElementById("semester").value
  };

  const url = id ? `${API_BASE}/courses/${id}` : `${API_BASE}/courses`;
  const method = id ? "PUT" : "POST";

  try {
    const res = await fetch(url, { method, headers, body: JSON.stringify(payload) });
    if (!res.ok) {
      alert(await res.text());
      return;
    }
    closeModal();
    loadCourses();
  } catch (err) {
    alert("Could not reach the server.");
  }
});

async function deleteCourse(id) {
  if (!confirm("Delete this course? This cannot be undone.")) return;
  try {
    await fetch(`${API_BASE}/courses/${id}`, { method: "DELETE", headers });
    loadCourses();
  } catch (err) {
    alert("Could not reach the server.");
  }
}

loadCourses();
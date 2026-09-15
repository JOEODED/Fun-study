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

const tbody = document.getElementById("assignmentsBody");
const modal = document.getElementById("assignmentModal");
const form = document.getElementById("assignmentForm");
const modalTitle = document.getElementById("modalTitle");
const courseSelect = document.getElementById("courseSelect");

let coursesCache = [];

// ---- Load courses (for the dropdown) ----

async function loadCourses() {
  try {
    const res = await fetch(`${API_BASE}/courses`, { headers });
    coursesCache = await res.json();

    courseSelect.innerHTML = `<option value="">Select a course...</option>` +
      coursesCache.map(c => `<option value="${c.id}">${c.courseCode} — ${c.courseName}</option>`).join("");
  } catch (err) {
    console.error("Could not load courses:", err);
  }
}

// ---- Load and render assignments ----

const statusFilter = document.getElementById("statusFilter");
if (statusFilter) {
  statusFilter.addEventListener("change", () => loadAssignments(statusFilter.value));
}

async function loadAssignments(status) {
  try {
    const url = status
      ? `${API_BASE}/search/assignments?status=${encodeURIComponent(status)}`
      : `${API_BASE}/assignments`;
    const res = await fetch(url, { headers });
    const assignments = await res.json();

    if (assignments.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="empty-row">${status ? "No assignments match this filter." : "No assignments yet — click \"Add Assignment\" to get started."}</td></tr>`;
      return;
    }

    tbody.innerHTML = assignments.map(a => `
      <tr>
        <td>${a.title}</td>
        <td>${a.course ? a.course.courseCode : "-"}</td>
        <td>${a.deadline || "-"}</td>
        <td>${a.priority || "-"}</td>
        <td>${a.status || "-"}</td>
        <td class="row-actions">
          ${a.status !== "COMPLETED" ? `<button onclick="markComplete(${a.id})">Complete</button>` : ""}
          <button onclick='openEdit(${JSON.stringify(a)})'>Edit</button>
          <button class="delete-btn" onclick="deleteAssignment(${a.id})">Delete</button>
        </td>
      </tr>
    `).join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-row">Could not load assignments. Is the backend running?</td></tr>`;
  }
}

// ---- Modal open/close ----

function openAdd() {
  modalTitle.textContent = "Add Assignment";
  form.reset();
  document.getElementById("assignmentId").value = "";
  modal.classList.remove("hidden");
}

function openEdit(a) {
  modalTitle.textContent = "Edit Assignment";
  document.getElementById("assignmentId").value = a.id;
  document.getElementById("title").value = a.title;
  document.getElementById("description").value = a.description || "";
  courseSelect.value = a.course ? a.course.id : "";
  document.getElementById("deadline").value = a.deadline || "";
  document.getElementById("priority").value = a.priority || "MEDIUM";
  document.getElementById("status").value = a.status || "PENDING";
  modal.classList.remove("hidden");
}

function closeModal() {
  modal.classList.add("hidden");
}

document.getElementById("addAssignmentBtn").addEventListener("click", openAdd);
document.getElementById("cancelBtn").addEventListener("click", closeModal);

// ---- Create / Update ----

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("assignmentId").value;
  const courseId = courseSelect.value;

  const payload = {
    title: document.getElementById("title").value,
    description: document.getElementById("description").value,
    deadline: document.getElementById("deadline").value || null,
    priority: document.getElementById("priority").value,
    status: document.getElementById("status").value
  };

  const isEdit = id !== "";
  const url = isEdit
    ? `${API_BASE}/assignments/${id}`
    : `${API_BASE}/assignments?courseId=${courseId}`;
  const method = isEdit ? "PUT" : "POST";

  try {
    const res = await fetch(url, { method, headers, body: JSON.stringify(payload) });
    if (!res.ok) {
      alert(await res.text());
      return;
    }
    closeModal();
    loadAssignments();
  } catch (err) {
    alert("Could not reach the server.");
  }
});

// ---- Mark Complete ----

async function markComplete(id) {
  try {
    const res = await fetch(`${API_BASE}/assignments/${id}/complete`, {
      method: "PATCH",
      headers
    });
    if (!res.ok) {
      alert(await res.text());
      return;
    }
    loadAssignments();
  } catch (err) {
    alert("Could not reach the server.");
  }
}

// ---- Delete ----

async function deleteAssignment(id) {
  if (!confirm("Delete this assignment? This cannot be undone.")) return;
  try {
    await fetch(`${API_BASE}/assignments/${id}`, { method: "DELETE", headers });
    loadAssignments();
  } catch (err) {
    alert("Could not reach the server.");
  }
}

// ---- Init ----

loadCourses().then(loadAssignments);

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

const tbody = document.getElementById("tasksBody");
const modal = document.getElementById("taskModal");
const form = document.getElementById("taskForm");
const modalTitle = document.getElementById("modalTitle");

const statusFilter = document.getElementById("statusFilter");
if (statusFilter) {
  statusFilter.addEventListener("change", () => loadTasks(statusFilter.value));
}

async function loadTasks(status) {
  try {
    const url = status
      ? `${API_BASE}/search/tasks?status=${encodeURIComponent(status)}`
      : `${API_BASE}/tasks`;
    const res = await fetch(url, { headers });
    const tasks = await res.json();

    if (tasks.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="empty-row">${status ? "No tasks match this filter." : "No tasks yet — click \"Add Task\" to get started."}</td></tr>`;
      return;
    }

    tbody.innerHTML = tasks.map(t => `
      <tr>
        <td>${t.title}</td>
        <td>${t.deadline || "-"}</td>
        <td>${t.priority || "-"}</td>
        <td>${t.status || "-"}</td>
        <td class="row-actions">
          ${t.status !== "COMPLETED" ? `<button onclick="markComplete(${t.id})">Complete</button>` : ""}
          <button onclick='openEdit(${JSON.stringify(t)})'>Edit</button>
          <button class="delete-btn" onclick="deleteTask(${t.id})">Delete</button>
        </td>
      </tr>
    `).join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-row">Could not load tasks. Is the backend running?</td></tr>`;
  }
}

function openAdd() {
  modalTitle.textContent = "Add Task";
  form.reset();
  document.getElementById("taskId").value = "";
  modal.classList.remove("hidden");
}

function openEdit(t) {
  modalTitle.textContent = "Edit Task";
  document.getElementById("taskId").value = t.id;
  document.getElementById("title").value = t.title;
  document.getElementById("description").value = t.description || "";
  document.getElementById("deadline").value = t.deadline || "";
  document.getElementById("priority").value = t.priority || "MEDIUM";
  document.getElementById("status").value = t.status || "PENDING";
  modal.classList.remove("hidden");
}

function closeModal() {
  modal.classList.add("hidden");
}

document.getElementById("addTaskBtn").addEventListener("click", openAdd);
document.getElementById("cancelBtn").addEventListener("click", closeModal);

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("taskId").value;

  const payload = {
    title: document.getElementById("title").value,
    description: document.getElementById("description").value,
    deadline: document.getElementById("deadline").value || null,
    priority: document.getElementById("priority").value,
    status: document.getElementById("status").value
  };

  const isEdit = id !== "";
  const url = isEdit ? `${API_BASE}/tasks/${id}` : `${API_BASE}/tasks`;
  const method = isEdit ? "PUT" : "POST";

  try {
    const res = await fetch(url, { method, headers, body: JSON.stringify(payload) });
    if (!res.ok) {
      alert(await res.text());
      return;
    }
    closeModal();
    loadTasks();
  } catch (err) {
    alert("Could not reach the server.");
  }
});

async function markComplete(id) {
  try {
    const res = await fetch(`${API_BASE}/tasks/${id}/complete`, { method: "PATCH", headers });
    if (!res.ok) {
      alert(await res.text());
      return;
    }
    loadTasks();
  } catch (err) {
    alert("Could not reach the server.");
  }
}

async function deleteTask(id) {
  if (!confirm("Delete this task? This cannot be undone.")) return;
  try {
    await fetch(`${API_BASE}/tasks/${id}`, { method: "DELETE", headers });
    loadTasks();
  } catch (err) {
    alert("Could not reach the server.");
  }
}

loadTasks();

// Configure before this script loads with: window.COREUP_API_BASE = "http://localhost:8080/api";
const defaultApiBase = window.location.protocol === "file:" ? "http://localhost:8080/api" : "/api";
const API_BASE = (window.COREUP_API_BASE || defaultApiBase).replace(/\/+$/, "");

const state = {
  users: [],
  selectedUserId: null,
  exercises: [],
  predefinedPlans: [],
  selectedPredefinedPlan: 0,
  search: "",
};

const elements = {
  studentList: document.querySelector("#studentList"),
  workoutContent: document.querySelector("#workoutContent"),
  totalUsers: document.querySelector("#totalUsers"),
  totalExercises: document.querySelector("#totalExercises"),
  userCount: document.querySelector("#userCount"),
  searchInput: document.querySelector("#searchInput"),
  apiNotice: document.querySelector("#apiNotice"),
  apiNoticeMessage: document.querySelector("#apiNoticeMessage"),
  connectionDot: document.querySelector("#connectionDot"),
  connectionLabel: document.querySelector("#connectionLabel"),
  connectionUrl: document.querySelector("#connectionUrl"),
  addExerciseButton: document.querySelector("#addExerciseButton"),
  presetTabs: document.querySelector("#presetTabs"),
  presetContent: document.querySelector("#presetContent"),
};

const goalLabels = {
  EMAGRECER: "Emagrecer",
  GANHAR_MASSA: "Ganhar massa",
  SAUDE_GERAL: "Saúde geral",
};

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (character) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;",
  })[character]);
}

function initials(name) {
  return String(name || "?")
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join("");
}

function formatGoal(goal) {
  return goalLabels[goal] || goal || "Sem objetivo";
}

function setConnection(online, message = "") {
  elements.connectionDot.className = `connection-dot ${online ? "online" : "offline"}`;
  elements.connectionLabel.textContent = online ? "API conectada" : "API indisponível";
  elements.connectionUrl.textContent = API_BASE;
  elements.apiNotice.classList.toggle("hidden", online);
  if (!online && message) elements.apiNoticeMessage.textContent = message;
}

async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers: {
        Accept: "application/json",
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...options.headers,
      },
    });
  } catch {
    throw new Error(`Não foi possível acessar ${API_BASE}. Verifique o endereço e se a API está em execução.`);
  }

  if (!response.ok) {
    let detail = "";
    try {
      const body = await response.json();
      detail = body.message || body.error || "";
    } catch {
      // Some server errors do not include a JSON response body.
    }
    throw new Error(detail || `A API respondeu com erro (${response.status}).`);
  }

  if (response.status === 204) return null;
  const contentType = response.headers.get("content-type") || "";
  return contentType.includes("application/json") ? response.json() : null;
}

function showApiError(error) {
  setConnection(false, error.message);
  elements.studentList.innerHTML = `<div class="empty-list">Não foi possível carregar os alunos.</div>`;
  elements.totalUsers.textContent = "—";
  elements.totalExercises.textContent = "—";
  elements.userCount.textContent = "0";
}

async function loadUsers() {
  elements.connectionLabel.textContent = "Verificando API";
  elements.connectionDot.className = "connection-dot";
  elements.apiNotice.classList.add("hidden");
  elements.studentList.innerHTML = '<div class="loading-state"><span class="spinner"></span> Carregando alunos...</div>';

  try {
    const users = await request("/usuarios");
    if (!Array.isArray(users)) throw new Error("A resposta de /usuarios precisa ser uma lista JSON.");
    state.users = users;
    setConnection(true);
    if (!state.users.some((user) => String(user.id) === String(state.selectedUserId))) {
      state.selectedUserId = state.users.length ? state.users[0].id : null;
    }
    renderUsers();
    if (state.selectedUserId !== null) await loadExercises(state.selectedUserId);
    else renderEmptyWorkout();
  } catch (error) {
    state.users = [];
    state.selectedUserId = null;
    state.exercises = [];
    showApiError(error);
    renderEmptyWorkout();
  }
}

function visibleUsers() {
  const needle = state.search.trim().toLocaleLowerCase("pt-BR");
  return state.users.filter((user) => String(user.nome || "").toLocaleLowerCase("pt-BR").includes(needle));
}

function renderUsers() {
  const users = visibleUsers();
  elements.userCount.textContent = String(state.users.length);
  elements.totalUsers.textContent = String(state.users.length).padStart(2, "0");

  if (!users.length) {
    elements.studentList.innerHTML = `<div class="empty-list">${state.users.length ? "Nenhum aluno encontrado." : "Cadastre o primeiro aluno para começar."}</div>`;
    return;
  }

  elements.studentList.innerHTML = users.map((user, index) => {
    const selected = String(user.id) === String(state.selectedUserId);
    const colorClass = `color-${(index % 4) + 1}`;
    return `
      <div class="student-row${selected ? " selected" : ""}">
        <button class="student-select" type="button" data-user-id="${escapeHtml(user.id)}">
          <span class="avatar avatar-student ${colorClass}" aria-hidden="true">${escapeHtml(initials(user.nome))}</span>
          <span class="student-info">
            <span class="student-name">${escapeHtml(user.nome)}</span>
            <span class="student-meta">${escapeHtml(user.idade)} anos${user.peso ? ` · ${escapeHtml(user.peso)} kg` : ""}</span>
          </span>
          <span class="student-goal">${escapeHtml(formatGoal(user.objetivo))}</span>
          <span class="row-chevron" aria-hidden="true">›</span>
        </button>
        <button class="remove-user" type="button" data-delete-user-id="${escapeHtml(user.id)}"
          aria-label="Remover aluno ${escapeHtml(user.nome)}" title="Remover aluno">×</button>
      </div>`;
  }).join("");
}

async function loadExercises(userId) {
  elements.addExerciseButton.disabled = false;
  elements.workoutContent.innerHTML = '<div class="loading-state"><span class="spinner"></span> Carregando treino...</div>';
  try {
    const exercises = await request(`/usuarios/${encodeURIComponent(userId)}/exercicios`);
    if (!Array.isArray(exercises)) throw new Error("A resposta de exercícios precisa ser uma lista JSON.");
    state.exercises = exercises;
    setConnection(true);
    renderWorkout();
  } catch (error) {
    state.exercises = [];
    elements.workoutContent.innerHTML = `<div class="empty-state"><span class="empty-illustration" aria-hidden="true">!</span><strong>Falha ao carregar o treino</strong><p>${escapeHtml(error.message)}</p></div>`;
    setConnection(false, error.message);
  }
  elements.totalExercises.textContent = String(state.exercises.length).padStart(2, "0");
}

function renderEmptyWorkout() {
  elements.addExerciseButton.disabled = true;
  elements.totalExercises.textContent = "00";
  elements.workoutContent.innerHTML = `
    <div class="empty-state">
      <span class="empty-illustration" aria-hidden="true">↗</span>
      <strong>${state.users.length ? "Escolha um aluno" : "Sua jornada começa aqui"}</strong>
      <p>${state.users.length ? "Selecione alguém da lista para visualizar e montar um treino personalizado." : "Cadastre um aluno para organizar os primeiros treinos."}</p>
    </div>`;
}

async function loadPredefinedPlans() {
  try {
    const plans = await request("/treinos/predefinidos");
    if (!Array.isArray(plans)) throw new Error("A resposta de treinos predefinidos precisa ser uma lista JSON.");
    state.predefinedPlans = plans;
    state.selectedPredefinedPlan = 0;
    renderPredefinedPlans();
  } catch (error) {
    elements.presetTabs.innerHTML = "";
    elements.presetContent.innerHTML = `<div class="empty-list">${escapeHtml(error.message)}</div>`;
  }
}

function renderPredefinedPlans() {
  const plan = state.predefinedPlans[state.selectedPredefinedPlan];
  if (!plan) {
    elements.presetTabs.innerHTML = "";
    elements.presetContent.innerHTML = '<div class="empty-list">Nenhum treino predefinido disponível.</div>';
    return;
  }

  elements.presetTabs.innerHTML = state.predefinedPlans.map((item, index) => `
    <button class="preset-tab${index === state.selectedPredefinedPlan ? " active" : ""}" type="button"
      role="tab" aria-selected="${index === state.selectedPredefinedPlan}" data-plan-index="${index}">
      ${escapeHtml(item.nome)}
    </button>`).join("");
  const exercisesMarkup = (plan.exercicios || []).map((exercise) => `
    <div class="exercise-card">
      <span class="exercise-type-icon" aria-hidden="true">↗</span>
      <span class="exercise-info">
        <span class="exercise-name">${escapeHtml(exercise.nome)}</span>
        <span class="exercise-detail">${escapeHtml(exercise.grupoMuscular || "Força")} · ${escapeHtml(exercise.series ?? "—")} × ${escapeHtml(exercise.repeticoes ?? "—")}</span>
      </span>
    </div>`).join("");
  elements.presetContent.innerHTML = `
    <div class="preset-summary"><strong>${escapeHtml(plan.nome)}</strong><span>${(plan.exercicios || []).length} exercícios</span></div>
    <div class="preset-exercise-list">${exercisesMarkup}</div>`;
}

function renderWorkout() {
  const user = state.users.find((item) => String(item.id) === String(state.selectedUserId));
  if (!user) {
    renderEmptyWorkout();
    return;
  }

  const heightInMeters = Number(user.altura) / 100;
  const bmi = Number(user.peso) > 0 && heightInMeters > 0
    ? (Number(user.peso) / (heightInMeters * heightInMeters)).toFixed(1).replace(".", ",")
    : null;
  const exercisesMarkup = state.exercises.length
    ? state.exercises.map((exercise) => {
      const cardio = String(exercise.tipo || "").toUpperCase() === "CARDIO";
      const detail = cardio
        ? `${escapeHtml(exercise.duracaoMinutos ?? "—")} min`
        : `${escapeHtml(exercise.grupoMuscular || "Força")} · ${escapeHtml(exercise.series ?? "—")} × ${escapeHtml(exercise.repeticoes ?? "—")}`;
      return `
        <div class="exercise-card">
          <span class="exercise-type-icon${cardio ? " cardio" : ""}" aria-hidden="true">${cardio ? "⌁" : "↗"}</span>
          <span class="exercise-info">
            <span class="exercise-name">${escapeHtml(exercise.nome)}</span>
            <span class="exercise-detail">${detail}</span>
          </span>
          <button class="remove-exercise" type="button" data-exercise-id="${escapeHtml(exercise.id)}" aria-label="Remover ${escapeHtml(exercise.nome)}" title="Remover exercício">×</button>
        </div>`;
    }).join("")
    : '<div class="no-exercises">Nenhum exercício neste treino. Adicione o primeiro!</div>';

  elements.workoutContent.innerHTML = `
    <div class="selected-profile">
      <span class="avatar" aria-hidden="true">${escapeHtml(initials(user.nome))}</span>
      <span>
        <span class="profile-name">${escapeHtml(user.nome)}</span>
        <span class="profile-detail">${escapeHtml(user.idade)} anos · ${escapeHtml(formatGoal(user.objetivo))}</span>
      </span>
      <span class="profile-bmi"><strong>${bmi || "—"}</strong><span>IMC</span></span>
    </div>
    <div class="workout-summary"><span>EXERCÍCIOS NO PLANO</span><strong>${state.exercises.length} ${state.exercises.length === 1 ? "exercício" : "exercícios"}</strong></div>
    <div class="exercise-list">${exercisesMarkup}</div>`;
  elements.totalExercises.textContent = String(state.exercises.length).padStart(2, "0");
}

function showFormError(element, message) {
  element.textContent = message;
  element.classList.remove("hidden");
}

function clearFormError(element) {
  element.textContent = "";
  element.classList.add("hidden");
}

function setFormBusy(form, busy) {
  const submit = form.querySelector('[type="submit"]');
  submit.disabled = busy;
  submit.textContent = busy ? "Salvando..." : form.id === "userForm" ? "Cadastrar aluno" : "Adicionar ao treino";
}

document.querySelector("#todayLabel").textContent = new Intl.DateTimeFormat("pt-BR", {
  weekday: "short", day: "2-digit", month: "short",
}).format(new Date());
elements.connectionUrl.textContent = API_BASE;

document.querySelector("#openUserModal").addEventListener("click", () => {
  clearFormError(document.querySelector("#userFormError"));
  document.querySelector("#userModal").showModal();
});

elements.addExerciseButton.addEventListener("click", () => {
  clearFormError(document.querySelector("#exerciseFormError"));
  document.querySelector("#exerciseModal").showModal();
});

document.querySelectorAll("[data-close]").forEach((button) => {
  button.addEventListener("click", () => document.querySelector(`#${button.dataset.close}`).close());
});

document.querySelector("#retryButton").addEventListener("click", loadUsers);
elements.presetTabs.addEventListener("click", (event) => {
  const tab = event.target.closest("[data-plan-index]");
  if (!tab) return;
  state.selectedPredefinedPlan = Number(tab.dataset.planIndex);
  renderPredefinedPlans();
});
elements.searchInput.addEventListener("input", (event) => {
  state.search = event.target.value;
  renderUsers();
});

elements.studentList.addEventListener("click", async (event) => {
  const removeButton = event.target.closest("[data-delete-user-id]");
  if (removeButton) {
    const user = state.users.find((item) => String(item.id) === String(removeButton.dataset.deleteUserId));
    if (!user || !window.confirm(`Remover ${user.nome} e todos os exercícios associados? Esta ação não pode ser desfeita.`)) return;

    removeButton.disabled = true;
    try {
      await request(`/usuarios/${encodeURIComponent(user.id)}`, { method: "DELETE" });
      await loadUsers();
    } catch (error) {
      window.alert(`Não foi possível remover o aluno: ${error.message}`);
      removeButton.disabled = false;
    }
    return;
  }

  const row = event.target.closest("[data-user-id]");
  if (!row) return;
  state.selectedUserId = row.dataset.userId;
  renderUsers();
  await loadExercises(state.selectedUserId);
});

document.querySelector("#userForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const errorElement = document.querySelector("#userFormError");
  clearFormError(errorElement);
  const values = new FormData(form);
  const payload = {
    nome: String(values.get("nome")).trim(),
    idade: Number(values.get("idade")),
    peso: Number(values.get("peso")),
    altura: Number(values.get("altura")),
    objetivo: values.get("objetivo"),
  };

  setFormBusy(form, true);
  try {
    const created = await request("/usuarios", { method: "POST", body: JSON.stringify(payload) });
    await loadUsers();
    if (created?.id !== undefined) {
      state.selectedUserId = created.id;
      renderUsers();
      await loadExercises(created.id);
    }
    form.reset();
    document.querySelector("#userModal").close();
  } catch (error) {
    showFormError(errorElement, error.message);
  } finally {
    setFormBusy(form, false);
  }
});

const exerciseType = document.querySelector("#exerciseType");
exerciseType.addEventListener("change", () => {
  const cardio = exerciseType.value === "CARDIO";
  document.querySelector("#strengthFields").classList.toggle("hidden", cardio);
  document.querySelector("#cardioFields").classList.toggle("hidden", !cardio);
  document.querySelector('[name="grupoMuscular"]').required = !cardio;
  document.querySelector('[name="series"]').required = !cardio;
  document.querySelector('[name="repeticoes"]').required = !cardio;
  document.querySelector('[name="duracaoMinutos"]').required = cardio;
});
exerciseType.dispatchEvent(new Event("change"));

document.querySelector("#exerciseForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const errorElement = document.querySelector("#exerciseFormError");
  clearFormError(errorElement);
  const values = new FormData(form);
  const cardio = values.get("tipo") === "CARDIO";
  const payload = cardio
    ? { tipo: "CARDIO", nome: String(values.get("nome")).trim(), duracaoMinutos: Number(values.get("duracaoMinutos")) }
    : {
      tipo: "FORCA",
      nome: String(values.get("nome")).trim(),
      grupoMuscular: String(values.get("grupoMuscular")).trim(),
      series: Number(values.get("series")),
      repeticoes: Number(values.get("repeticoes")),
    };

  setFormBusy(form, true);
  try {
    await request(`/usuarios/${encodeURIComponent(state.selectedUserId)}/exercicios`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
    await loadExercises(state.selectedUserId);
    form.reset();
    exerciseType.dispatchEvent(new Event("change"));
    document.querySelector("#exerciseModal").close();
  } catch (error) {
    showFormError(errorElement, error.message);
  } finally {
    setFormBusy(form, false);
  }
});

elements.workoutContent.addEventListener("click", async (event) => {
  const removeButton = event.target.closest("[data-exercise-id]");
  if (!removeButton) return;
  removeButton.disabled = true;
  try {
    await request(`/exercicios/${encodeURIComponent(removeButton.dataset.exerciseId)}`, { method: "DELETE" });
    await loadExercises(state.selectedUserId);
  } catch (error) {
    removeButton.disabled = false;
    setConnection(false, error.message);
  }
});

loadUsers();
loadPredefinedPlans();

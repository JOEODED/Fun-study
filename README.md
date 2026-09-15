# StudyHub (Fun Study)

A full-stack student productivity and course management web application. Built as a portfolio project to demonstrate practical full-stack development: authentication, relational data modeling, REST API design, and a responsive dashboard UI.

## Overview

StudyHub helps students track their courses, assignments, study tasks, grades, and study time in one place — replacing scattered notes and spreadsheets with a single organized dashboard.

## Features

- **Authentication** — registration and login with hashed passwords
- **Dashboard** — at-a-glance stats: course count, pending/completed assignments, upcoming deadlines, study hours, overall progress
- **Course Management** — full CRUD for courses (code, name, lecturer, credit units, semester)
- **Assignment Management** — full CRUD, priority levels, deadlines, mark-as-complete, linked to courses
- **Study Tasks** — a personal to-do list independent of coursework, with priority and status tracking
- **Grade Calculator** — enter assignment/test/exam scores per course; automatically computes total score, letter grade, and GPA
- **Study Timer** — Pomodoro-style 25-minute focus sessions with 5-minute breaks; completed sessions are logged and tallied
- **Responsive Design** — usable on desktop, tablet, and mobile

## Technologies

**Frontend:** HTML, CSS, JavaScript (vanilla, no framework)
**Backend:** Java, Spring Boot (REST API)
**Database:** MySQL
**Version Control:** Git, GitHub

## Architecture

```
Frontend (HTML/CSS/JS)
      |
      | fetch() calls, JSON, X-User-Email header
      v
Spring Boot REST API (/api/...)
      |
      | Spring Data JPA / Hibernate
      v
MySQL Database
```

The frontend is a static multi-page app — one HTML file per feature (courses, assignments, tasks, grades, timer), each with its own JS file that calls the backend's REST endpoints. Authentication is currently handled by passing the logged-in user's email in an `X-User-Email` header on every request (a temporary approach — see Future Improvements).

## Database Structure

| Table | Purpose |
|---|---|
| `users` | Registered users (name, email, hashed password) |
| `courses` | Courses belonging to a user |
| `assignments` | Assignments, linked to a course and a user |
| `tasks` | Personal study tasks, linked to a user only |
| `study_sessions` | Logged Pomodoro sessions, optionally linked to a course |
| `grades` | Score entries per course, with computed total/letter grade |

Relationships: a `User` has many `Courses`, `Assignments`, `Tasks`, `StudySessions`, and `Grades`. An `Assignment` and a `Grade` each belong to one `Course`. Deleting a course cascades to delete its assignments.

## Installation

### Prerequisites
- Java 17+ and Maven
- MySQL 8+
- A modern web browser

### Backend setup
1. Clone the repository
2. Create a MySQL database: `CREATE DATABASE fun_study_db;`
3. Copy `backend/src/main/resources/application.properties.example` to `application.properties` and fill in your own MySQL username and password
4. From the `backend` folder, run:
   ```bash
   ./mvnw spring-boot:run
   ```
5. The API will start on `http://localhost:8080`

### Frontend setup
1. Open the `frontend` folder in VS Code
2. Use a local server (e.g. the "Live Server" extension) to serve `index.html`
3. Register a new account, then log in

## API Documentation

All endpoints (except register/login) require an `X-User-Email` header identifying the logged-in user.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create a new account |
| POST | `/api/auth/login` | Log in |
| GET | `/api/dashboard` | Dashboard summary stats |
| GET/POST | `/api/courses` | List / create courses |
| GET/PUT/DELETE | `/api/courses/{id}` | Get / update / delete a course |
| GET/POST | `/api/assignments` | List / create assignments (create needs `?courseId=`) |
| GET/PUT/DELETE | `/api/assignments/{id}` | Get / update / delete an assignment |
| PATCH | `/api/assignments/{id}/complete` | Mark an assignment complete |
| GET/POST | `/api/tasks` | List / create study tasks |
| GET/PUT/DELETE | `/api/tasks/{id}` | Get / update / delete a task |
| PATCH | `/api/tasks/{id}/complete` | Mark a task complete |
| GET/POST | `/api/grades` | List / add a grade entry |
| GET | `/api/grades/gpa` | Get current GPA |
| GET/POST | `/api/sessions` | List / log a completed study session |
| GET | `/api/sessions/total-minutes` | Total minutes studied |

## Screenshots

_Add screenshots of the Dashboard, Courses, Assignments, and Timer pages here before submitting._

## Future Improvements

- Replace the `X-User-Email` header approach with proper token-based authentication (JWT)
- Add a Statistics page with weekly study activity charts
- Add global search and filtering across courses, assignments, and tasks
- Add unit and integration tests for the backend services
- Deploy the app (e.g. Railway/Render for backend, Netlify/Vercel for frontend)

## License

This project was built for educational and portfolio purposes.

# Wojet Project Management System (Under development)

[![Java](https://img.shields.io/badge/Java-21-007396)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791)](#)
[![Build](https://img.shields.io/badge/Build-Maven-FF69B4)](#)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](#license)

A modern, multi-tenant **Project Management Tool**. Backend: **Java 21 + Spring Boot 3**, JWT auth, RBAC, audit trails, and a clean REST API. A separate Angular front-end can consume the API.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Run](#run)
- [API Overview](#api-overview)
- [Security](#security)
- [Auditing](#auditing)
- [Frontend](#frontend)
- [Project Structure](#project-structure)
- [Development Tips](#development-tips)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Core domain**: Users, Clients (tenants), Projects, Tasks, Comments, Attachments  
- **Extended**: Tags, Status, Priority (lookup-driven)  
- **Security**: JWT in HttpOnly cookie; roles: `CLIENT_ADMIN`, `PROJECT_MANAGER`, `USER`  
- **Auditing**: every entity has `created_at`, `updated_at`, `created_by`, `updated_by`  
- **Internationalization**: i18n-ready  
- **Observability**: structured logs; toggle SQL logging

---

## Architecture


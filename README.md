## 📅 Book Me Up
**Book Me Up** is a full-stack appointment management platform designed to simplify the process of scheduling and managing visits between customers and service providers.

The application allows users to discover available services, create and manage appointments, communicate in real time, and manage their business profiles through an intuitive web interface.

The project was developed with a focus on **scalable architecture, security, maintainability, and production-like infrastructure**, using modern web technologies and containerized deployment.

The application is deployed on a VPS environment using Docker and is available as a live demo for testing without requiring local setup.

> 🚧 **Project Status: Alpha (1.0.0-ALPHA)**.<br> The current release focuses on core end-user functionality. Administrative tools, advanced system management, and additional platform features are planned for future versions.

## 🌍 Live Demo [![Live Demo](https://img.shields.io/badge/Live-Demo-success?style=for-the-badge)](https://m4zek.com.pl)
  
  The application is deployed and available on VPS: 👉 **[Visit Book Me Up (Live)](https://m4zek.com.pl)**
  
## 📌 Overview

#### Version
  - Current version: **1.0.0-ALPHA**
  - Only **end-user functionality** without admin panel and system management

#### Features
  - User registration and authentication using JWT/JWE
  - Role-based access controll
  - Company management
  - Appointment scheduling system
  - Docker support
  - Real-time chat using WebSockets
  - Database migrations using Flyway
  - File upload and object storage using MinIO

## 🛠️ Tech Stack
| Category | Technologies |
|-----------|-------------|
| Backend | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white) |
| Frontend | ![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white) |
| Database & Object Storage | ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white) ![MinIO](https://img.shields.io/badge/MinIO-C72E49?style=for-the-badge&logo=minio&logoColor=white) |
| DevOps & Tools | ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white) |


## 💾 Local Installation and Setup (Docker)
The project provides a complete Docker-based environment containing:

  - Spring Boot backend
  - Angular frontend
  - MariaDB database
  - MinIO object storage
  
  ### 1. Clone the repository

  ```bash
  git clone https://github.com/M4zek/book-me-up.git
  cd book-me-up-main
  ```
  
  ### 2. Build and start all services
  ```bash
  docker compose up --build --remove-orphans
  ```

  This command starts all required application services.
  > ⚠️ **Note:** The first build may take some time. Wait until all services are fully initialized before continuing.

  ### 3. Configure MinIO hostname
  > **Solves:** The problem of generating signed URLs for files in MinIO via the backend.<br>
  > **Note:** To allow your browser to resolve the MinIO hostname correctly in the local development environment, add the following entry to your system's hosts file:
  

  ```text
  127.0.0.1 minio
  ```

  - **🐧 Linux**

  Edit the hosts file with root privileges:

  ```bash
  sudo nano /etc/hosts
  ```
  Add the following line at the end if it does not already exist:

  ```text
  127.0.0.1 minio
  ```
  - **🪟 Windows**

  Open **Notepad** as **Administrator**, then open:

  ```text
  C:\Windows\System32\drivers\etc\hosts
  ```

  Add the following line:

  ```text
  127.0.0.1 minio
  ```
  
  Save the file and exit.
  > **Note:** Administrator privileges are required to modify the hosts file.


  ### 4. Seed the database
  > **Note:** This will populate the database with the required initial or sample data.
  
  In a new terminal window, run:

  ```bash
  docker compose run --build --rm db-seed
  ```

  > ✅ Once you've completed Step 4, the app is ready to use: **[Book Me Up (Local)](http://localhost:4200)**

## 🧪 Test Accounts (Quick Access)
To let you test the core features (including real-time chat) instantly without going through the registration process, you can use these pre-configured demo accounts:

| Role | Email / Username | Password | Purpose |
| :--- | :--- | :--- | :--- |
| **Client A** | `client.john@example.com` | `Password123!` | Test booking services and sending chat messages |
| **Business/Provider** | `service.alice@example.com` | `Password123!` | Test receiving bookings and replying to messages |

***Tip 1:** Open two different browser windows (e.g., standard and incognito) to log into both accounts simultaneously and test the real-time WebSocket chat between them!*

***Tip 2:** Search for a business by name: **Haircut & Nails** (it must be this specific business, because Alice is the owner), select an offer and make a reservation, then check the reservation in Alice’s dashboard—a new reservation for her business will appear there*

***Tip 3:** Browse through all the available tabs*

## 👨‍💻 Author 

- Created with passion by **[M4zek](https://github.com/M4zek)**

## 📄 License

This project was created for educational and portfolio purposes.

It is not intended to be used as a production-ready commercial solution.

This project is under the **[MIT License](https://mit-license.org/)**.





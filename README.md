
# MediSphere

MediSphere is a distributed hospital management system developed as our group project. It combines a React frontend with independent Java Spring Boot services for patient management, emergency queue management, operating room scheduling, admissions, and blood and organ donor management.

## Features

- Hospital overview dashboard
- Operating room scheduling and resource reservations
- Patient registration and management
- Priority-based emergency queue
- Hospital room allocation, admission, and discharge
- Blood and organ donor registration
- Blood and organ inventory management
- Compatibility matching and resource reservations

## Technology Stack

**Frontend:** React, Vite, JavaScript, CSS, Lucide React

**Backend:** Java, Spring Boot, Maven, REST APIs

**Database:** H2 for applicable backend services; in-memory storage for patient management

## Project Structure

```text
MediSphere/
├── frontend/
├── scheduling-service/
├── emergencyQueue-patientService-roomAllocation/
├── donor-service/
└── README.md
```

## Prerequisites

Install the following before running MediSphere:

- Git
- Node.js and npm
- Java JDK 21
- Apache Maven
- Visual Studio Code (recommended)

Verify the installations:

```bash
java -version
mvn -version
node -v
npm -v
```

## Running MediSphere Locally

Clone the repository:

```bash
git clone https://github.com/huda249/MediSphere.git
cd MediSphere
```

If you already cloned the repository, switch to `main` and pull the latest changes.

### 1. Start Patient, Emergency Queue & Admissions

Open a terminal in the project root:

```bash
cd emergencyQueue-patientService-roomAllocation
mvn spring-boot:run
```

Runs on `http://localhost:8080`.

### 2. Start OR Scheduling

Open a separate terminal in the project root:

```bash
cd scheduling-service
mvn spring-boot:run
```

Runs on `http://localhost:8082`.

### 3. Start Donor Matching

Open another terminal in the project root:

```bash
cd donor-service
mvn spring-boot:run
```

Runs on `http://localhost:8083`.

### 4. Start the Frontend

Open a fourth terminal in the project root:

```bash
cd frontend
npm install
npm run dev
```

Open the application at:

http://localhost:5173

Keep all four terminals running while using MediSphere.

## API Services

| Service | Port | Example Endpoint |
|---|---|---|
| Patient Management | 8080 | `/api/patients` |
| Emergency Queue | 8080 | `/api/emergency/queue` |
| Admissions | 8080 | `/api/admissions/admit/{id}/{roomNum}` |
| OR Scheduling | 8082 | `/api/scheduling/resources` |
| Donor Management | 8083 | `/api/donors` |
| Blood Inventory | 8083 | `/api/blood-bank/inventory` |
| Organ Inventory | 8083 | `/api/organs` |

The frontend uses Vite's development proxy to communicate with these backend services.

## Testing

After starting the services:

1. Open the Dashboard.
2. Register a test patient on the Patients page.
3. Confirm that the patient appears in Emergency Queue.
4. Update the patient's priority.
5. Admit the patient into an available room.
6. Confirm the patient leaves the Emergency Queue.
7. Discharge the patient and verify that the room is released.
8. Create an operating room reservation.
9. Add donor and inventory records.
10. Test blood and organ compatibility searches and resource reservations.

Use fictional information for testing.

## Important Notes

- Patient records are currently stored in memory and may reset when the patient service restarts.
- Database-backed services may preserve records between restarts, depending on their configurations.
- Some Dashboard statistics and equipment statuses are illustrative rather than live data.
- The Demo Workspace dropdown is a UI placeholder; authentication and account switching are not implemented.
- The application is an academic prototype and is not intended for clinical use.

## Troubleshooting

**`mvn` is not recognized**

Install Apache Maven and add its `bin` directory to your system PATH, or run Maven using its full executable path.

**`npm` is blocked in PowerShell**

On Windows, try:

```powershell
npm.cmd install
npm.cmd run dev
```

**A backend fails to start**

Check that Java is installed and the required port is not already in use.

**The website shows a backend connection error**

Ensure the corresponding Java Spring Boot service is running on its configured port.

**The frontend is missing after pulling**

Verify that you are using the latest `main` branch.

## Team Development

The project is maintained in the shared GitHub repository. Team members should pull the latest changes before starting work and coordinate code changes through Git branches and pull requests.

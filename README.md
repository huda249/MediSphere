# MediSphere

MediSphere is a distributed hospital management system developed as our group project. It combines a React frontend with independent Java Spring Boot services for patient management, emergency queue management, operating room scheduling, admissions, and blood and organ donor management.

## Features

- Hospital overview dashboard
- Operating room scheduling and resource reservations
- Patient registration and management
- Priority-based emergency queue
- Hospital room allocation, admission, and discharge
- Donor registration
- Blood and organ inventory management
- Blood and organ compatibility matching and resource reservations
- Persistent database storage
- Resource conflict detection and prevention

## Technology Stack

**Frontend:** React, Vite, JavaScript, CSS, Lucide React

**Backend:** Java, Spring Boot, Maven, REST APIs

**Database:** SQLite for patient management, emergency queue, hospital room allocation, donor management, and blood and organ inventory. H2 for operating room scheduling and booking persistence.

## Project Structure

```text
MediSphere/
├── frontend/
├── database/
├── data/
│   └── db.sqlite
├── scheduling-service/
├── emergencyQueue-patientService-roomAllocation/
├── donor-service/
├── pom.xml
└── README.md
```

## Prerequisites

Install the following before running MediSphere:

- Git
- Node.js and npm
- Java JDK 21
- Apache Maven
- Python 3 (for hospital room initialization)
- Visual Studio Code (recommended)

Verify the installations:

```bash
java -version
mvn -version
node -v
npm -v
python --version
```

Ensure Maven is configured to use Java 21.

## Running MediSphere Locally

Clone the repository:

```bash
git clone https://github.com/huda249/MediSphere.git
cd MediSphere
```

If you have already cloned the repository, switch to `main` and pull the latest changes.

### 1. Build the Backend Services

From the project root, run:

```bash
mvn clean install -DskipTests
```

This builds the shared database module and all three backend services.

### 2. Initialize the Hospital Rooms

The shared SQLite database is located at `data/db.sqlite`.

Before starting the backend services for the first time, initialize the hospital rooms by running the following command from the project root:

```bash
python -c "import sqlite3; conn = sqlite3.connect('file:data/db.sqlite?mode=rw', uri=True); conn.execute('CREATE UNIQUE INDEX IF NOT EXISTS idx_patientroom_name ON PatientRoom(name)'); conn.executemany('INSERT INTO PatientRoom (name, free, patientId) SELECT ?, 1, 0 WHERE NOT EXISTS (SELECT 1 FROM PatientRoom WHERE name = ?)', [(r,r) for r in ('101','103','105','107','109')]); conn.commit(); print(conn.execute('SELECT * FROM PatientRoom').fetchall()); conn.close()"
```

This initializes five hospital rooms: 101, 103, 105, 107, and 109.

The command can be executed multiple times without creating duplicate rooms or removing existing records.

### 3. Start Patient, Emergency Queue & Admissions

Open a terminal in the project root:

```bash
mvn -pl emergencyQueue-patientService-roomAllocation spring-boot:run "-Dspring-boot.run.workingDirectory=."
```

Runs on `http://localhost:8080`.

### 4. Start OR Scheduling

Open a separate terminal in the project root:

```bash
mvn -pl scheduling-service spring-boot:run "-Dspring-boot.run.workingDirectory=."
```

Runs on `http://localhost:8082`.

The scheduling service communicates with the patient management service to verify patient records before creating reservations.

### 5. Start Donor Matching

Open another terminal in the project root:

```bash
mvn -pl donor-service spring-boot:run "-Dspring-boot.run.workingDirectory=."
```

Runs on `http://localhost:8083`.

**Important:** The backend services must be launched with the project root as their working directory to ensure that the patient and donor services access the same SQLite database.

For Windows PowerShell, if the relative working directory does not resolve correctly, replace `"-Dspring-boot.run.workingDirectory=."` in the above commands with:

```powershell
"-Dspring-boot.run.workingDirectory=$($PWD.Path)"
```

### 6. Start the Frontend

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
| Room Availability | 8080 | `/api/admissions/rooms/available` |
| OR Scheduling | 8082 | `/api/scheduling/resources` |
| OR Reservations | 8082 | `/api/scheduling/bookings` |
| Donor Management | 8083 | `/api/donors` |
| Blood Inventory | 8083 | `/api/blood-bank/inventory` |
| Organ Inventory | 8083 | `/api/organs` |

The frontend uses Vite's development proxy to communicate with these backend services.

## Database and Resource Management

The patient management and donor matching services use the shared SQLite database to store and retrieve records. The scheduling service uses a separate file-based H2 database to persist operating room reservations.

Database transactions and conditional SQL updates are used to maintain consistency during patient admission, discharge, room allocation, and blood and organ reservations.

The scheduling service checks for overlapping reservations to prevent conflicting allocations of operating rooms, equipment, and recovery beds.

## Testing

After starting the services:

1. Open the Dashboard.
2. Register a fictional test patient on the Patients page.
3. Confirm that the patient appears in the Emergency Queue.
4. Update the patient's priority and verify the queue ordering.
5. Admit the patient into an available hospital room.
6. Confirm that the patient leaves the Emergency Queue and the room is no longer available.
7. Check operating room resource availability.
8. Create an operating room reservation for an active patient.
9. Attempt to create a conflicting reservation and verify that it is rejected.
10. Discharge the patient and verify that the hospital room is released.
11. Register a fictional donor and add blood and organ inventory records.
12. Perform blood and organ compatibility searches.
13. Reserve matching resources and verify that the inventory is updated.
14. Attempt to reserve unavailable resources and verify that the requests are rejected.
15. Restart the applicable backend services and verify that saved records persist.

Use fictional information for all testing.

## Important Notes

- Patient, donor, and inventory records are stored persistently in SQLite.
- Operating room reservations are stored persistently in H2.
- Some Dashboard statistics are illustrative rather than live data.
- Equipment information is retrieved from the scheduling service's predefined resource catalogue.
- Donor registration currently stores the donor's name, address, and blood type. Live organ donor registration is limited by the current database schema.
- Compatibility matching uses simplified blood-type rules and does not represent complete clinical compatibility assessment.
- Inter-service donor reservation notifications have not been fully verified.
- The Demo Workspace dropdown is a UI placeholder; authentication and account switching are not implemented.
- The application is an academic prototype and is not intended for clinical use.

## Troubleshooting

**`mvn` is not recognized**

Install Apache Maven and add its `bin` directory to your system PATH, or run Maven using its full executable path.

**Maven uses an incorrect Java version**

Configure the `JAVA_HOME` environment variable to point to the Java 21 installation and verify the configuration using `mvn -version`.

**`npm` is blocked in PowerShell**

On Windows, try:

```powershell
npm.cmd install
npm.cmd run dev
```

**A backend fails to start**

Check that Java 21 is installed and that the required port is not already in use.

**The SQLite database cannot be opened**

Ensure that `data/db.sqlite` exists and that the backend services are launched from the project root with the correct working directory.

**No hospital rooms are available**

Ensure that the hospital room initialization command has been executed.

**The website shows a backend connection error**

Ensure the corresponding Java Spring Boot service is running on its configured port.

**The frontend is missing after pulling**

Verify that you are using the latest `main` branch.

## Team Development

The project is maintained in the shared GitHub repository. Team members should pull the latest changes before starting work and coordinate code changes through Git branches and pull requests.

Generated build files and modified test databases should not be included in new commits.

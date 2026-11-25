DROP TABLE IF EXISTS Patient;
DROP TABLE IF EXISTS Staff;
DROP TABLE IF EXISTS Donor;
DROP TABLE IF EXISTS Equipment;
DROP TABLE IF EXISTS Blood;
DROP TABLE IF EXISTS Organ;
DROP TABLE IF EXISTS Operation;
DROP TABLE IF EXISTS PatientRoom;
DROP TABLE IF EXISTS OperatingRoom;

DROP TABLE IF EXISTS OperationStaff;
DROP TABLE IF EXISTS OperationEquipment;
DROP TABLE IF EXISTS OperationOperatingRoom;

create table IF NOT EXISTS Patient (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(255) NOT NULL,
  ailment VARCHAR(255),
  age INT,
  status VARCHAR(255),
  arrival VARCHAR(255),
  departure varchar(255),
  symptoms varchar(255),
  priority int,
  queuepos int
);

create table IF NOT EXISTS Staff (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(255) NOT NULL,
  salary INT,
  address VARCHAR(255),
  role VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Donor (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    name    VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    bloodType VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Operation (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    opType       VARCHAR(255) NOT NULL,
    scheduled TEXT NOT NULL,
    patientId INT,
    FOREIGN KEY (patientId) REFERENCES Patient(id)
);

CREATE TABLE IF NOT EXISTS Equipment (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    state  VARCHAR(255),
    name         VARCHAR(255) NOT NULL,
    model VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Blood (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    bloodType         VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    harvestTime VARCHAR(255),
    units int,
    donorId     INT,
    operationId INT,
    FOREIGN KEY (donorId) REFERENCES Donor(id),
    FOREIGN KEY (operationId) REFERENCES Operation(id)
);

CREATE TABLE IF NOT EXISTS Organ (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name       VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    harvestTime VARCHAR(255),
    bloodType VARCHAR(255),
    donorId     INT,
    operationId INT,
    FOREIGN KEY (donorId) REFERENCES Donor(id),
    FOREIGN KEY (operationId) REFERENCES Operation(id)
);

CREATE TABLE IF NOT EXISTS PatientRoom (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    free BOOLEAN NOT NULL DEFAULT TRUE,
    patientId INT,
    FOREIGN KEY (patientId) REFERENCES Patient(id)
);

CREATE TABLE IF NOT EXISTS OperatingRoom (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    name         VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS OperationStaff (
  staffId INT,
  operationId INT,
  FOREIGN KEY (staffId) REFERENCES Staff(id),
  FOREIGN KEY (operationId) REFERENCES Operation(id)
);

CREATE TABLE IF NOT EXISTS OperationEquipment (
  equipmentId INT,
  operationId INT,
  FOREIGN KEY (equipmentId) REFERENCES Equipment(id),
  FOREIGN KEY (operationId) REFERENCES Operation(id)
);

CREATE TABLE IF NOT EXISTS OperationOperatingRoom (
    operationId INT,
    operatingRoomId INT,
    FOREIGN KEY (operatingRoomId) REFERENCES OperatingRoom(id)
);

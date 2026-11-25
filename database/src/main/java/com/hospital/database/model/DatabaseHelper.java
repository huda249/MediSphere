package com.hospital.database.model;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseHelper {
    public DatabaseHelper() {
        connect();
    }
    private Connection conn;

    public static void Create() {
        var url = "jdbc:sqlite:data/db.sqlite";

        // read data from the sql schema
        File schema = new File("database/src/main/java/com/hospital/database/model/schema.sql");
        System.out.println("Schema path: " + new File("database/src/main/java/com/hospital/database/model/schema.sql").getAbsolutePath());
        System.out.println("DB path: " + new File("db.sqlite").getAbsolutePath());
        StringBuilder data = new StringBuilder();
        try {
            String sql = new String(java.nio.file.Files.readAllBytes(schema.toPath()));

            try (var c = DriverManager.getConnection(url);
                 var stmt = c.createStatement()) {
                for (String s : sql.split(";")) {
                    if (!s.trim().isEmpty()) {
                        stmt.execute(s);
                    }
                }
                System.out.println("Schema created");
            }
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void connect() {
        var url = "jdbc:sqlite:data/db.sqlite";
        System.out.println("DB path: " + new File("data/db.sqlite").getAbsolutePath());
        //var conn = Connection();
        try {
            var c = DriverManager.getConnection(url);
            System.out.println("connected!");
            this.conn = c;
            //return c;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            //return null;
        }
    }

    // inserts
    public int insertPatient(String name,
                             String ailment,
                             int age,
                             String status,
                             String arrival,
                             String symptoms,
                             int priority, int queuepos) {
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO Patient(name, ailment, age, status, arrival, symptoms, priority, queuepos) VALUES(?,?,?,?,?,?,?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setString(2, ailment);
            query.setInt(3, age);
            query.setString(4, status);
            query.setString(5, arrival);
            query.setString(6, symptoms);
            query.setInt(7, priority);
            query.setInt(8, queuepos);
            query.executeUpdate();

            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("err" + e.getMessage());
            return 0;
        }
    }

  // IntelliJ API Decompiler stub source generated from a class file
  // Implementation of methods is not available

    public int insertStaff(String name, String address, int salary, String role){
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO Staff(name, salary, address, role) VALUES(?,?,?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setInt(2, salary);
            query.setString(3, address);
            query.setString(4, role);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertDonor(String name, String address, String bloodType){
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO Donor(name, address, bloodType) VALUES(?,?, ?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setString(2, address);
            query.setString(3, bloodType);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertOperation(String opType, String scheduled, int patientId){
        // if the nullable ones are null, set to defaults
        // todo: add all the other details
        String sql = "INSERT INTO Operation(opType, scheduled, patientId) VALUES(?,?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, opType);
            query.setString(2, scheduled);
            query.setInt(3, patientId);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertEquipment(String name, String state, String model){
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO Equipment(name, state, model) VALUES(?,?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setString(2, state);
            query.setString(3, model);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertBlood(String bloodType, int donorId, String status, String harvestTime, int operationId, int units){
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO Blood(bloodType, donorId, status, harvestTime, operationId, units) VALUES(?,?,?,?,?, ?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, bloodType);
            query.setInt(2, donorId);
            query.setString(3, status);
            query.setString(4, harvestTime);
            query.setInt(5, operationId);
            query.setInt(6, units);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertOrgan(String name, int donorId, String status, String harvestTime, int operationId, String bloodType){
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO Organ(name, donorId, status, harvestTime, operationId, bloodType) VALUES(?,?,?,?,?, ?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setInt(2, donorId);
            query.setString(3, status);
            query.setString(4, harvestTime);
            query.setInt(5, operationId);
            query.setString(6, bloodType);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertPatientRoom(String name, boolean free){
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO PatientRoom(name, free, patientId) VALUES(?,?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setBoolean(2, free);
            query.setInt(3, 0);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertOperatingRoom(String name) {
        // if the nullable ones are null, set to defaults
        String sql = "INSERT INTO OperatingRoom(name) VALUES(?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertOperationStaff(int operationId, int staffId) {
        String sql = "INSERT INTO OperationStaff(operationId, staffId) VALUES(?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, operationId);
            query.setInt(2, staffId);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertOperationEquipment(int operationId, int equipmentId) {
        String sql = "INSERT INTO OperationEquipment(operationId, equipmentId) VALUES(?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, operationId);
            query.setInt(2, equipmentId);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int insertOperationOperatingRoom(int operationId, int operatingRoomId) {
        String sql = "INSERT INTO OperationOperatingRoom(operationId, operatingRoomId) VALUES(?,?)";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, operationId);
            query.setInt(2, operatingRoomId);
            query.executeUpdate();
            try (var stmt = this.conn.createStatement();
                 var rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            } return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    // gets
    public ArrayList<Patient> getPatient(String name, String status) {
        if (name == null) name = "";
        String sql = "SELECT * FROM Patient where name LIKE ? and status = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            query.setString(2, "%"+status+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<Patient>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pName = res.getString("name");
                var pAilment = res.getString("ailment");
                var pAge = res.getInt("age");
                var pSymptoms = res.getString("symptoms");
                var pArrival = res.getString("arrival");
                var pDeparture = res.getString("departure");
                var pPriority = res.getInt("priority");
                var pQueuepos = res.getInt("queuepos");
                var pStatus = res.getString("status");
                ret.add(new Patient(pid, pName, pAge, pAilment, pSymptoms, pStatus, pArrival, pDeparture, pPriority, pQueuepos));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Staff> getStaff(String name, String role) {
        if (name == null) name = "";
        if (role == null) role = "";
        String sql = "SELECT * FROM Staff where name LIKE ? AND role LIKE ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            query.setString(2, "%"+role+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<Staff>();
            while (res.next()) {
                var Sid = res.getInt("id");
                var Sname = res.getString("name");
                var Ssalary = res.getInt("salary");
                var Saddress = res.getString("address");
                var Srole = res.getString("role");
                ret.add(new Staff(Sid, Sname, Ssalary, Saddress, Srole));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Donor> getDonor(String name, String address, String bloodType) {
        if (name == null) name = "";
        if (address == null) address = "";
        if (bloodType == null) bloodType = "";
        String sql = "SELECT * FROM Donor where name LIKE ? and address like ? and bloodType like ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            query.setString(2, "%"+address+"%");
            query.setString(3, "%"+bloodType+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<Donor>();
            while (res.next()) {
                var Did = res.getInt("id");
                var Dname = res.getString("name");
                var Daddress = res.getString("address");
                var DbloodType = res.getString("bloodType");
                ret.add(new Donor(Did, Dname, Daddress, DbloodType));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Equipment> getEquipment(String name, String state, String model) {
        if (name == null) name = "";
        if (state == null) state = "";
        if (model == null) model = "";
        String sql = "SELECT * FROM Equipment where name LIKE ? AND state LIKE ? and model like ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            query.setString(2, "%"+state+"%");
            query.setString(3, "%"+model+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<Equipment>();
            while (res.next()) {
                var Eid = res.getInt("id");
                var Ename = res.getString("name");
                var Estate = res.getString("state");
                var Emodel = res.getString("model");
                ret.add( new Equipment(Eid, Ename, Estate, Emodel));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Blood> getBlood(String type, String status) {
        if (type == null) type = "";
        if (status == null) status = "";
        String sql = "SELECT * FROM Blood where bloodType LIKE ? and status like ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+type+"%");
            query.setString(2, "%"+status+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<Blood>();
            while (res.next()) {
                var Bid = res.getInt("id");
                var BbloodType = res.getString("bloodType");
                var BdonorId = res.getInt("donorId");
                var BoperationId = res.getInt("operationId");
                var Bstatus = res.getString("status");
                var BharvestTime = res.getString("harvestTime");
                var Bunits = res.getInt("units");
                ret.add( new Blood(Bid, BbloodType, BdonorId, BoperationId, Bstatus, BharvestTime, Bunits));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Organ> getOrgan(String name, String status, String harvestTime, String bloodType) {
        if (name == null) name = "";
        String sql = "SELECT * FROM Organ where name LIKE ? and status like ? and harvestTime like ? and bloodType like ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            query.setString(2, "%"+status+"%");
            query.setString(3, "%"+harvestTime+"%");
            query.setString(4, "%"+bloodType+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<Organ>();
            while (res.next()) {
                var Bid = res.getInt("id");
                var BbloodType = res.getString("name");
                var BdonorId = res.getInt("donorId");
                var BoperationId = res.getInt("operationId");
                var Bstatus = res.getString("status");
                var BharvestTime = res.getString("harvestTime");
                var Btype = res.getString("bloodType");
                ret.add(new Organ(Bid, BbloodType, BdonorId, BoperationId, Bstatus, BharvestTime, Btype));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Organ> getOrganByDonor(int donorId) {
        String sql = "SELECT * FROM Organ where donorId = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, donorId);
            var res = query.executeQuery();
            var ret = new ArrayList<Organ>();
            while (res.next()) {
                var Bid = res.getInt("id");
                var BbloodType = res.getString("name");
                var BdonorId = res.getInt("donorId");
                var BoperationId = res.getInt("operationId");
                var Bstatus = res.getString("status");
                var BharvestTime = res.getString("harvestTime");
                var Btype = res.getString("bloodType");
                ret.add(new Organ(Bid, BbloodType, BdonorId, BoperationId, Bstatus, BharvestTime, Btype));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<PatientRoom> getPatientRoom(String name) {
        if (name == null) name = "";
        String sql = "SELECT * FROM PatientRoom where name LIKE ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<PatientRoom>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pname = res.getString("name");
                var pfree = res.getBoolean("free");
                ret.add( new PatientRoom(pid, pname, pfree));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<PatientRoom> getPatientRoomByPatientId(int patientId) {
        String sql = "SELECT * FROM PatientRoom where patientId = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, patientId);
            var res = query.executeQuery();
            var ret = new ArrayList<PatientRoom>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pname = res.getString("name");
                var pfree = res.getBoolean("free");
                ret.add( new PatientRoom(pid, pname, pfree));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<PatientRoom> getFreePatientRooms() {
        String sql = "SELECT * FROM PatientRoom where free = 1;";
        try (var query = this.conn.createStatement()) {
            var res = query.executeQuery(sql);
            var ret = new ArrayList<PatientRoom>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pname = res.getString("name");
                var pfree = res.getBoolean("free");
                ret.add( new PatientRoom(pid, pname, pfree));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<OperatingRoom> getOperationRoom(String name) {
        if (name == null) name = "";
        String sql = "SELECT * FROM OperatingRoom where name LIKE ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%"+name+"%");
            var res = query.executeQuery();
            var ret = new ArrayList<OperatingRoom>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pname = res.getString("name");
                ret.add(new OperatingRoom(pid, pname));
            }
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<Operation> getOperation(String opType, String scheduled, int patientId) {
        if (opType == null) opType = "";
        String sql = "SELECT * FROM Operation where opType LIKE ? and scheduled LIKE ? and patientId = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, "%" + opType + "%");
            query.setString(2, "%" + scheduled.toString() + "%");
            query.setInt(3, patientId);
            var res = query.executeQuery();
            var ret = new ArrayList<Operation>();
            while (res.next()) {
                var operationId = res.getInt("id");
                var OopType = res.getString("opType");
                var Oscheduled = res.getString("scheduled");
                var OpatientId = res.getInt("patientId");

                ArrayList<Integer> opStaff = getOperationAttributes(
                        "SELECT staffId FROM OperationStaff where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opEquipment = getOperationAttributes(
                        "SELECT equipmentId FROM OperationEquipment where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opOperatingRoom = getOperationAttributes(
                        "SELECT operatingRoomId FROM OperationOperatingRoom where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opBlood = getOperationAttributes(
                        "SELECT id FROM Blood where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opOrgan = getOperationAttributes(
                        "SELECT id FROM Organ where operationId = ?",
                        operationId
                );
                ret.add(new Operation(operationId, OopType, Oscheduled, OpatientId, opStaff, opEquipment, opBlood, opOrgan, opOperatingRoom));
            }
            // more times
            return ret;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    private ArrayList<Integer> getOperationAttributes(String sql, int operationId) {
        ArrayList<Integer> list = new ArrayList<>();
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, operationId);
            var res = query.executeQuery();
            while (res.next()) {
                list.add(res.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
    public Operation getOperationById(int id) {
        String sql = "SELECT * FROM Operation where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Operation>();
            while (res.next()) {
                var operationId = res.getInt("id");
                var OopType = res.getString("opType");
                var Oscheduled = res.getString("scheduled");
                var OpatientId = res.getInt("patientId");

                ArrayList<Integer> opStaff = getOperationAttributes(
                        "SELECT staffId FROM OperationStaff where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opEquipment = getOperationAttributes(
                        "SELECT equipmentId FROM OperationEquipment where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opOperatingRoom = getOperationAttributes(
                        "SELECT operatingRoomId FROM OperationOperatingRoom where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opBlood = getOperationAttributes(
                        "SELECT id FROM Blood where operationId = ?",
                        operationId
                );
                ArrayList<Integer> opOrgan = getOperationAttributes(
                        "SELECT id FROM Organ where operationId = ?",
                        operationId
                );
                ret.add(new Operation(operationId, OopType, Oscheduled, OpatientId, opStaff, opEquipment, opBlood, opOrgan, opOperatingRoom));
            }
            // more times
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM Patient where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Patient>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pName = res.getString("name");
                var pAilment = res.getString("ailment");
                var pAge = res.getInt("age");
                var pSymptoms = res.getString("symptoms");
                var pArrival = res.getString("arrival");
                var pDeparture = res.getString("departure");
                var pPriority = res.getInt("priority");
                var pQueuepos = res.getInt("queuepos");
                var pStatus = res.getString("status");
                ret.add(new Patient(pid, pName, pAge, pAilment, pSymptoms, pStatus, pArrival, pDeparture, pPriority, pQueuepos));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Staff getStaffById(int id) {
        String sql = "SELECT * FROM Staff where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Staff>();
            while (res.next()) {
                var Sid = res.getInt("id");
                var Sname = res.getString("name");
                var Ssalary = res.getInt("salary");
                var Saddress = res.getString("address");
                var Srole = res.getString("role");
                ret.add(new Staff(Sid, Sname, Ssalary, Saddress, Srole));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Donor getDonorById(int id) {
        String sql = "SELECT * FROM Donor where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Donor>();
            while (res.next()) {
                var Did = res.getInt("id");
                var Dname = res.getString("name");
                var Daddress = res.getString("address");
                var DbloodType = res.getString("bloodType");
                ret.add(new Donor(Did, Dname, Daddress, DbloodType));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Equipment getEquipmentById(int id) {
        String sql = "SELECT * FROM Equipment where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Equipment>();
            while (res.next()) {
                var Eid = res.getInt("id");
                var Ename = res.getString("name");
                var Estate = res.getString("state");
                var Emodel = res.getString("model");
                ret.add( new Equipment(Eid, Ename, Estate, Emodel));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Blood getBloodById(int id) {
        String sql = "SELECT * FROM Blood where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Blood>();
            while (res.next()) {
                var Bid = res.getInt("id");
                var BbloodType = res.getString("bloodType");
                var BdonorId = res.getInt("donorId");
                var BoperationId = res.getInt("operationId");
                var Bstatus = res.getString("status");
                var BharvestTime = res.getString("harvestTime");
                var Bunits = res.getInt("units");
                ret.add( new Blood(Bid, BbloodType, BdonorId, BoperationId, Bstatus, BharvestTime, Bunits));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Organ getOrganById(int id) {
        String sql = "SELECT * FROM Organ where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<Organ>();
            while (res.next()) {
                var Bid = res.getInt("id");
                var BbloodType = res.getString("name");
                var BdonorId = res.getInt("donorId");
                var BoperationId = res.getInt("operationId");
                var Bstatus = res.getString("status");
                var BharvestTime = res.getString("harvestTime");
                var Btype = res.getString("bloodType");
                ret.add(new Organ(Bid, BbloodType, BdonorId, BoperationId, Bstatus, BharvestTime, Btype));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public PatientRoom getPatientRoomById(int id) {
        String sql = "SELECT * FROM PatientRoom where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<PatientRoom>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pname = res.getString("name");
                var pfree = res.getBoolean("free");
                ret.add( new PatientRoom(pid, pname, pfree));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public OperatingRoom getOperatingRoomById(int id) {
        String sql = "SELECT * FROM OperatingRoom where id = ?;";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, id);
            var res = query.executeQuery();
            var ret = new ArrayList<OperatingRoom>();
            while (res.next()) {
                var pid = res.getInt("id");
                var pname = res.getString("name");
                ret.add(new OperatingRoom(pid, pname));
            }
            return ret.get(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // edits
    public int updatePatient(int id, int priority, int queuepos, String departure, String status, String ailment, String symptoms) {
        String sql = "UPDATE Patient set priority = ? , queuepos = ? , departure = ? , status = ? , ailment = ? , symptoms = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, priority);
            query.setInt(2, queuepos);
            query.setString(3, departure);
            query.setString(4, status);
            query.setString(5, ailment);
            query.setString(6, symptoms);
            query.setInt(7, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateStaff(int id, String name, int salary, String address, String role) {
        String sql = "UPDATE Staff set name = ? , salary = ? , address = ? , role = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setInt(2, salary);
            query.setString(3, address);
            query.setString(4, role);
            query.setInt(5, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateDonor(int id, String name, String address, String bloodType) {
        String sql = "UPDATE Donor set name = ? , address = ? , bloodType = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setString(2, address);
            query.setString(3, bloodType);
            query.setInt(4, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateBloodOperation(int id, int operationId ) {
        String sql = "UPDATE Blood set operationId = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, operationId);
            query.setInt(2, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateBloodUnits(int id, int units ) {
        String sql = "UPDATE Blood set units = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1, units);
            query.setInt(2, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
    public int updateBloodStatus(int id, String status ) {
        String sql = "UPDATE Blood set status = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, status);
            query.setInt(2, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    public int updateOrgan(int id, String name, int donorId, int operationId, String status, String harvestTime) {
        String sql = "UPDATE Organ set name = ? , donorId = ? , operationId = ? , status = ? , harvestTime = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setInt(2, donorId);
            query.setInt(3, operationId);
            query.setString(4, status);
            query.setString(5, harvestTime);
            query.setInt(6, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateEquipment(int id, String state, String name, String model) {
        String sql = "UPDATE Equipment set state = ? , name = ? , model = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, state);
            query.setString(2, name);
            query.setString(3, model);
            query.setInt(4, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateOperation(int id, String opType, String scheduled, int patientId) {
        String sql = "UPDATE Operation set opType = ? , scheduled = ?, patientId = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, opType);
            query.setString(2, scheduled);
            query.setInt(3, patientId);
            query.setInt(4, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updatePatientRoom(int id, String name, boolean free, int patientId) {
        String sql = "UPDATE PatientRoom set name = ? , free = ? , patientId = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setBoolean(2, free);
            query.setInt(3, patientId);
            query.setInt(4, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int updateOperatingRoom(int id, String name) {
        String sql = "UPDATE OperatingRoom set name = ? where id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setString(1, name);
            query.setInt(2, id);
            return query.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    // deletes
    public int deletePatient(int id) {
        String sql = "DELETE FROM Patient WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteStaff(int id) {
        String sql = "DELETE FROM Staff WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteDoner(int id) {
        String sql = "DELETE FROM Donor WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteOperation(int id) {
        String sql = "DELETE FROM Operation WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteEquipment(int id) {
        String sql = "DELETE FROM Equipment WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteBlood(int id) {
        String sql = "DELETE FROM Blood WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteOrgan(int id) {
        String sql = "DELETE FROM Organ WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deletePatientRoom(int id) {
        String sql = "DELETE FROM PatientRoom WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteOperatingRoom(int id) {
        String sql = "DELETE FROM OperatingRoom WHERE id = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,id);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteOperationStaff(int operationId, int staffId) {
        String sql = "DELETE FROM OperationStaff WHERE operationId = ? AND staffId = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,operationId);
            query.setInt(2,staffId);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteOperationEquipment(int operationId, int equipmentId) {
        String sql = "DELETE FROM OperationEquipment WHERE operationId = ? AND equipmentId = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,operationId);
            query.setInt(2,equipmentId);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int deleteOperationOperatingRoom(int operationId, int operatingRoomId) {
        String sql = "DELETE FROM OperationOperatingRoom WHERE operationId = ? AND operatingRoomId = ?";
        try (var query = this.conn.prepareStatement(sql)) {
            query.setInt(1,operationId);
            query.setInt(2,operatingRoomId);
            query.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static void main(String[] args) {
        //connect();
        //Create();
        var db = new DatabaseHelper();
        var date = new java.util.Date().toString();
        //var sqlDate = new java.sql.Date(date.getTime());
        // insert tests

        //System.out.println(db.insertStaff("jimbo", "123 main street", 1, "janitor"));
        //System.out.println(db.insertPatientRoom("1A", true));
        System.out.println(db.insertPatient("jimdfwfwefbob", "dead :(", 24, "ded", date, "none", 1, 1));
        //System.out.println(db.insertDonor("jimbo", "123 main street", "A"));
        //System.out.println(db.insertBlood("A", 1, "available", "01/01/2026", 1, 5));


        //System.out.println(db.updatePatient(2, 1,1,date, "alive", "aliveness", "none"));
        //System.out.println(db.updateDonor(2, "wefwfeffefee", "sdfwf", "Z"));
        //System.out.println(db.updateBloodStatus(1, "spoiled"));
        //System.out.println(db.updateBloodUnits(1, 1000));
        /*

        System.out.println(db.insertPatientRoom("1B", false));


        System.out.println(db.insertOperation( "heart surgery",  date, 1));
        System.out.println(db.insertEquipment("mri1", "functional", "MRI"));

        System.out.println(db.insertOrgan("Lung", 1, "available", "01/01/2026", 1));
        System.out.println(db.insertPatientRoom("1A", false));
        System.out.println(db.insertOperatingRoom("2A"));
        System.out.println(db.insertOperationStaff(1, 1));
        System.out.println(db.insertOperationOperatingRoom(1, 1));
        System.out.println(db.insertOperationEquipment(1, 1));*/

        // update tests
        /*//System.out.println(db.updatePatient(2, "alivwe :)", "jimbobob", "245 jump street", 1));
        System.out.println(db.updatePatientRoom(2, "10sdf", false, 2));
        System.out.println(db.updateStaff(2, "Sdfsdf", 121231, "12312312 sdfwe", "ceo"));
        System.out.println(db.updateDonor(2, "wefwfeffefee", "sdfwf"));
        System.out.println(db.updateOperation(2, "MAID", date, 1));
        System.out.println(db.updateEquipment(2, "broken", "wewer", "MRRR"));
        System.out.println(db.updateBlood(2, "B", 1, 1, "fwdf", "yestsdfsdfsdferday"));
        System.out.println(db.updateOrgan(2, "rfge", 1, 1, "wefw", "fwefwef"));
        System.out.println(db.updateOperatingRoom(2, "dwfwefwefdeathroom"));*/


        // get tests
        /*
        var op = db.getOperation("heart surgery", "Sun Nov 22 11:12:45 EST 2026", 1);
        for (int i=0; i < op.size() ; i++ ){
            var cop = op.get(i);
            System.out.println("op: "+cop.getName() + " " +
                    cop.date + " " +
                    cop.patientId);
        }

        op = db.getOperationById(op.get(0).id);
        for (int i=0; i < op.size() ; i++ ){
            var cop = op.get(i);
            System.out.println("id:  op: "+cop.getName() + " " +
                    cop.date + " " +
                    cop.patientId);
        }
        var patient = db.getPatient("jimbob");
        for (int i=0; i < patient.size(); i++) {
            var p = patient.get(i);
            System.out.println(p.getId()+" "+p.getAddress() + " "+p.getAge() + " "+ p.getName() + " " + p.getBirthday());
        }
        patient = db.getPatientById(patient.get(0).getId());
        for (int i=0; i < patient.size(); i++) {
            var p = patient.get(i);
            System.out.println("id: "+p.getId()+" "+p.getAddress() + " "+p.getAge() + " "+ p.getName() + " " + p.getBirthday());
        }

        var staff = db.getStaff("jimbo", "janitor");
        for (int i=0; i < staff.size(); i++) {
            var s = staff.get(i);
            System.out.println(s.getId() + " " + s.getName() + " " + s.getSalary() + " " + s.getAddress() + " " + s.getRole());
        }

        staff = db.getStaffById(staff.get(0).getId());
        for (int i=0; i < staff.size(); i++) {
            var s = staff.get(i);
            System.out.println("id: " + s.getId() + " " + s.getName() + " " + s.getSalary() + " " + s.getAddress() + " " + s.getRole());
        }

        var donor = db.getDonor("jimbo", "123 main street");
        for (int i=0; i  < donor.size();i++) {
            var d= donor.get(i);
            System.out.println(d.getId() + " " + d.getName() + " " + d.getAddress());
        }
        donor = db.getDonorById(donor.get(0).getId());
        for (int i=0; i  < donor.size();i++) {
            var d= donor.get(i);
            System.out.println("id: "+d.getId() + " " + d.getName() + " " + d.getAddress());
        }

        var equipment = db.getEquipment("mri1", "functional", "MRI");
        for (int i=0; i  < equipment.size();i++) {
            var e= equipment.get(i);
            System.out.println(e.getId() + " " + e.getModel() + " " + e.getName() + " " + e.getState());
        }
        equipment = db.getEquipmentById(equipment.get(0).getId());
        for (int i=0; i  < equipment.size();i++) {
            var e= equipment.get(i);
            System.out.println("id: "+e.getId() + " " + e.getModel() + " " + e.getName() + " " + e.getState());
        }

        var blood = db.getBlood(1, "A", "available", "01/01/2026");
        for (int i = 0; i< blood.size(); i++) {
            var b= blood.get(i);
            System.out.println(b.id+" "+b.getDonorId() + " " + b.getBloodType() + " " + b.getStatus() + " " + b.getOperationId() + " " + b.getHarvestTime());
        }
        blood = db.getBloodById(blood.get(0).id);
        for (int i = 0; i< blood.size(); i++) {
            var b= blood.get(i);
            System.out.println("id  "+b.id+" "+b.getDonorId() + " " + b.getBloodType() + " " + b.getStatus() + " " + b.getOperationId() + " " + b.getHarvestTime());
        }

        var organ = db.getOrgan(1, "Lung", "available", "01/01/2026");
        for (int i = 0; i< organ.size(); i++) {
            var b= organ.get(i);
            System.out.println(b.getId()+" "+b.getDonorId() + " " + b.getName() + " " + b.getStatus() + " " + b.getOperationId() + " " + b.getHarvestTime());
        }
        organ = db.getOrganById(organ.get(0).getId());
        for (int i = 0; i< organ.size(); i++) {
            var b= organ.get(i);
            System.out.println("id "+b.getId()+" "+b.getDonorId() + " " + b.getName() + " " + b.getStatus() + " " + b.getOperationId() + " " + b.getHarvestTime());
        }

        var patroom = db.getPatientRoom("1A");
        for (int i=0; i < patroom.size() ;i++) {
            var p = patroom.get(i);
            System.out.println(p.getId() + " " + p.getPatientId() + " " + p.getName() + " "+ p.isFree());
        }
        patroom = db.getPatientRoomById(patroom.get(0).getId());
        for (int i=0; i < patroom.size() ;i++) {
            var p = patroom.get(i);
            System.out.println("id: "+p.getId() + " " + p.getPatientId() + " " + p.getName() + " "+ p.isFree());
        }
        patroom = db.getFreePatientRooms();
        for (int i=0; i < patroom.size() ;i++) {
            var p = patroom.get(i);
            System.out.println("free: " + p.getId() + " " + p.getPatientId() + " " + p.getName() + " "+ p.isFree());
        }

        var oproom = db.getOperationRoom("2A");
        for (int i=0; i < oproom.size(); i++) {
            var o = oproom.get(i);
            System.out.println(o.getId() + " " + o.getName());
        }
        oproom = db.getOperatingRoomById(oproom.get(0).getId());
        for (int i=0; i < oproom.size(); i++) {
            var o = oproom.get(i);
            System.out.println("id: "+o.getId() + " " + o.getName());
        }*/

        // delete tests
        /*System.out.println(db.deletePatient(1));
        System.out.println(db.deleteStaff(1));
        System.out.println(db.deleteDoner(1));
        System.out.println(db.deleteOperation( 1));
        System.out.println(db.deleteEquipment(1));
        System.out.println(db.deleteBlood(1));
        System.out.println(db.deleteOrgan(1));
        System.out.println(db.deletePatientRoom(1));
        System.out.println(db.deleteOperatingRoom(1));
        System.out.println(db.deleteOperationStaff(1, 1));
        System.out.println(db.deleteOperationOperatingRoom(1, 1));
        System.out.println(db.deleteOperationEquipment(1, 1));*/
    }
}
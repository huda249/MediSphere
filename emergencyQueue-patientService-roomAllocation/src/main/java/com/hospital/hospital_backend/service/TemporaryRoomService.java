package com.hospital.hospital_backend.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service 
public class TemporaryRoomService {
    
    //temporary until full implementation
    private final Set<Integer> roomsAvailable = new HashSet<>(Set.of(101, 103, 105, 107, 109));

    //reserving a room
    public synchronized boolean reserve(int roomNum){

         //validation that room is available
         if(!roomsAvailable.contains(roomNum)){
            return false;
         }

         //removing reserved room from the list of available rooms
         roomsAvailable.remove(roomNum);

         return true;
    }

    //releasing a previously reserved room
    //so it can be reserved for other patients
    public synchronized boolean release(int roomNum){

        //validating the room number
        if(!Set.of(101,103,105,107,109).contains(roomNum)){
            return false;
        }

        //checking if the room has already been released/is available
        if(roomsAvailable.contains(roomNum)){
            return false;
        }

        //adding the room back to the list of available rooms
        roomsAvailable.add(roomNum);

        return true;
    }
}

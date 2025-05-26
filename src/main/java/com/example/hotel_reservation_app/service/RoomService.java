package com.example.hotel_reservation_app.service;

import com.example.hotel_reservation_app.entity.Room;
import com.example.hotel_reservation_app.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Room> getAllAvailableRooms() {
        return roomRepository.findByAvailableTrue();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    
    public List<Room> searchRooms(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllAvailableRooms();
        }
        String jpql = "SELECT r FROM Room r WHERE r.available = true AND (LOWER(r.type) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :term, '%')))";
        TypedQuery<Room> query = entityManager.createQuery(jpql, Room.class);
        query.setParameter("term", searchTerm);
        try {
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error during room search: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    public void addSampleRooms() {
        if (roomRepository.count() == 0) {
            roomRepository.save(new Room("101", "Single", 50.00));
            roomRepository.save(new Room("102", "Single", 55.00));
            roomRepository.save(new Room("201", "Double", 80.00));
            roomRepository.save(new Room("202", "Double", 85.00));
            roomRepository.save(new Room("301", "Suite", 150.00));
        }
    }
}

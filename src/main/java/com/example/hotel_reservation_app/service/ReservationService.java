package com.example.hotel_reservation_app.service;

import com.example.hotel_reservation_app.entity.Reservation;
import com.example.hotel_reservation_app.entity.Room;
import com.example.hotel_reservation_app.entity.User;
import com.example.hotel_reservation_app.repository.ReservationRepository;
import com.example.hotel_reservation_app.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Reservation createReservation(User user, Long roomId, String guestName, LocalDate checkIn, LocalDate checkOut, MultipartFile idCardFile) throws IOException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.isAvailable()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        String originalFileName = idCardFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
      
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(idCardFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setGuestName(guestName); 
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setIdCardFilename(uniqueFileName); 

        room.setAvailable(false); 
        roomRepository.save(room);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    @Transactional
    public void cancelReservation(Long reservationId, User currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + reservationId));

        // Optional: Check if the current user is allowed to cancel this reservation
        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            // Or if admin, allow. For simplicity, we'll assume user can only cancel their own.
            throw new SecurityException("You are not authorized to cancel this reservation.");
        }

        Room room = reservation.getRoom();
        if (room != null) {
            room.setAvailable(true);
            roomRepository.save(room);
        } else {
            System.err.println("Warning: Reservation ID " + reservationId + " has no associated room.");
        }

        Path idCardPath = Paths.get(uploadDir, reservation.getIdCardFilename());
        try {
             Files.deleteIfExists(idCardPath);
        } catch (IOException e) {
             System.err.println("Could not delete ID card file: " + idCardPath + " - " + e.getMessage());
        }

        reservationRepository.delete(reservation);
    }
}

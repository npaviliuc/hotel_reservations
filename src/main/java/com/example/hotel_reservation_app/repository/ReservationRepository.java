package com.example.hotel_reservation_app.repository;

import com.example.hotel_reservation_app.entity.Reservation;
import com.example.hotel_reservation_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
}

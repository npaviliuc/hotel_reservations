package com.example.hotel_reservation_app.controller;

import com.example.hotel_reservation_app.entity.Reservation;
import com.example.hotel_reservation_app.entity.Room;
import com.example.hotel_reservation_app.entity.User;
import com.example.hotel_reservation_app.service.ReservationService;
import com.example.hotel_reservation_app.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/reserve/{roomId}")
    public String showReservationForm(@PathVariable Long roomId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        model.addAttribute("room", room);
        model.addAttribute("reservation", new Reservation()); 
        return "reservation_form";
    }

    @PostMapping("/reserve")
    public String makeReservation(@RequestParam Long roomId,
                                  @RequestParam String guestName, 
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                  @RequestParam("idCard") MultipartFile idCardFile,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (idCardFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ID Card file is required.");
            return "redirect:/reserve/" + roomId;
        }

        try {
            reservationService.createReservation(loggedInUser, roomId, guestName, checkInDate, checkOutDate, idCardFile);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation successful!");
            return "redirect:/my-reservations";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "File upload failed: " + e.getMessage());
            return "redirect:/reserve/" + roomId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reservation failed: " + e.getMessage());
            return "redirect:/reserve/" + roomId;
        }
    }

    @GetMapping("/my-reservations")
    public String myReservations(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        List<Reservation> reservations = reservationService.getReservationsByUser(loggedInUser);
        model.addAttribute("reservations", reservations);
        // Guest name in reservations can contain XSS payload
        return "my_reservations";
    }

    @PostMapping("/reservations/cancel")
    @Transactional
    public String cancelReservation(@RequestParam Long reservationId, RedirectAttributes redirectAttributes, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            reservationService.cancelReservation(reservationId, loggedInUser);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not cancel reservation: " + e.getMessage());
        }
        return "redirect:/my-reservations";
    }
}



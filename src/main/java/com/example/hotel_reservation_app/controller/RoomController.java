package com.example.hotel_reservation_app.controller;

import com.example.hotel_reservation_app.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/")
    public String homePage(Model model) {
        roomService.addSampleRooms();
        model.addAttribute("rooms", roomService.getAllAvailableRooms());
        return "index";
    }

    @GetMapping("/rooms")
    public String listRooms(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("rooms", roomService.searchRooms(search));
            model.addAttribute("searchTerm", search);
        } else {
            model.addAttribute("rooms", roomService.getAllAvailableRooms());
        }
        return "rooms";
    }
}

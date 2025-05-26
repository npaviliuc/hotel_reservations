package com.example.hotel_reservation_app.service;

import com.example.hotel_reservation_app.entity.User;
import com.example.hotel_reservation_app.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User registerUser(String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        String u = username != null ? username.replace("'", "''") : "";
        String p = password != null ? password.replace("'", "''") : "";
        String fn = firstName != null ? firstName.replace("'", "''") : "";
        String ln = lastName != null ? lastName.replace("'", "''") : "";
        String e = email != null ? email.replace("'", "''") : "";
        String ph = phoneNumber != null ? phoneNumber.replace("'", "''") : "";

        String sql = "INSERT INTO users (username, password, first_name, last_name, email, phone_number) VALUES ('" +
                u + "', '" + p + "', '" + fn + "', '" + ln + "', '" + e + "', '" + ph + "')";

        System.out.println("Executing INSECURE SQL for registration (INSERT): " + sql);

        try {
            int rowsAffected = entityManager.createNativeQuery(sql).executeUpdate();

            if (rowsAffected > 0) {
                String findUserSql = "SELECT * FROM users WHERE username = '" + u + "'";
                Query findUserQuery = entityManager.createNativeQuery(findUserSql, User.class);
                List<User> users = findUserQuery.getResultList();
                if (!users.isEmpty()) {
                    return users.get(0);
                } else {
                    System.err.println("User inserted via raw SQL, but could not be re-fetched by username: " + u);
                    User unmanagedUser = new User(username, password, firstName, lastName, email, phoneNumber);
                    return unmanagedUser;
                }
            } else {
                System.err.println("Raw SQL insert for registration failed to affect any rows.");
                return null;
            }
        } catch (Exception ex) {
            System.err.println("Error during raw SQL registration: " + ex.getMessage());
            return null;
        }
    }



    public Optional<User> loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        System.out.println("Executing SQL: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, User.class);
            List<User> users = query.getResultList();
            if (!users.isEmpty()) {
                return Optional.of(users.get(0));
            }
        } catch (Exception e) {
            System.err.println("Login query failed: " + e.getMessage());
        }
        return Optional.empty();
    }
}

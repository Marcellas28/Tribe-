package com.dayworks_ltd.loyalty_engine.auth.repository;

import com.dayworks_ltd.loyalty_engine.auth.enums.Status;
import com.dayworks_ltd.loyalty_engine.auth.enums.UserRole;
import com.dayworks_ltd.loyalty_engine.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repo;
    @Test
    void addUser() {
        String username, password, role, status;
        username = "Lim3";
        password = "Limau";
        role = UserRole.LEAD_COLLECTOR.name();
        status = Status.ACTIVE.name();

        int affectedRows = repo.addUser(
                username,
                password,
                role,
                status,
                null
        );

        System.out.println("Rows Added = " + affectedRows);
    }

    @Test
    void getAllUsers() {
        List<User> users = repo.getAllUsers();

        for( User user : users )
        {
            System.out.println("user = " + user.getUsername());
        }

    }

    @Test
    void getUsersByRole() {

        List<User> users = repo.getUsersByRole(UserRole.ADMIN.name());

        for( User user : users )
        {
            System.out.println("Admin = " + user.getUsername());
        }

        users = repo.getUsersByRole(UserRole.LEAD_COLLECTOR.name());

        for( User user : users )
        {
            System.out.println("Collector = " + user.getUsername());
        }
    }

    @Test
    void getUserById() {
        User user = repo.getUserById(3L);

        System.out.println("user = " + user.getUsername());
    }

    @Test
    void updateUser() {

        String username, password, role, status;
        username = "Juma";
        password = "Jum@";
        role = UserRole.ADMIN.name();
        status = Status.SUSPENDED.name();

        int rowsAffected = repo.updateUsername(2L, username);
        System.out.println("rowsAffected = " + rowsAffected);

        rowsAffected = repo.updatePassword(2L, password);
        System.out.println("rowsAffected = " + rowsAffected);

        rowsAffected = repo.updateRole(2L, role);
        System.out.println("rowsAffected = " + rowsAffected);

        rowsAffected = repo.updateStatus(2L, status);
        System.out.println("rowsAffected = " + rowsAffected);
    }
}
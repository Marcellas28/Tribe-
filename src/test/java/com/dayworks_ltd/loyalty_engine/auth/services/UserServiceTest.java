package com.dayworks_ltd.loyalty_engine.auth.services;

import com.dayworks_ltd.loyalty_engine.auth.DTO.LoginDto;
import com.dayworks_ltd.loyalty_engine.auth.DTO.UpdateDto;
import com.dayworks_ltd.loyalty_engine.auth.DTO.UserDto;
import com.dayworks_ltd.loyalty_engine.auth.enums.Status;
import com.dayworks_ltd.loyalty_engine.auth.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        for( UserDto user : users )
        {
            System.out.println("user = " + user.getUsername() );
        }
    }

    @Test
    void getUsersByRole() {
        List<UserDto> users = userService.getUsersByRole(UserRole.ADMIN);

        for( UserDto user : users )
        {
            System.out.println("user = " + user.getUsername() );
        }

        users = userService.getUsersByRole(UserRole.LEAD_COLLECTOR);

        for( UserDto user : users )
        {
            System.out.println("user = " + user.getUsername() );
        }
    }

    @Test
    void getUserById() {
        UserDto user = userService.getUserById(2L);

        System.out.println("user = " + user.getUsername());
    }

    @Test
    void addUser() {
        UserDto user = UserDto.builder()
                .username("Hilton")
                .password("hilton")
                .role(UserRole.LEAD_COLLECTOR.name())
                .status(Status.ACTIVE.name())
                .build();

        int rowsAffected = userService.addUser(user);

        System.out.println("Rows Affected = " + rowsAffected);
    }

    @Test
    void updateUser() {
        String username = "Julia";
        String password = "Jul1@";
        UserRole role = UserRole.ADMIN;
        Status status = Status.SUSPENDED;

        int rows = userService.updateUser(UpdateDto.builder()
                .id(2L + "")
                .attributeName("username")
                .attributeValue(username)
                .build());
        System.out.println("rows affected: " + rows);

        rows = userService.updateUser(UpdateDto.builder()
                .id(2L + "")
                .attributeName("password")
                .attributeValue(password)
                .build());
        System.out.println("rows affected: " + rows);

        rows = userService.updateUser(UpdateDto.builder()
                .id(2L + "")
                .attributeName("role")
                .attributeValue(role.name())
                .build());
        System.out.println("rows affected: " + rows);

        rows = userService.updateUser(UpdateDto.builder()
                .id(2L + "")
                .attributeName("status")
                .attributeValue(status.name())
                .build());
        System.out.println("rows affected: " + rows);
    }

    @Test
    void getUserLoginCredentials() {
        LoginDto login = userService.getUserLoginCredentials();

        System.out.println("username = " + login.getUsername() + "\tPassword = " + login.getPassword());
    }
}
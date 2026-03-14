package com.dayworks_ltd.loyalty_engine.auth.controller;

import com.dayworks_ltd.loyalty_engine.auth.DTO.UpdateDto;
import com.dayworks_ltd.loyalty_engine.auth.DTO.UserDto;
import com.dayworks_ltd.loyalty_engine.auth.enums.UserRole;
import com.dayworks_ltd.loyalty_engine.auth.services.UserService;
import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/get/all")
    public ApiResponseBody getAllUsers()
    {
        ApiResponseBody response;
        List<UserDto> users = new ArrayList<>();

        try{
            service.getAllUsers();

            response = ApiResponseBody.builder()
                    .status("200")
                    .message("success")
                    .respObject(users)
                    .build();
        }catch (Exception e)
        {
            response = ApiResponseBody.builder()
                    .status("500")
                    .message("failed")
                    .respObject(users)
                    .build();
        }

        return response;
    }

    @GetMapping("/get/by-role")
    public ApiResponseBody getUsersByRole(@RequestBody UserRole role)
    {
        ApiResponseBody response;
        List<UserDto> users = new ArrayList<>();

        try{
            service.getUsersByRole(role);

            response = ApiResponseBody.builder()
                    .status("200")
                    .message("success")
                    .respObject(users)
                    .build();
        }catch (Exception e)
        {
            response = ApiResponseBody.builder()
                    .status("500")
                    .message("failed")
                    .respObject(users)
                    .build();
        }

        return response;
    }

    @GetMapping("/get/by-id/{userId}")
    public ApiResponseBody getUserById( @PathVariable Long userId)
    {
        ApiResponseBody response;
        UserDto user = null;

        try{
            user = service.getUserById(userId);

            response = ApiResponseBody.builder()
                    .status("200")
                    .message("success")
                    .respObject(user)
                    .build();
        }catch (Exception e)
        {
            response = ApiResponseBody.builder()
                    .status("500")
                    .message("failed")
                    .respObject(user)
                    .build();
        }

        return response;
    }

    @PutMapping("/update")
    public ApiResponseBody updateUser(@RequestBody UpdateDto update)
    {
        ApiResponseBody response;

        int rowsAffected = 0;

        try{
            rowsAffected = service.updateUser(update);

            response = ApiResponseBody.builder()
                    .status("200")
                    .message("success")
                    .respObject("rowsAffected: " + rowsAffected)
                    .build();
        }catch (Exception e)
        {
            response = ApiResponseBody.builder()
                    .status("500")
                    .message("failed")
                    .respObject("rowsAffected: " + rowsAffected)
                    .build();
        }

        return response;
    }

    @PostMapping("/add")
    public ApiResponseBody addUser( @RequestBody UserDto user)
    {
        ApiResponseBody response;
        int rowsAffected = 0;

        try{
            rowsAffected = service.addUser(user);

            response = ApiResponseBody.builder()
                    .status("200")
                    .message("success")
                    .respObject("rowsAffected: " + rowsAffected)
                    .build();
        }catch (Exception e)
        {
            response = ApiResponseBody.builder()
                    .status("500")
                    .message("failed")
                    .respObject("rowsAffected: " + rowsAffected)
                    .build();
        }

        return response;
    }
}

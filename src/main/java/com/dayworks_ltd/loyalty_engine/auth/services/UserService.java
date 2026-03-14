package com.dayworks_ltd.loyalty_engine.auth.services;

import com.dayworks_ltd.loyalty_engine.auth.DTO.LoginDto;
import com.dayworks_ltd.loyalty_engine.auth.DTO.UpdateDto;
import com.dayworks_ltd.loyalty_engine.auth.DTO.UserDto;
import com.dayworks_ltd.loyalty_engine.auth.enums.UserRole;
import com.dayworks_ltd.loyalty_engine.auth.model.User;
import com.dayworks_ltd.loyalty_engine.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private List<UserDto> userToUserDtoMapper(List<User> users)
    {
        return users.stream().map(user -> UserDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build() )
                .collect(Collectors.toList() );
    }

    public List<UserDto> getAllUsers()
    {
        List<User> users = userRepository.getAllUsers();

        return userToUserDtoMapper(users);
    }

    public List<UserDto> getUsersByRole(UserRole role)
    {
        List<User> users = userRepository.getUsersByRole(role.name());

        return userToUserDtoMapper(users);
    }

    public UserDto getUserById( Long userId )
    {
        User user = userRepository.getUserById(userId);

        return UserDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }

    public int addUser(UserDto user)
    {
        return userRepository.addUser(
                user.getUsername(),
                bCryptPasswordEncoder.encode(user.getPassword()),
                user.getRole(),
                user.getStatus(),
                user.getMerchantId()
        );
    }

    public int updateUser(UpdateDto update)
    {
        switch( update.getAttributeName() )
        {
            case "username":
                return userRepository.updateUsername(
                        Long.parseLong(update.getId()),
                        update.getAttributeValue()
                );

            case "password":
                return userRepository.updatePassword(
                        Long.parseLong(update.getId()),
                        update.getAttributeValue()
                );

            case "role":
                return userRepository.updateRole(
                        Long.parseLong(update.getId()),
                        update.getAttributeValue()
                );

            case "status":
                return userRepository.updateStatus(
                        Long.parseLong(update.getId()),
                        update.getAttributeValue()
                );

            default:
                return -1;
        }
    }

    public LoginDto getUserLoginCredentials()
    {
        User user = userRepository.getUserById(1L);

        return LoginDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

}

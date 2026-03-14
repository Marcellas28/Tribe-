package com.dayworks_ltd.loyalty_engine.auth.controller;

import com.dayworks_ltd.loyalty_engine.auth.DTO.AuthToken;
import com.dayworks_ltd.loyalty_engine.auth.DTO.LoginDto;
import com.dayworks_ltd.loyalty_engine.auth.model.CustomUserDetails;
import com.dayworks_ltd.loyalty_engine.auth.services.CustomUserDetailsService;
import com.dayworks_ltd.loyalty_engine.auth.services.JWTService;
import com.dayworks_ltd.loyalty_engine.auth.services.UserService;
import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;
import com.dayworks_ltd.loyalty_engine.utility.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ApiResponseBody userLogin(@RequestBody LoginDto loginCredentials)
    {
        ApiResponseBody response;

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginCredentials.getUsername(),
                            loginCredentials.getPassword()
                    )
            );

            CustomUserDetails user = userDetailsService.loadUserByUsername(loginCredentials.getUsername());

            String jwt = jwtService.generateToken(user);
            AuthToken token = AuthToken.builder()
                    .jwt(jwt)
                    .build();

            if(authentication.isAuthenticated() )
            {
                response = ApiResponseBody.builder()
                        .status("200")
                        .message("success")
                        .respObject( new Pair<String, String>("jwt", jwt))
                        .build();
            }
            else {
                response = ApiResponseBody.builder()
                        .status("200")
                        .message("success")
                        .respObject(new Pair<String, String>( "reason", "invalid username or password"))
                        .build();
            }
        }
        catch (Exception e)
        {
            response = ApiResponseBody.builder()
                    .status("401")
                    .message("failed")
                    .respObject(new Pair<String, String>("reason", e.getMessage()))
                    .build();
        }


        return response;
    }
}

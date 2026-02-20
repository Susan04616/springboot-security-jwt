package io.getarrays.userservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j // Lombok: gives you a logger called "log"
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // Spring Security uses this to authenticate username + password
    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * This method runs when a user tries to log in.
     * Spring Security calls this automatically.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        // Read username and password from the HTTP request
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        log.info("Username is: {}", username);

        // Create authentication token with username + password
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // Spring Security will call UserDetailsService.loadUserByUsername() here
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * This method runs AFTER successful login.
     * This is where we generate the JWT token.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication)
            throws IOException, ServletException {

        // This is the authenticated user returned by Spring Security
        User user = (User) authentication.getPrincipal();

        // Algorithm used to sign the JWT (secret key)
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        // Create JWT access token
        String accessToken = JWT.create()
                .withSubject(user.getUsername()) // username inside token
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 minutes
                .withIssuer(request.getRequestURL().toString()) // who issued token
                .withClaim("roles", // add user roles into the token
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(algorithm); // sign token

        //Create refresh token
        String refresh_Token = JWT.create()
                .withSubject(user.getUsername()) // username inside token
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 10 minutes
                .withIssuer(request.getRequestURL().toString()) // who issued token
                .sign(algorithm); // sign token

        // Send token back to client in response header

        /*response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refresh_Token);*/

        // Prepare JSON response
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token",refresh_Token);

        // Tell browser/client this is JSON
        response.setContentType(APPLICATION_JSON_VALUE);

        // Write JSON to response body
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }
}

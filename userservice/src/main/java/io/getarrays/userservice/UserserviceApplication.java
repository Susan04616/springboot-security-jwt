package io.getarrays.userservice;

import io.getarrays.userservice.model.Role;
import io.getarrays.userservice.model.User;
import io.getarrays.userservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {

        SpringApplication.run(UserserviceApplication.class, args);
	}

    //----------------------------------------Sercurity-------------------------------------------------
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //----------------------------------------Sercurity-------------------------------------------------


    @Bean
    CommandLineRunner run(UserService userService){
        return orgs -> {

            //Create roles
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            //Create roles
            userService.saveUser(new User(null, "John Travolta", "john", "1234", new ArrayList<>()));
            userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>()));
            userService.saveUser(new User(null, "Jim Carry", "jim", "1234", new ArrayList<>()));
            userService.saveUser(new User(null, "Arnold Phasha", "arnold", "1234", new ArrayList<>()));

            //Add role to user
            userService.addRoleToUser("john", "ROLE_USER" );
            userService.addRoleToUser("john", "ROLE_MANAGER" );
            userService.addRoleToUser("will", "ROLE_MANAGER" );
            userService.addRoleToUser("jim", "ROLE_ADMIN" );
            userService.addRoleToUser("arnold", "ROLE_SUPER_ADMIN" );
            userService.addRoleToUser("arnold", "ROLE_ADMIN" );
            userService.addRoleToUser("arnold", "ROLE_USER" );

        };
    }

}

package io.getarrays.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.AUTO;

@Entity  //Map the table to the class
@Data    //comes from Lombok, automatically generates getters and setters
@NoArgsConstructor //generates constructor with no arguments
@AllArgsConstructor//Generates a constructor with all fields as parameters.
public class Role {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String name;
}

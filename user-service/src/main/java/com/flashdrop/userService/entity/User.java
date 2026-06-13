package com.flashdrop.userService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    @CreationTimestamp
    private LocalDate createDate;
}

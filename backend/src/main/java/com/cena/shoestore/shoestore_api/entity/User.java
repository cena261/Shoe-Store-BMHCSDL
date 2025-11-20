package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    Long userId;

    @Column(name = "EMAIL", nullable = false, length = 255)
    String email;

    @Column(name = "PASSWORDHASH", nullable = false, length = 255)
    String passwordHash;

    @Column(name = "FULLNAME", nullable = false, length = 100)
    String fullName;

    @Column(name = "PHONE", length = 20)
    String phone;

    @Column(name = "CREATEDAT", nullable = false)
    Instant createdAt;

    @Column(name = "LASTLOGIN")
    Instant lastLogin;

    // số 0/1 → Integer
    @Column(name = "ISACTIVE", nullable = false)
    Integer isActive;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<UserRole> userRoles;
}

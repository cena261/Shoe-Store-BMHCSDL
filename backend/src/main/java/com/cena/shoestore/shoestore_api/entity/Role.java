package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLEID")
    Long roleId;

    @Column(name = "ROLENAME", nullable = false, length = 100)
    String roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    Set<UserRole> userRoles;
}

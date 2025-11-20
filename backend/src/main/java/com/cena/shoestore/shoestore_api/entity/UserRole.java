package com.cena.shoestore.shoestore_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@IdClass(UserRoleId.class)
@Table(name = "USERROLES")
public class UserRole {

    @Id
    @Column(name = "USERID")
    Long userId;

    @Id
    @Column(name = "ROLEID")
    Long roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID", insertable = false, updatable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLEID", insertable = false, updatable = false)
    Role role;

    @Column(name = "ASSIGNEDAT", nullable = false)
    Instant assignedAt;
}

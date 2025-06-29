package com.example.pdp_project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String password;
    String phone;
    String verificationCode;
    String name;
    Boolean isOnline;
    Boolean isActive;
    Boolean isVerified;
    @OneToOne
    Attachment photo;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(getIsActive());
    }
}

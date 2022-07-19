package com.todoay.api.domain.auth.entity;

import com.todoay.api.domain.profile.entity.Profile;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter @NoArgsConstructor
public class Auth implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // IDENTITY, TABLE, SEQUENCE
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "auth", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Profile profile;


    @Builder  //이게 있으면 쉽게 객체 생성이 가능하다
    public Auth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void associateWithProfile(Profile profile) {
        this.profile = profile;
        profile.setAuth(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

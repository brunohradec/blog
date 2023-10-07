package me.bhradec.blog.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.bhradec.blog.domain.enumerations.AppUserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private AppUserRole role;

    @OneToMany(mappedBy = "author")
    private List<Post> posts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /* Users can have ADMIN or USER role in this app, but Spring security
         * treats roles as a collection, so users can have multiple roles
         * if needed. */
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // There is no account expiration in this application.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // There is no account locking in this application.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // There is no credential expiration in this application.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // There is no account disabling in this application.
        return true;
    }
}

package tim.labs.labs.database.entity;

import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.exception.InvalidUserCredentialsException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "users")
@Data
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    @Getter
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private Role role;

    public void setUsername(String username) throws InvalidUserCredentialsException {
        if (username == null) throw new InvalidUserCredentialsException();
        this.username = username;
    }

    public void setPassword(String password) throws InvalidUserCredentialsException {
        if (password == null) throw new InvalidUserCredentialsException();
        this.password = password;
    }

    public void setRole(Role role) throws InvalidUserCredentialsException {
        this.role = role;
    }

    public String getPermission() {
        return this.role.toString();
    }
    public boolean Validate() {
        return true;
    }
}

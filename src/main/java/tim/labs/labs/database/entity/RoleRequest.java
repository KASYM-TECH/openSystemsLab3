package tim.labs.labs.database.entity;


import tim.labs.labs.database.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean fulfilled;
}

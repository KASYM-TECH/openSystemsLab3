package tim.labs.labs.database.entity;

import tim.labs.labs.database.entity.enums.Color;
import tim.labs.labs.database.entity.enums.Country;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    private Color hairColor;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Location location;

    private double height;

    @Enumerated(EnumType.STRING)
    private Country nationality;

    @Column(nullable = false)
    private Boolean allowAdminEdit;

    @Column(nullable = false)
    private long creatorId;

    public Long getId() {
        return this.id;
    }

    public boolean Validate() {
        return true;
    }
}

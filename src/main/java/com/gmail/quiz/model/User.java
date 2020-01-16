package com.gmail.quiz.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "usr")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    private long id;

    @Column(name = "user_name", nullable = false, unique = true, length = 30)  //  used in schema generation
    @NotBlank  // validate field before execute SQL statement
    @Size(max = 30)  // @Length(max = 30) -- Hibernate specific
    @NonNull // validate field creating object
    private String userName;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    @NonNull
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;

    @Column(nullable = false)
    @NotNull
    @EqualsAndHashCode.Exclude
    @Getter(AccessLevel.NONE)
    private Boolean enabled;

    @Column(nullable = false)
    @NotEmpty
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role") //  @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public boolean isEnabled() {
        return enabled == null ? false : enabled;
    }
}

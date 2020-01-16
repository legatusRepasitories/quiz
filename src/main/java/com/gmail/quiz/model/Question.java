package com.gmail.quiz.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    private long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank
    @NonNull
    private String text;

    @Column(nullable = false)
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "right_variant")
    @NonNull
    private List<String> rightVariants;

    @Column(nullable = false)
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "wrong_variant")
    @NonNull
    private List<String> wrongVariants;
}

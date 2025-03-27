package org.fusif.game_detector.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "rawg_game")
public class RawgGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "released")
    private LocalDate released;

    @Column(name = "tba")
    private Boolean tba;

    @Column(name = "background_image", length = 256)
    private String backgroundImage;

    @OneToOne
    private Application application;

}
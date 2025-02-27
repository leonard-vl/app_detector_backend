package org.fusif.game_detector.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "session", schema = "game_detector")
public final class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "session_start", nullable = false)
    private Instant sessionStart;

    @Column(name = "session_stop", nullable = false)
    private Instant sessionStop;

    @Column(name = "game_name", nullable = false, length = 128)
    private String gameName;
}
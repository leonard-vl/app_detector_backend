package org.fusif.game_detector.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SessionDTO {
    private String gameName;
    private Instant sessionStart;
    private Instant sessionStop;
}

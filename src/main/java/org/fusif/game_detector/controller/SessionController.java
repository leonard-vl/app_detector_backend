package org.fusif.game_detector.controller;

import lombok.AllArgsConstructor;
import org.fusif.game_detector.entity.Session;
import org.fusif.game_detector.service.SessionService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SessionController {
    SessionService sessionService;

    @PostMapping(path ="/session")
    public void createSession(@RequestBody Session session) {
        sessionService.saveSession(session);
    }

    @GetMapping(path = "/session/gamename")
    public Session getSessionByGameName(@RequestParam String gameName) {
        return sessionService.getSessionByGameName(gameName);
    }

    @GetMapping(path = "/session/id")
    public Session getSessionById(@RequestParam Long id) {
        return sessionService.getSessionById(id);
    }
}

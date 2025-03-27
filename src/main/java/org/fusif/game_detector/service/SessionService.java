package org.fusif.game_detector.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.fusif.game_detector.entity.Session;
import org.fusif.game_detector.repository.ISessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Setter
@AllArgsConstructor
public class SessionService {
    ISessionRepository sessionRepository;

    public void saveSessions(List<Session> sessions) {
        sessionRepository.saveAll(sessions);
    }
}

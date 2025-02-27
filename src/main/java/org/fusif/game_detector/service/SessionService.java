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

    public void saveSession(Session session) {
        sessionRepository.save(session);
    }

    public void deleteSession(Session session) {
        sessionRepository.delete(session);
    }

    public Session getSessionById(Long id) {
        return sessionRepository.findById(id).orElseThrow();
    }

    public Session getSessionByGameName(String gameName) {
        return sessionRepository.findByGameName(gameName).orElseThrow();
    }

    public List<Session> getSessionsByGameName(String gameName) {
        return sessionRepository.findSessionsByGameName(gameName);
    }
}

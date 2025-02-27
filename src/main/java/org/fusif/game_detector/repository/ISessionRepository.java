package org.fusif.game_detector.repository;

import org.fusif.game_detector.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISessionRepository extends JpaRepository<Session, Long> {
    @Override
    Optional<Session> findById(Long aLong);
    Optional<Session> findByGameName(String gameName);
    Optional<Session> findSessionByGameName(String gameName);
    List<Session> findSessionsByGameName(String gameName);
}

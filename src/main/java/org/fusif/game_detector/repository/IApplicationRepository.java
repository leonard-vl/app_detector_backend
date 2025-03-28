package org.fusif.game_detector.repository;

import org.fusif.game_detector.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IApplicationRepository extends JpaRepository<Application, Integer> {
    Optional<Application> findApplicationByPath(String path);
    List<Application> findApplicationsBySaveSessionFalse();
}
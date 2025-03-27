package org.fusif.game_detector.repository;

import org.fusif.game_detector.entity.RawgGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawgGameRepository extends JpaRepository<RawgGame, Integer> {
}
package org.fusif.game_detector.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.fusif.game_detector.entity.Application;
import org.fusif.game_detector.repository.IApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Getter
@Setter
@AllArgsConstructor
public class GameService {
    IApplicationRepository iApplicationRepository;

    public Optional<Application> getGameByPath(String path) {
        return this.iApplicationRepository.findApplicationByPath(path);
    }
}

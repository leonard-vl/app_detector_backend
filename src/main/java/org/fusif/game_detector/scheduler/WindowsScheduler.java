package org.fusif.game_detector.scheduler;


import com.sun.jna.platform.WindowUtils;
import org.fusif.game_detector.entity.Application;
import org.fusif.game_detector.entity.Session;
import org.fusif.game_detector.model.DesktopWindowWrapper;
import org.fusif.game_detector.service.GameService;
import org.fusif.game_detector.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WindowsScheduler {
    SessionService sessionService;
    GameService gameService;

    Set<DesktopWindowWrapper> previousWindows = new HashSet<>();
    Set<Session> runningSessions = new HashSet<>();

    @Autowired
    public WindowsScheduler(SessionService sessionService, GameService gameService) {
        this.sessionService = sessionService;
        this.gameService = gameService;
    @PreDestroy
    public void shutdown() {
        this.runningSessions.forEach(s -> s.setSessionStop(Instant.now()));

        this.saveSessions(runningSessions);
    }

    @Scheduled(fixedRate = 5000)
    public void updateWindows() {
        Set<DesktopWindowWrapper> currentWindows = getCurrentWindows(true);

        Set<DesktopWindowWrapper> closedWindows = getClosedWindows(currentWindows);
        Set<DesktopWindowWrapper> newWindows = getNewWindows(currentWindows);

        previousWindows = currentWindows;

        this.initialiseSessions(newWindows);
        this.closeSessions(closedWindows);

        this.saveSessions(runningSessions);
    }

    public void saveSessions(Set<Session> sessions) {
        List<Session> completeSessions = sessions.stream()
                .filter(s -> s.getSessionStop() != null)
                .toList();

        this.runningSessions = sessions.stream()
                .filter(s -> s.getSessionStop() == null)
                .collect(Collectors.toSet());

        this.sessionService.saveSessions(completeSessions);
    }

    public void initialiseSessions(Set<DesktopWindowWrapper> newWindows) {
        newWindows.forEach(w -> {
            String gamePath = w.getWindow().getFilePath();

            Optional<Application> existingApplication = this.gameService.getGameByPath(gamePath);
            Application application;

            if (existingApplication.isPresent()) {
                application = existingApplication.get();
            } else {
                application = new Application();
                application.setPath(gamePath);
                application.setTitle(w.getWindow().getTitle());
            }

            Session session = new Session();
            session.setApplication(application);
            session.setSessionStart(Instant.now());

            runningSessions.add(session);
        });
    }

    public void closeSessions(Set<DesktopWindowWrapper> closedWindows) {
        closedWindows.forEach(w -> runningSessions.forEach(s -> {
            if (s.getApplication().getPath().equals(w.getWindow().getFilePath())) {
                s.setSessionStop(Instant.now());
            }
        }));
    }

    public Set<DesktopWindowWrapper> getCurrentWindows(boolean onlyVisibleWindows) {
        Set<String> applicationsToIgnorePaths = this.applicationService.getApplicationsBySaveSessionFalse().stream()
                .map(Application::getPath)
                .collect(Collectors.toSet());

        return WindowUtils.getAllWindows(onlyVisibleWindows).stream()
                .filter(w -> !w.getTitle().isEmpty() && !applicationsToIgnorePaths.contains(w.getFilePath()))
                .map(DesktopWindowWrapper::new)
                .collect(Collectors.toSet());
    }

    public Set<DesktopWindowWrapper> getNewWindows(Set<DesktopWindowWrapper> currentWindows) {
        Set<DesktopWindowWrapper> newWindows = new HashSet<>(currentWindows);
        newWindows.removeAll(previousWindows);

        return newWindows;
    }

    public Set<DesktopWindowWrapper> getClosedWindows(Set<DesktopWindowWrapper> currentWindows) {
        Set<DesktopWindowWrapper> closedWindows = new HashSet<>(previousWindows);
        closedWindows.removeAll(currentWindows);

        return closedWindows;
    }
}

package org.fusif.game_detector.scheduler;


import com.sun.jna.platform.WindowUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.fusif.game_detector.entity.Application;
import org.fusif.game_detector.entity.Session;
import org.fusif.game_detector.model.DesktopWindowWrapper;
import org.fusif.game_detector.service.ApplicationService;
import org.fusif.game_detector.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WindowsScheduler {
    SessionService sessionService;
    ApplicationService applicationService;

    Set<DesktopWindowWrapper> previousWindows = new HashSet<>();
    Set<Session> runningSessions = new HashSet<>();

    @Autowired
    public WindowsScheduler(SessionService sessionService, ApplicationService applicationService) {
        this.sessionService = sessionService;
        this.applicationService = applicationService;
    }

    @PreDestroy
    public void shutdown() {
        var now = Instant.now();
        this.runningSessions.forEach(s -> s.setSessionStop(now));

        this.saveSessions(runningSessions);
        log.info("Saved {} running sessions on shutdown", runningSessions.size());
    }

    @Scheduled(fixedRate = 5000)
    public void updateWindows() {
        try {
            Set<DesktopWindowWrapper> currentWindows = getCurrentWindows(false);

            Set<DesktopWindowWrapper> closedWindows = getClosedWindows(currentWindows);
            Set<DesktopWindowWrapper> newWindows = getNewWindows(currentWindows);

            previousWindows = currentWindows;

            this.initialiseSessions(newWindows);
            this.closeSessions(closedWindows);

            this.saveSessions(runningSessions);
        } catch (Exception e) {
            log.error("Error updating windows", e);
        }
    }

    public void saveSessions(Set<Session> sessions) {
        List<Session> completeSessions = sessions.stream()
                .filter(s -> s.getSessionStop() != null)
                .toList();

        this.runningSessions = sessions.stream()
                .filter(s -> s.getSessionStop() == null)
                .collect(Collectors.toSet());

        if (!completeSessions.isEmpty()) {
            this.sessionService.saveSessions(completeSessions);
            log.info("Saved {} complete sessions", completeSessions.size());
        }
    }

    public void initialiseSessions(Set<DesktopWindowWrapper> newWindows) {
        List<Application> newApplicationsToSave = new ArrayList<>();
        var now = Instant.now();

        newWindows.forEach(w -> {
            Application application;
            String appPath = w.getWindow().getFilePath();

            Optional<Application> existingApplication = this.applicationService.getApplicationByPath(appPath);

            if (existingApplication.isPresent() && Boolean.TRUE.equals(!existingApplication.get().getSaveSession())) {
                return;
            }

            if (existingApplication.isPresent()) {
                application = existingApplication.get();
            } else {
                application = new Application();
                application.setPath(appPath);
                application.setTitle(w.getWindow().getTitle());
                application.setSaveSession(true);

                newApplicationsToSave.add(application);
            }

            Session session = new Session();
            session.setApplication(application);
            session.setSessionStart(now);

            runningSessions.add(session);
        });

        if (!newApplicationsToSave.isEmpty()) {
            this.applicationService.saveApplications(newApplicationsToSave);
            log.info("Saved {} new applications: {}", newApplicationsToSave.size(),
                    newApplicationsToSave.stream().map(Application::getTitle).collect(Collectors.joining(", "))
            );
        }
    }

    public void closeSessions(Set<DesktopWindowWrapper> closedWindows) {
        var now = Instant.now();
        closedWindows.forEach(w -> runningSessions.forEach(s -> {
            if (s.getApplication().getPath().equals(w.getWindow().getFilePath())) {
                s.setSessionStop(now);
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

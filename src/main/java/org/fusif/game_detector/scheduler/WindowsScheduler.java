package org.fusif.game_detector.scheduler;


import com.sun.jna.platform.WindowUtils;
import jakarta.annotation.PreDestroy;
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
        this.runningSessions.forEach(s -> s.setSessionStop(Instant.now()));

        this.saveSessions(runningSessions);
    }

    @Scheduled(fixedRate = 5000)
    public void updateWindows() {
        Set<DesktopWindowWrapper> currentWindows = getCurrentWindows(false);

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
        List<Application> newApplicationsToSave = new ArrayList<>();

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
            session.setSessionStart(Instant.now());

            runningSessions.add(session);
        });

        this.applicationService.saveApplications(newApplicationsToSave);
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

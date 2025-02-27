package org.fusif.game_detector.scheduler;


import com.sun.jna.platform.WindowUtils;
import org.fusif.game_detector.model.DesktopWindowWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WindowsScheduler {
    Set<DesktopWindowWrapper> previousWindows = new HashSet<>();

    @Scheduled(fixedRate = 5000)
    public void updateWindows() {
        Set<DesktopWindowWrapper> currentWindows = getCurrentWindows();

        Set<DesktopWindowWrapper> closedWindows = getClosedWindows(currentWindows);
        Set<DesktopWindowWrapper> newWindows = getNewWindows(currentWindows);

        previousWindows = currentWindows;

        updateNewWindows(newWindows);
        updateClosedWindows(closedWindows);
    }

    public Set<DesktopWindowWrapper> getCurrentWindows() {
        return WindowUtils.getAllWindows(true).stream()
                .filter(w -> !w.getTitle().isEmpty())
                .map(w -> new DesktopWindowWrapper(w) {})
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

    public void updateNewWindows(Set<DesktopWindowWrapper> newWindows) {
        System.out.println("New windows:");
        newWindows.forEach(w -> System.out.println(w.getWindow().getTitle()));
    }

    public void updateClosedWindows(Set<DesktopWindowWrapper> closedWindows) {
        System.out.println("Closed windows:");
        closedWindows.forEach(w -> System.out.println(w.getWindow().getTitle()));
    }
}

package org.fusif.game_detector.scheduler;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import org.fusif.game_detector.model.DesktopWindowWrapper;
import org.fusif.game_detector.service.ApplicationService;
import org.fusif.game_detector.service.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WindowsSchedulerTest {
    @Mock
    SessionService sessionService;

    @Mock
    ApplicationService applicationService;


    @Test
    void assert_get_only_windows_with_title() {

        DesktopWindow mockWindow1 = mock(DesktopWindow.class);
        when(mockWindow1.getTitle()).thenReturn("App 1");
        when(mockWindow1.getFilePath()).thenReturn("Path 1");

        DesktopWindow mockWindow2 = mock(DesktopWindow.class);
        when(mockWindow2.getTitle()).thenReturn("App 2");
        when(mockWindow2.getFilePath()).thenReturn("Path 2");

        DesktopWindow mockWindow3 = mock(DesktopWindow.class);
        when(mockWindow3.getTitle()).thenReturn("");


        try (MockedStatic<WindowUtils> windowUtilsMockedStatic = Mockito.mockStatic(WindowUtils.class)) {
            windowUtilsMockedStatic.when(() -> WindowUtils.getAllWindows(true))
                    .thenReturn(List.of(mockWindow1, mockWindow2, mockWindow3));

            WindowsScheduler windowsScheduler = new WindowsScheduler(sessionService, applicationService);
            Set<DesktopWindowWrapper> currentWindows = windowsScheduler.getCurrentWindows(true);

            assertEquals(2, currentWindows.size());
            assertTrue(currentWindows.stream().anyMatch(w -> w.getWindow().getTitle().equals("App 1")));
            assertTrue(currentWindows.stream().anyMatch(w -> w.getWindow().getTitle().equals("App 2")));
        }
    }

    @Test
    void assert_that_new_windows_are_updated() {
        DesktopWindowWrapper mockWrapper1 = mock(DesktopWindowWrapper.class);
        DesktopWindowWrapper mockWrapper2 = mock(DesktopWindowWrapper.class);
        DesktopWindowWrapper mockWrapper3 = mock(DesktopWindowWrapper.class);

        Set<DesktopWindowWrapper> previousWindows = Set.of(mockWrapper1, mockWrapper2);
        Set<DesktopWindowWrapper> currentWindows = Set.of(mockWrapper1, mockWrapper2, mockWrapper3);
        Set<DesktopWindowWrapper> newWindows = new HashSet<>(currentWindows);

        newWindows.removeAll(previousWindows);

        assertEquals(1, newWindows.size());
    }

    @Test
    void assert_that_closed_windows_are_updated() {
        DesktopWindowWrapper mockWrapper1 = mock(DesktopWindowWrapper.class);
        DesktopWindowWrapper mockWrapper2 = mock(DesktopWindowWrapper.class);
        DesktopWindowWrapper mockWrapper3 = mock(DesktopWindowWrapper.class);

        Set<DesktopWindowWrapper> previousWindows = Set.of(mockWrapper1, mockWrapper2, mockWrapper3);
        Set<DesktopWindowWrapper> currentWindows = Set.of(mockWrapper1, mockWrapper2);
        Set<DesktopWindowWrapper> closedWindows = new HashSet<>(previousWindows);

        closedWindows.removeAll(currentWindows);

        assertEquals(1, closedWindows.size());
    }
}
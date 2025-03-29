package org.fusif.game_detector.scheduler;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import org.fusif.game_detector.entity.Application;
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
    void assert_get_only_windows_with_title_with_saveSession_true() {
        DesktopWindow windowWithTitle1 = mock(DesktopWindow.class);
        when(windowWithTitle1.getTitle()).thenReturn("title1");
        when(windowWithTitle1.getFilePath()).thenReturn("Path1");

        DesktopWindow windowWithTitle2 = mock(DesktopWindow.class);
        when(windowWithTitle2.getTitle()).thenReturn("title2");
        when(windowWithTitle2.getFilePath()).thenReturn("Path2");

        DesktopWindow windowWithTitle3 = mock(DesktopWindow.class);
        when(windowWithTitle3.getTitle()).thenReturn("title3");
        when(windowWithTitle3.getFilePath()).thenReturn("path3");

        DesktopWindow windowWithTitle4 = mock(DesktopWindow.class);
        when(windowWithTitle4.getTitle()).thenReturn("title4");
        when(windowWithTitle4.getFilePath()).thenReturn("path4");

        DesktopWindow windowWithoutTitle1 = mock(DesktopWindow.class);
        when(windowWithoutTitle1.getTitle()).thenReturn("");
        when(windowWithTitle2.getFilePath()).thenReturn("Path5");

        Application application1 = mock(Application.class);
        Application application2 = mock(Application.class);
        when(application1.getPath()).thenReturn("path3");
        when(application2.getPath()).thenReturn("path4");

        when(applicationService.getApplicationsBySaveSessionFalse())
                .thenReturn(List.of(application1, application2));

        try (MockedStatic<WindowUtils> windowUtilsMockedStatic = Mockito.mockStatic(WindowUtils.class)) {
            windowUtilsMockedStatic.when(() -> WindowUtils.getAllWindows(true))
                    .thenReturn(List.of(
                            windowWithTitle1,
                            windowWithTitle2,
                            windowWithTitle3,
                            windowWithTitle4,
                            windowWithoutTitle1
                    ));

            WindowsScheduler windowsScheduler = new WindowsScheduler(sessionService, applicationService);
            Set<DesktopWindowWrapper> currentWindows = windowsScheduler.getCurrentWindows(true);

            assertEquals(2, currentWindows.size());
            assertTrue(currentWindows.stream().anyMatch(w -> w.getWindow().getTitle().equals("title1")));
            assertTrue(currentWindows.stream().anyMatch(w -> w.getWindow().getTitle().equals("title2")));
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
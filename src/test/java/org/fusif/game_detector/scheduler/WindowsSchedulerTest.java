package org.fusif.game_detector.scheduler;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import org.fusif.game_detector.model.DesktopWindowWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WindowsSchedulerTest {
    @Test
    void updateWindows() {
    }

    @Test
    void assert_filters_windows_without_title() {
        DesktopWindow mockWindow1 = mock(DesktopWindow.class);
        when(mockWindow1.getTitle()).thenReturn("Game 1");
        when(mockWindow1.getFilePath()).thenReturn("filePath1");

        DesktopWindow mockWindow2 = mock(DesktopWindow.class);
        when(mockWindow2.getTitle()).thenReturn("Game 2");
        when(mockWindow2.getFilePath()).thenReturn("filePath2");

        DesktopWindow mockWindow3 = mock(DesktopWindow.class);
        when(mockWindow3.getTitle()).thenReturn("");
        when(mockWindow3.getFilePath()).thenReturn("filePath3");

        try (MockedStatic<WindowUtils> windowUtilsMockedStatic = Mockito.mockStatic(WindowUtils.class)) {
            windowUtilsMockedStatic.when(() -> WindowUtils.getAllWindows(true)).thenReturn(List.of(mockWindow1, mockWindow2, mockWindow3));

            WindowsScheduler windowsScheduler = new WindowsScheduler();
            Set<DesktopWindowWrapper> currentWindows = windowsScheduler.getCurrentWindows();

            assertEquals(2, currentWindows.size());
            assertTrue(currentWindows.stream().anyMatch(w -> w.getWindow().getTitle().equals("Game 1")));
            assertTrue(currentWindows.stream().anyMatch(w -> w.getWindow().getTitle().equals("Game 2")));
        }
    }

    @Test
    void getNewWindows() {
    }

    @Test
    void getClosedWindows() {
    }

    @Test
    void updateNewWindows() {
    }

    @Test
    void updateClosedWindows() {
    }
}
package org.fusif.game_detector.model;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.win32.WinDef;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class DesktopWindowWrapperTest {

    @Test
    void testEquals() {
        DesktopWindowWrapper desktopWindowWrapper1 = new DesktopWindowWrapper(
                new DesktopWindow(any(WinDef.HWND.class), anyString(), "filePath", any(Rectangle.class))
        );

        DesktopWindowWrapper desktopWindowWrapper2 = new DesktopWindowWrapper(
                new DesktopWindow(any(WinDef.HWND.class), anyString(), "filePath", any(Rectangle.class))
        );

        assertEquals(desktopWindowWrapper1, desktopWindowWrapper2);
    }
}
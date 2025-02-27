package org.fusif.game_detector.model;

import com.sun.jna.platform.DesktopWindow;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesktopWindowWrapper {
    DesktopWindow window;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DesktopWindowWrapper that)) return false;

        return that.window.getFilePath().equals(this.window.getFilePath());
    }
}

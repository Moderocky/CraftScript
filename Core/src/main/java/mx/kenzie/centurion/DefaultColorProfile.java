package mx.kenzie.centurion;

import net.kyori.adventure.text.format.TextColor;

public interface DefaultColorProfile {

    static ColorProfile get() {
        return new ColorProfile(TextColor.color(224, 224, 224),
            TextColor.color(13, 150, 255), TextColor.color(18, 215, 255), TextColor.color(255, 226, 10));
    }

}

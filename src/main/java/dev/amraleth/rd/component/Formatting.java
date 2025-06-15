package dev.amraleth.rd.component;

import org.jetbrains.annotations.NotNull;

public class Formatting {

    public static @NotNull String formatSizeToStringRepresentation(int size) {
        String[] units = {"", "k", "M", "G", "T", "P"};
        double scaled = size;
        int unitIndex = 0;

        while (scaled >= 1024 && unitIndex < units.length - 1) {
            scaled /= 1024.0;
            unitIndex++;
        }

        return scaled % 1 == 0 ? String.format("%d%s", (int) scaled, units[unitIndex]) : String.format("%.1f%s", scaled, units[unitIndex]);
    }
}

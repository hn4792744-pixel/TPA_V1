package me.uc_hussein.ultrastpa.util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
public final class ColorUtil {
 private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
 private ColorUtil() {}
 public static Component component(String s){ return LEGACY.deserialize(s == null ? "" : s); }
 public static String legacy(String s){ return s == null ? "" : s.replace('&','§'); }
}

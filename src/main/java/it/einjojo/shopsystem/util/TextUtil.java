package it.einjojo.shopsystem.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for text manipulation
 */
public class TextUtil {
    private static final Map<Character, String> AMPERSAND_TO_MINIMESSAGE;
    public static final char AMPERSAND = '&';
    public static final char SECTION = '§';


    static {
        AMPERSAND_TO_MINIMESSAGE = new HashMap<>();
        AMPERSAND_TO_MINIMESSAGE.put('a', "<green>");
        AMPERSAND_TO_MINIMESSAGE.put('b', "<aqua>");
        AMPERSAND_TO_MINIMESSAGE.put('c', "<red>");
        AMPERSAND_TO_MINIMESSAGE.put('d', "<light_purple>");
        AMPERSAND_TO_MINIMESSAGE.put('e', "<yellow>");
        AMPERSAND_TO_MINIMESSAGE.put('f', "<white>");
        AMPERSAND_TO_MINIMESSAGE.put('0', "<black>");
        AMPERSAND_TO_MINIMESSAGE.put('1', "<dark_blue>");
        AMPERSAND_TO_MINIMESSAGE.put('2', "<dark_green>");
        AMPERSAND_TO_MINIMESSAGE.put('3', "<dark_aqua>");
        AMPERSAND_TO_MINIMESSAGE.put('4', "<dark_red>");
        AMPERSAND_TO_MINIMESSAGE.put('5', "<dark_purple>");
        AMPERSAND_TO_MINIMESSAGE.put('6', "<gold>");
        AMPERSAND_TO_MINIMESSAGE.put('7', "<gray>");
        AMPERSAND_TO_MINIMESSAGE.put('8', "<dark_gray>");
        AMPERSAND_TO_MINIMESSAGE.put('9', "<blue>");
        AMPERSAND_TO_MINIMESSAGE.put('k', "<obfuscated>");
        AMPERSAND_TO_MINIMESSAGE.put('l', "<bold>");
        AMPERSAND_TO_MINIMESSAGE.put('m', "<strikethrough>");
        AMPERSAND_TO_MINIMESSAGE.put('o', "<italic>");
        AMPERSAND_TO_MINIMESSAGE.put('r', "<reset>");
    }

    /**
     * Efficiently transform a string with ampersand color codes to MiniMessage format
     *
     * @param s The string to transform
     * @return The transformed string
     */
    public static String transformToMiniMessage(char prefix, String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == prefix && i < s.length() - 1) {
                char code = s.charAt(i + 1);
                String replacement = AMPERSAND_TO_MINIMESSAGE.get(code);
                if (replacement != null) {
                    sb.append(replacement);
                    i++; // Skip the next character
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }


}

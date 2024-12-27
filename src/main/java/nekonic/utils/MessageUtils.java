package nekonic.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtils {
    private static ResourceBundle resourceBundle;

    public static void setLocale(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("lang/messages", locale);
    }

    public static String getMessage(String key) {
        return resourceBundle.getString(key);
    }
}

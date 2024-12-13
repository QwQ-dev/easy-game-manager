package me.qwqdev.egm.utils.text;

import lombok.experimental.UtilityClass;

/**
 * Utility class for manipulating and formatting strings.
 *
 * @author NaerQAQ
 * @version 1.0
 * @since 2024/1/7
 */
@UtilityClass
public class StringUtils {
    /**
     * Formatting string.
     *
     * @param string the string
     * @param params the params
     * @return the string
     */
    public static String formatting(String string, String... params) {
        for (int i = 0; i < params.length; i += 2) {
            string = string.replace(params[i], params[i + 1]);
        }

        return string;
    }
}

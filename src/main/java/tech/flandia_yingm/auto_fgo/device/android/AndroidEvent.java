package tech.flandia_yingm.auto_fgo.device.android;

import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Value
public class AndroidEvent {

    private static final Pattern EVENT_PATTERN =
            Pattern.compile("\\s*?(/dev/input/event\\d):\\s*?([0-9a-f]{4})\\s*?([0-9a-f]){4}\\s*?([0-9a-f]{8})\\s*?");


    @NonNull
    private final String deviceName;

    private final int eventType;

    private final int eventCode;

    private final long eventValue;


    public static AndroidEvent parse(String str) {
        val strMatcher = EVENT_PATTERN.matcher(str);
        if (strMatcher.matches()) {
            return new AndroidEvent(
                    strMatcher.group(1),
                    Integer.parseInt(strMatcher.group(2), 16),
                    Integer.parseInt(strMatcher.group(3), 16),
                    Long.parseLong(strMatcher.group(4), 16)
            );
        } else {
            throw new IllegalArgumentException(String.format("No matches found in %s", str));
        }
    }

    public static List<AndroidEvent> parseAll(String str) {
        val list = new ArrayList<AndroidEvent>();
        for (String line : str.split("\n")) {
            list.add(parse(line));
        }
        return list;
    }

}

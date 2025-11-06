package alertSystemSMS.notificationMicroservice.utils;

import java.util.Set;

public class CarrierUtils {

    // Prefixes for Smart, TNT, and Sun Cellular (which is now under Smart)
    // Prefixes for Smart, TNT, and Sun Cellular (which is now under Smart)
    private static final Set<String> SMART_PREFIXES = Set.of(
            // Existing Prefixes
            "0813", "0907", "0908", "0909", "0910", "0911", "0912", "0913", "0914",
            "0918", "0919", "0920", "0921", "0928", "0929", "0930", "0938", "0939",
            "0946", "0947", "0948", "0949", "0950", "0951", "0961", "0963", "0968",
            "0970", "0981", "0989", "0998", "0999",

            // Newly Added Prefixes
            "0960", // TNT
            "0969", // Smart/TNT
            "0922", // Sun
            "0923", // Sun
            "0924", // Sun
            "0925", // Sun
            "0931", // Sun
            "0932", // Sun
            "0933", // Sun
            "0934", // Sun
            "0940", // Sun
            "0941", // Sun
            "0942", // Sun
            "0943", // Sun
            "0944"  // Sun
    );

    /**
     * Checks if a given phone number belongs to the SMART network.
     * Assumes the number is in the format "+639xxxxxxxxx" or "09xxxxxxxxx".
     * @param phoneNumber The phone number to check.
     * @return true if it's a SMART number, false otherwise.
     */
    public static boolean isSmartNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return false;
        }

        String prefix;
        if (phoneNumber.startsWith("+63")) {
            prefix = "0" + phoneNumber.substring(3, 6);
        } else if (phoneNumber.startsWith("09")) {
            prefix = phoneNumber.substring(0, 4);
        } else {
            return false; // Not a valid PH mobile format
        }

        return SMART_PREFIXES.contains(prefix);
    }
}

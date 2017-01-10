package sk.r3n.util;

public class BirthId {

    public static boolean isValid(String birthId) {
        try {
            if (birthId.length() < 10 || birthId.length() > 11) {
                return false;
            }
            String date = birthId.substring(0, 6);
            String subcode = birthId.substring(7);
            long num = Long.parseLong(date + subcode);
            if (num % 11 != 0) {
                if (date.length() + subcode.length() != 9) {
                    return false;
                }
            }
            int month = Integer.parseInt(date.substring(2, 4));
            if (month > 70) {
                month -= 70;
            }
            if (month > 50) {
                month -= 50;
            }
            if (month > 20) {
                month -= 20;
            }
            if (month < 1 || month > 12) {
                return false;
            }
            int day = Integer.parseInt(date.substring(4));
            if (day < 1 || day > 31) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

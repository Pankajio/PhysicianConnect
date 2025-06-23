package physicianconnect.presentation.util;

import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

public class UserUtil {
    public static String getUserId(Object user) {
        if (user instanceof Physician p)
            return p.getId();
        if (user instanceof Receptionist r)
            return r.getId();
        return "";
    }

    public static String getUserName(Object user) {
        if (user instanceof Physician p)
            return p.getName();
        if (user instanceof Receptionist r)
            return r.getName();
        return "";
    }

    public static String getUserEmail(Object user) {
        if (user instanceof Physician p)
            return p.getEmail();
        if (user instanceof Receptionist r)
            return r.getEmail();
        return "";
    }

    public static String getUserType(Object user) {
        if (user instanceof Physician)
            return "physician";
        if (user instanceof Receptionist)
            return "receptionist";
        return "";
    }

    public static String getUserName(String id, String type, java.util.List<Object> allUsers) {
        for (Object user : allUsers) {
            if (getUserId(user).equals(id) && getUserType(user).equals(type)) {
                return getUserName(user);
            }
        }
        return id;
    }
}
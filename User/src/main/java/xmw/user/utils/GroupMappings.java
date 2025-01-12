package xmw.user.utils;

import java.util.HashMap;
import java.util.Map;

public class GroupMappings {
    public static final Map<String, String> GROUP_MAP = new HashMap<>();
    static {
        GROUP_MAP.put("g_lecturer", "Lecturer");
        GROUP_MAP.put("g_employee", "Employee");
        GROUP_MAP.put("g_professor", "Professor");
        GROUP_MAP.put("g_student", "Student");
    }
}

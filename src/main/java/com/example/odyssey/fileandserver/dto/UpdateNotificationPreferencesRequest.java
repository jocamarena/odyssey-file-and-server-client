package com.example.odyssey.fileandserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateNotificationPreferencesRequest {
    private String userID;
    private List<NotificationPreference> preferences;

    @Data
    public static class NotificationPreference {
        private String notificationType;
        private boolean enabled;
    }
}

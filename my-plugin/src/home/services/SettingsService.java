package home.services;

import com.intellij.openapi.components.ServiceManager;

public interface SettingsService {
    static SettingsService getInstance() {
        return ServiceManager.getService(SettingsService.class);
    }
}

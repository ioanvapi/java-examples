package home;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public interface PluginController {

    static PluginController getInstance() {
        return ServiceManager.getService(PluginController.class);
    }

    void setView(ConnectorViewFactory view);
    void setRegion(String regionName);
    void refreshRegionList();

    void refreshStatus();

    void refreshFunctionList();

    void refreshZipArtifactList();

    void refreshProfilesList();
}

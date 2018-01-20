package home;

import com.intellij.openapi.components.ServiceManager;
import home.model.ArtifactEntry;
import home.model.FunctionEntry;
import home.model.ProfileEntry;
import home.model.RegionEntry;

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

    void setLambdaFunction(FunctionEntry entry);

    void setRegion(RegionEntry entry);

    void setProfile(ProfileEntry entry);

    void setZipArtifact(ArtifactEntry entry);
}

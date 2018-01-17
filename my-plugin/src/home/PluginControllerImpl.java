package home;

import com.amazonaws.regions.Regions;
import com.intellij.openapi.project.Project;
import home.model.FunctionEntry;
import home.model.ProfileEntry;
import home.model.RegionEntry;
import home.services.SettingsService;
import org.apache.velocity.util.ArrayListWrapper;

import java.util.ArrayList;
import java.util.List;

public class PluginControllerImpl implements PluginController {

    private ConnectorViewFactory view;

    private SettingsService settingsService = SettingsService.getInstance();

    @Override
    public void setView(ConnectorViewFactory view) {
        this.view = view;
    }

    @Override
    public void setRegion(String regionName) {
        Regions region = getRegion(regionName);
        if(region == null) {
            return;
        }
        setRegionAndProfile(region);
    }

    @Override
    public void refreshRegionList() {
        view.logDebug("Refresh region list.");
        List<RegionEntry> regions = new ArrayList<>();
        Regions selectedRegion = Regions.EU_CENTRAL_1;//todo
        view.setRegionList(regions, selectedRegion);
        refreshStatus();
    }

    @Override
    public void refreshFunctionList() {
        view.logDebug("Refresh function list.");
        ArrayList<String> functionNames = new ArrayList<>();
        List<FunctionEntry> functions = new ArrayList<>(); //todo
        FunctionEntry selectedFunctionEntry = null;

        view.setFunctionList(functions, selectedFunctionEntry);

        refreshStatus();
    }

    @Override
    public void refreshZipArtifactList() {
        view.logDebug("Refresh Zip artifacts list.");
    }

    @Override
    public void refreshProfilesList() {
        view.logDebug("Refresh AWS profiles list.");
        List<ProfileEntry> profilesList = new ArrayList<>();
        String selectedProfile = null; //todo
        view.setProfilesList(profilesList, selectedProfile);
    }

    @Override
    public void refreshStatus() {
        //todo
        String function = "function";
        String artifact = "artifact";
        String region = "region";
        String regionDescription = "region desc";
        String credentialProfile = "profile";
        view.refreshStatus(function, artifact, region, regionDescription, credentialProfile);
    }


    private void setRegionAndProfile(Regions region) {
        view.logInfo("Region is set to: %s", region.toString());
        view.logInfo("ProfileEntry is set to:");
        updateModel(region);
    }

    private void updateModel(Regions region) {

    }


    private Regions getRegion(String regionName) {
        for (Regions region : Regions.values()){
            if(region.getName().equals(regionName)){
                return region;
            }
        }
        return null;
    }
}

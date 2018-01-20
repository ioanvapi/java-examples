package home;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.model.Runtime;
import home.model.ArtifactEntry;
import home.model.FunctionEntry;
import home.model.ProfileEntry;
import home.model.RegionEntry;
import home.services.AwsService;
import home.services.SettingsService;

import java.util.ArrayList;
import java.util.List;

public class PluginControllerImpl implements PluginController {

    private ConnectorViewFactory view;

    private SettingsService settingsService = SettingsService.getInstance();
    private AwsService awsService = AwsService.getInstance();

    @Override
    public void setView(ConnectorViewFactory view) {
        this.view = view;
    }

    @Override
    public void setRegion(String regionName) {
        Regions region = Regions.fromName(regionName);
        if(region == null) {
            return;
        }
        setRegionAndProfile(region);
    }

    @Override
    public void refreshRegionList() {
        view.logDebug("Refresh region list.");
        //todo: selected region from previous
        String selectedRegion = awsService.getDefaultRegion().getName();
        view.setRegionList(awsService.getRegions(), selectedRegion);
        refreshStatus();
    }

    @Override
    public void refreshFunctionList() {
        view.logDebug("Refresh function list.");
        List<FunctionEntry> goFunctions = awsService.getLambdaFunctionsForGo();
        FunctionEntry selectedFunctionEntry = null;
        String lastSelectedFunctionName = settingsService.getSelectedFunctionName();
        for (FunctionEntry function : goFunctions) {
            if (function.getFunctionName().equals(lastSelectedFunctionName)) {
                selectedFunctionEntry = function;
            }
        }

        view.logDebug("Found %d Go1x functions.", goFunctions.size());
        view.setFunctionList(goFunctions, selectedFunctionEntry);
        refreshStatus();
    }

    @Override
    public void refreshZipArtifactList() {
        view.logDebug("Refresh Zip artifacts list.");
    }

    @Override
    public void refreshProfilesList() {
        view.logDebug("Refresh AWS profiles list.");
        List<ProfileEntry> profiles = awsService.getProfiles();
        String selectedProfileName = determineSelectedProfile();
        view.setProfilesList(profiles, selectedProfileName);
        view.logInfo("Found %d credential profiles.", profiles.size());
    }

    @Override
    public void setLambdaFunction(FunctionEntry entry) {
        view.logDebug("Set lambda function.");
        settingsService.setSelectedFunctionName(entry.getFunctionName());
        refreshStatus();
    }

    @Override
    public void setRegion(RegionEntry entry) {
        view.logDebug("Set region.");

        refreshStatus();
    }

    @Override
    public void setProfile(ProfileEntry entry) {
        view.logDebug("Set profile.");

        refreshStatus();
    }

    @Override
    public void setZipArtifact(ArtifactEntry entry) {
        view.logDebug("Set artifact.");

        refreshStatus();
    }

    /*
    * Determine selected profile finding the last used profile in settings
    * or use the default profile.
     */
    private String determineSelectedProfile() {
        //todo: use selected from previous (settings service) too

        return awsService.getDefaultProfileName();
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

    //todo
    private void updateModel(Regions region) {
        //awsService.changeClient(region.getName(), );
    }

}

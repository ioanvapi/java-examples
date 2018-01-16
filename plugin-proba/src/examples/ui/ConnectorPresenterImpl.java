package examples.ui;

import com.intellij.openapi.project.Project;
import examples.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConnectorPresenterImpl implements ConnectorPresenter {
    private final Regions DEFAULT_REGION = Regions.US_EAST_1;
    private ConnectorSettings connectorSettings = io.github.satr.idea.plugin.connector.la.models.ConnectorSettings.getInstance();
    private ConnectorView view;
    private List<TestFunctionInputEntry> testFunctionInputRecentEntryList = new ArrayList<>();

    @Override
    public void setView(ConnectorView view) {
        this.view = view;
    }

    @Override
    public void refreshFunctionList() {
        view.logDebug("Refresh function list.");
        ArrayList<String> functionNames = new ArrayList<>();
        List<FunctionEntry> functions = getConnectorModel().getFunctions();
        String lastSelectedFunctionName = connectorSettings.getLastSelectedFunctionName();
        FunctionEntry selectedFunctionEntry = null;
        int functionCount = 0;
        for (FunctionEntry entry : functions) {
            if (!entry.getRuntime().equals(Runtime.Java8)) {
                continue;
            }
            functionNames.add(entry.getFunctionName());
            if(entry.getFunctionName().equals(lastSelectedFunctionName)){
                selectedFunctionEntry = entry;
            }
            functionCount++;
        }
        view.logDebug("Found %d Java-8 functions.", functionCount);
        connectorSettings.setFunctionNames(functionNames);
        view.setFunctionList(functions, selectedFunctionEntry);
        FunctionEntry functionEntry = view.getSelectedFunctionEntry();
        if(functionEntry == null) {
            view.logInfo("Function is not selected.");
        } else {
            view.logInfo("Selected function: \"%s\"", functionEntry.getFunctionName());
        }
        refreshStatus();
    }

    @Override
    public void updateFunction(Project project) {
        view.logDebug("Update function.");
        FunctionEntry functionEntry = view.getSelectedFunctionEntry();
        ArtifactEntry artifactEntry = view.getSelectedArtifactEntry();
        OperationResult validationResult = validateToUpdate(functionEntry, artifactEntry);
        if(validationResult.failed()){
            showError(project, validationResult.getErrorAsString());
            return;
        }
        String functionName = functionEntry.getFunctionName();
        String artifactFilePath = artifactEntry.getOutputFilePath();
        final OperationValueResult<FunctionEntry> result = getConnectorModel().updateWithJar(functionName, artifactFilePath);
        if (!result.success()) {
            showError(project, result.getErrorAsString());
            return;
        }
        connectorSettings.setLastSelectedFunctionName(functionName);
        connectorSettings.setLastSelectedJarArtifactName(artifactEntry.getName());
        showInfo(project, "Lambda function \"%s\" has been updated with the JAR-artifact \"%s\".",
                result.getValue().getFunctionName(), artifactEntry.getName());
    }

    private void showError(Project project, String format, Object... args) {
        MessageHelper.showError(project, format, args);
        view.logError(format, args);
    }

    private void showInfo(Project project, String format, Object... args) {
        MessageHelper.showInfo(project, format, args);
        view.logInfo(format, args);
    }

    private OperationResult validateToUpdate(FunctionEntry functionEntry, ArtifactEntry artifactEntry) {
        OperationResultImpl result = new OperationResultImpl();
        if(functionEntry == null){
            result.addError("Function is not selected.");
        }
        if(artifactEntry == null){
            result.addError("JAR-artifact is not selected.");
        }
        if(result.failed()) {
            return result;
        }
        if(!new File(artifactEntry.getOutputFilePath()).exists()){
            result.addError("JAR-artifact file does not exist.");
        }
        return result;
    }

    @Override
    public void shutdown() {
        view.logDebug("Shutdown.");
        shutdownConnectorModel();
    }

    @Override
    public void refreshRegionList(Project project) {
        view.logDebug("Refresh region list.");
        view.setRegionList(getConnectorModel().getRegions(), getLastSelectedRegion());
        refreshStatus();
    }

    @Override
    public void refreshCredentialProfilesList(Project project) {
        view.logDebug("Refresh credential profile list.");
        List<CredentialProfileEntry> credentialProfiles = getConnectorModel().getCredentialProfiles();
        view.setCredentialProfilesList(credentialProfiles, getLastSelectedCredentialProfile());
        view.logInfo("Found %d credential profiles.", credentialProfiles.size());
        refreshStatus();
    }

    @Override
    public void refreshStatus() {
        CredentialProfileEntry credentialProfileEntry = view.getSelectedCredentialProfileEntry();
        String credentialProfile = credentialProfileEntry != null ? credentialProfileEntry.toString() : null;
        RegionEntry regionEntry = view.getSelectedRegionEntry();
        String region = regionEntry != null ? regionEntry.getName() : null;
        String regionDescription = regionEntry != null ? regionEntry.toString() : null;
        FunctionEntry functionEntry = view.getSelectedFunctionEntry();
        String function = functionEntry != null ? functionEntry.toString() : null;
        ArtifactEntry artifactEntry = view.getSelectedArtifactEntry();
        String artifact = artifactEntry != null ? artifactEntry.toString() : null;
        view.refreshStatus(function, artifact, region, regionDescription, credentialProfile);
    }

    @Override
    public void refreshAllLists(Project project) {
        view.logDebug("Refresh all.");
        refreshJarArtifactList(project);
        refreshRegionList(project);
        refreshCredentialProfilesList(project);
        refreshFunctionList();
    }

    @Override
    public void refreshJarArtifactList(Project project) {
        view.logDebug("Refresh JAR-artifact list.");
        ProjectModel projectModel = ServiceManager.getService(ProjectModel.class);
        String lastSelectedJarArtifactName = connectorSettings.getLastSelectedJarArtifactName();
        ArtifactEntry selectedArtifactEntry = null;
        Collection<? extends ArtifactEntry> jarArtifacts = projectModel.getJarArtifacts(project);
        for(ArtifactEntry entry : jarArtifacts){
            if(entry.getName().equals(lastSelectedJarArtifactName)){
                selectedArtifactEntry = entry;
                break;
            }
        }
        view.setArtifactList(jarArtifacts, selectedArtifactEntry);
        ArtifactEntry artifactEntry = view.getSelectedArtifactEntry();
        if(artifactEntry == null) {
            view.logInfo("JAR-artifact is not selected.");
        } else {
            view.logInfo("Selected JAR-artifact: \"%s\"", artifactEntry.getName());
        }
        refreshStatus();
    }

    @Override
    public void setRegion(RegionEntry regionEntry) {
        Regions region = tryGetRegionBy(regionEntry.getName());
        if(region == null) {
            return;
        }
        setRegionAndProfile(region, connectorSettings.getLastSelectedCredentialProfile());
    }

    private void setRegionAndProfile(Regions region, String credentialProfile) {
        view.logInfo("Region is set to: %s", region.toString());
        view.logInfo("Profile is set to: %s", credentialProfile);
        reCreateConnectorModel(region, credentialProfile);
        connectorSettings.setLastSelectedRegionName(region.getName());
        refreshFunctionList();
    }

    @Override
    public void setCredentialProfile(CredentialProfileEntry credentialProfileEntry) {
        BasicProfile basicProfile = credentialProfileEntry.getBasicProfile();
        Regions lastSelectedRegion = getLastSelectedRegion();
        Regions region = lastSelectedRegion;
        if(!isEmpty(basicProfile.getRegion())) {
            region = tryGetRegionBy(basicProfile.getRegion());
        }
        if(region == null){
            region = lastSelectedRegion;
        }
        String credentialProfile = credentialProfileEntry.getName();
        connectorSettings.setLastSelectedCredentialProfile(credentialProfile);
        setRegionAndProfile(region, credentialProfile);
        if(!lastSelectedRegion.getName().equals(region.getName())){
            view.setRegion(region);
        }
        CredentialProfileEntry profileEntry = view.getSelectedCredentialProfileEntry();
        if(profileEntry == null) {
            view.logInfo("Credential profile is not selected.");
        } else {
            view.logInfo("Selected Credential profile: \"%s\"", profileEntry.getName());
        }
        refreshStatus();
    }

    @Override
    public void setFunction(FunctionEntry functionEntry) {
        view.logDebug("Set function.");
        connectorSettings.setLastSelectedFunctionName(functionEntry.getFunctionName());
        refreshStatus();
    }

    @Override
    public void setJarArtifact(ArtifactEntry artifactEntry) {
        view.logDebug("Set JAR-artifact.");
        connectorSettings.setLastSelectedFunctionName(artifactEntry.getName());
        refreshStatus();
    }

    @Override
    public void runFunctionTest(Project project, String inputText) {
        FunctionEntry functionEntry = view.getSelectedFunctionEntry();
        if(functionEntry == null) {
            view.logError("Cannot run function - function is not selected.");
            return;
        }
        if(isEmpty(inputText.trim())){
            view.logError("Cannot run function \"%s\" - input is empty.", functionEntry.getFunctionName());
            return;
        }
        view.logDebug("Run function \"%s\".", functionEntry.getFunctionName());
        OperationValueResult<String> result = getConnectorModel().invokeFunction(functionEntry.getFunctionName(), inputText);
        if(result.hasInfo()){
            view.logInfo(result.getInfoAsString());
        }
        if(result.failed()) {
            view.logError("Run function test failed:\n%s", result.getErrorAsString());
        }
        view.setFunctionTestOutput(result.getValue());
    }

    @Override
    public void openTestFunctionInputFile(File file) {
        try {
            String filePath = file.getCanonicalPath();
            view.logDebug("Read function test input from file: %s", filePath);
            byte[] buffer = Files.readAllBytes(file.toPath());
            String inputText = new String(buffer);
            view.setTestFunctionInput(inputText);
            for(TestFunctionInputEntry entry : testFunctionInputRecentEntryList){
                if(entry.getFilePath().equals(filePath)){
                    testFunctionInputRecentEntryList.remove(entry);
                    break;
                }
            }
            testFunctionInputRecentEntryList.add(new TestFunctionInputEntry(filePath, file.getName(), inputText));
            view.setTestFunctionInputRecentEntryList(testFunctionInputRecentEntryList);
        } catch (Exception e) {
            e.printStackTrace();
            view.logError(e);
        }
    }

    @Override
    public String getLastSelectedTestFunctionInputFilePath() {
        String filePath = connectorSettings.getLastSelectedTestFunctionInputFilePath();
        return isEmpty(filePath) ? "" : filePath;
    }

    @Override
    public void setLastSelectedTestFunctionInputFilePath(String path) {
        connectorSettings.setLastSelectedTestFunctionInputFilePath(path);
    }

    @Override
    public void setSetTestFunctionInputFromRecent(TestFunctionInputEntry entry) {
        view.setTestFunctionInput(entry.getInputText());
    }

    private Regions tryGetRegionBy(String regionName) {
        for (Regions region : Regions.values()){
            if(region.getName().equals(regionName)){
                return region;
            }
        }
        return null;
    }

    @Override
    @NotNull
    protected Regions getLastSelectedRegion() {
        String lastSelectedRegionName = connectorSettings.getLastSelectedRegionName();
        Regions region = lastSelectedRegionName == null
                ? DEFAULT_REGION
                : tryGetRegionBy(lastSelectedRegionName);
        return region != null ? region : DEFAULT_REGION;
    }

    @Override
    @NotNull
    protected String getLastSelectedCredentialProfile() {
        String lastSelectedCredentialProfile = connectorSettings.getLastSelectedCredentialProfile();
        String credentialProfile = lastSelectedCredentialProfile == null
                ? Constant.CredentialProfile.DEFAULT
                : lastSelectedCredentialProfile;
        return credentialProfile != null ? credentialProfile : Constant.CredentialProfile.DEFAULT;
    }
}

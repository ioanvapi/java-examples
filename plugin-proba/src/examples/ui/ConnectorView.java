package examples.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.jetbrains.annotations.NotNull;

public class ConnectorView {
    static final Logger logger = LogManager.getLogger(ConnectorView.class);
    private final ConnectorPresenter presenter;
    private final ProgressManager progressManager = ProgressManager.getInstance();
    private final ProjectModel projectModel = ServiceManager.getService(ProjectModel.class);

    private JPanel toolPanel;
    private JTextPane txtStatus;
    private JPanel pnlTabs;
    private JPanel pnlToolBar;
    private JButton updateFunctionButton;
    private JButton refreshAllButton;
    private JButton clearLogButton;
    private JButton openTestFunctionInputFileButton;
    private JButton runTestFunctionButton;
    private JTabbedPane tabContent;
    private JPanel logPan;
    private JPanel tabPanSettings;
    private JPanel pnlSettings;
    private JPanel runFunctionTestTab;
    private JPanel logSettingsPan;
    private JScrollPane logScrollPan;
    private JTextArea logTextBox;

    public ConnectorView() {
        prepareUiLogger();
        this.presenter = presenter;
        this.presenter.setView(this);
        presenter.refreshJarArtifactList(project);

        refreshAllButton.addActionListener(e -> {
            runRefreshAllList(presenter);
        });
        refreshFuncListButton.addActionListener(e -> {
            runRefreshFunctionList(presenter);
        });
        refreshJarArtifactsButton.addActionListener(e -> {
            runRefreshJarArtifactList(presenter);
        });
        refreshRegionsButton.addActionListener(e -> {
            runRefreshRegionList(presenter);
        });
        refreshCredentialProfiles.addActionListener(e -> {
            runRefreshCredentialProfilesList(presenter);
        });
        updateFunctionButton.addActionListener(e -> {
            runUpdateFunction(presenter);
        });
        clearLogButton.addActionListener(e -> {
            clearLog();
        });
        runTestFunctionButton.addActionListener(e -> {
            runFunctionTest(presenter);
        });
        openTestFunctionInputFileButton.addActionListener(e -> {
            openFunctionTestInputFile(presenter);
        });
        testFunctionInputRecentFileList.addItemListener(e -> {
            runSetTestFunctionInputFromRecent(presenter, e);
        });
        functionList.addItemListener(e -> {
            runSetFunction(presenter, e);
        });
        regionList.addItemListener(e -> {
            runSetRegion(presenter, e);
        });
        credentialProfileList.addItemListener(e -> {
            runSetCredentialProfile(presenter, e);
        });
        jarArtifactList.addItemListener(e -> {
            runSetJarArtifact(presenter, e);
        });
        runRefreshAllList(presenter);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void openFunctionTestInputFile(ConnectorPresenter presenter) {
        if(runFunctionTestOperationInProgress){
            return;
        }

        String path = presenter.getLastSelectedTestFunctionInputFilePath();
        File file = new File(path);
        if(!file.isDirectory() || !file.exists()){
            file = new File("");
        }
        JFileChooser fileChooser = new JFileChooser(file);
        fileChooser.setDialogTitle("Test Function Input");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(false);
        if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try {
            File selectedFile = fileChooser.getSelectedFile();
            presenter.setLastSelectedTestFunctionInputFilePath(fileChooser.getCurrentDirectory().getCanonicalPath());
            runOperation(() -> presenter.openTestFunctionInputFile(selectedFile), "Read test function input file \"%s\".", selectedFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            logError(e.getMessage());
        }
    }

    private void prepareUiLogger() {
        logger.addAppender(new AsyncAppender(){
            @Override
            public void append(LoggingEvent event) {
                super.append(event);
                logTextBox.append(String.format("\n%s: %s", event.getLevel(), event.getMessage()));
            }
        });
        logLevelList.addItem(Level.DEBUG);
        logLevelList.addItem(Level.INFO);
        logLevelList.addItem(Level.ERROR);
        logger.setLevel(Level.INFO);
        logLevelList.setSelectedItem(Level.INFO);
        logLevelList.addItemListener(e -> {
            logger.setLevel((Level) e.getItem());
        });
    }

    private void runSetRegion(ConnectorPresenter presenter, ItemEvent e) {
        if(operationInProgress || setRegionOperationInProgress
                || e.getStateChange() != ItemEvent.SELECTED)
            return;
        RegionEntry entry = (RegionEntry)e.getItem();
        if(entry == null)
            return;
        runOperation(() -> presenter.setRegion(entry), "Select region: " + entry.toString());
    }

    private void runSetFunction(ConnectorPresenter presenter, ItemEvent e) {
        if(operationInProgress || setRegionOperationInProgress
                || e.getStateChange() != ItemEvent.SELECTED)
            return;
        FunctionEntry entry = (FunctionEntry)e.getItem();
        if(entry == null)
            return;
        runOperation(() -> presenter.setFunction(entry), "Select function: " + entry.toString());
    }

    private void runSetTestFunctionInputFromRecent(ConnectorPresenter presenter, ItemEvent e) {
        if(operationInProgress
                || e.getStateChange() != ItemEvent.SELECTED)
            return;
        TestFunctionInputEntry entry = (TestFunctionInputEntry)e.getItem();
        if(entry == null)
            return;
        runOperation(() -> presenter.setSetTestFunctionInputFromRecent(entry), "Select test function input from file: ", entry.getFileName());
    }

    private void runFunctionTest(ConnectorPresenter presenter) {
        if(operationInProgress || runFunctionTestOperationInProgress)
            return;
        runOperation(() -> {
            runFunctionTestOperationInProgress = true;
            try {
                presenter.runFunctionTest(project, functionTestInputText.getText());
            } finally {
                runFunctionTestOperationInProgress = false;
            }
        }, "Run test of the function");
    }

    private void runSetCredentialProfile(ConnectorPresenter presenter, ItemEvent e) {
        if(e.getStateChange() != ItemEvent.SELECTED)
            return;
        CredentialProfileEntry entry = (CredentialProfileEntry)e.getItem();
        if(entry == null)
            return;
        runOperation(() -> presenter.setCredentialProfile(entry), "Select credential profile: " + entry.toString());
    }

    private void runSetJarArtifact(ConnectorPresenter presenter, ItemEvent e) {
        if(e.getStateChange() != ItemEvent.SELECTED)
            return;
        ArtifactEntry entry = (ArtifactEntry) e.getItem();
        if(entry == null)
            return;
        runOperation(() -> presenter.setJarArtifact(entry), "Select JAR-artifact: " + entry.toString());
    }

    private void runUpdateFunction(ConnectorPresenter presenter) {
        runOperation(() -> presenter.updateFunction(getProject()),
                "Update selected AWS Lambda function with JAR-artifact");
    }

    private void clearLog() {
        logTextBox.setText("");
    }

    private void runRefreshAllList(ConnectorPresenter presenter) {
        runOperation(() -> {
            presenter.refreshAllLists(project);
            presenter.refreshStatus();
        }, "Refresh all lists");
    }
    private void runRefreshFunctionList(ConnectorPresenter presenter) {
        runOperation(() -> {
            presenter.refreshFunctionList();
            presenter.refreshStatus();
        }, "Refresh list of AWS Lambda functions");
    }

    private void runRefreshJarArtifactList(ConnectorPresenter presenter) {
        runOperation(() -> {
            presenter.refreshJarArtifactList(project);
            presenter.refreshStatus();
        }, "Refresh list of JAR-artifacts in the project");
    }

    private void runRefreshRegionList(ConnectorPresenter presenter) {
        runOperation(() -> {
            presenter.refreshRegionList(project);
            presenter.refreshStatus();
        }, "Refresh list of AWS regions");
    }

    private void runRefreshCredentialProfilesList(ConnectorPresenter presenter) {
        runOperation(() -> {
            presenter.refreshCredentialProfilesList(project);
            presenter.refreshStatus();
        }, "Refresh list of credential profiles");
    }

    private void runOperation(Runnable runnable, final String format, Object... args) {
        String title = String.format(format, args);
        if(operationInProgress)
            return;
        try {
            operationInProgress = true;
            setControlsEnabled(false);
            progressManager.run(new Task.Backgroundable(project, title, true) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    try {
                        logDebug(title);
                        runnable.run();
                    } catch (Throwable t) {
                        Class<? extends Throwable> exceptionClass = t.getClass();
                        if(exceptionClass.equals(AWSLambdaException.class)){
                            MessageHelper.showError(project, t.getMessage());
                            logError(t.getMessage());
                        } else if(exceptionClass.equals(SdkClientException.class)){
                            MessageHelper.showError(project, t.getMessage());
                            logError(t.getMessage());
                        } else {
                            MessageHelper.showCriticalError(project, t);
                            logError(t.getMessage());
                        }
                    }
                    finally {
                        setControlsEnabled(true);
                        operationInProgress = false;
                    }
                }
            });
        }
        catch(Throwable t){
            MessageHelper.showError(project, t);
        }
    }

    private void setControlsEnabled(boolean enabled) {
        refreshAllButton.setEnabled(enabled);
        refreshFuncListButton.setEnabled(enabled);
        refreshJarArtifactsButton.setEnabled(enabled);
        refreshRegionsButton.setEnabled(enabled);
        refreshCredentialProfiles.setEnabled(enabled);
        updateFunctionButton.setEnabled(enabled);
        runTestFunctionButton.setEnabled(enabled);
        openTestFunctionInputFileButton.setEnabled(enabled);
        functionList.setEnabled(enabled);
        jarArtifactList.setEnabled(enabled);
        regionList.setEnabled(enabled);
        credentialProfileList.setEnabled(enabled);
        testFunctionInputRecentFileList.setEnabled(enabled);
    }

    public Project getProject() {
        return project;
    }

    
    protected void finalize() throws Throwable {
        if (presenter != null)
            presenter.shutdown();

        super.finalize();
    }

    
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolPanel, "", false);
        toolWindow.getContentManager().addContent(content);
        this.project = project;
        this.presenter.refreshJarArtifactList(this.project);
    }

    
    public void setFunctionList(List<FunctionEntry> functions, FunctionEntry selectedFunctionEntry) {
        functionList.removeAllItems();
        for (FunctionEntry entry : functions) {
            if (entry.getRuntime().equals(Runtime.Java8)) {
                functionList.addItem(entry);
            }
        }
        if(selectedFunctionEntry != null){
            functionList.setSelectedItem(selectedFunctionEntry);
        }
    }

    
    public void setArtifactList(Collection<? extends ArtifactEntry> artifacts, ArtifactEntry selectedArtifactEntry) {
        jarArtifactList.removeAllItems();
        for(ArtifactEntry artifactEntry : artifacts) {
            jarArtifactList.addItem(artifactEntry);
        }
        if(selectedArtifactEntry != null){
            jarArtifactList.setSelectedItem(selectedArtifactEntry);
        }
    }

    
    public void setCredentialProfilesList(List<CredentialProfileEntry> credentialProfiles, String selectedCredentialsProfile) {
        credentialProfileList.removeAllItems();
        CredentialProfileEntry selectedCredentialsProfileEntry = null;
        for(CredentialProfileEntry entry : credentialProfiles){
            credentialProfileList.addItem(entry);
            if(!isEmpty(selectedCredentialsProfile) && entry.getName().equals(selectedCredentialsProfile))
                selectedCredentialsProfileEntry = entry;
        }
        if(selectedCredentialsProfileEntry != null) {
            credentialProfileList.setSelectedItem(selectedCredentialsProfileEntry);
        }
    }

    
    public void setRegion(Regions region) {
        if(setRegionOperationInProgress){
            return;
        }
        try {
            setRegionOperationInProgress = true;
            for (int i = 0; i < regionList.getItemCount(); i++) {
                if(((RegionEntry) regionList.getItemAt(i)).getName().equals(region.getName())){
                    regionList.setSelectedIndex(i);
                    return;
                }
            }
        } finally {
            setRegionOperationInProgress = false;
        }
    }

    
    public void refreshStatus(String function, String artifact, String region, String regionDescription, String credentialProfile) {
        txtStatus.setText(String.format("Func: \"%s\"; Jar: \"%s\"; Region: \"%s\"; Profile:\"%s\"",
                getNotEmptyString(function, "?"),
                getNotEmptyString(artifact, "?"),
                getNotEmptyString(region, "?"),
                getNotEmptyString(credentialProfile, "?")
        ));
        txtStatus.setToolTipText(String.format("Func: %s\nJar: %s\nRegion: %s\nProfile: %s",
                getNotEmptyString(function, "?"),
                getNotEmptyString(artifact, "?"),
                getNotEmptyString(regionDescription, "?"),
                getNotEmptyString(credentialProfile, "?")
        ));
    }

    
    public FunctionEntry getSelectedFunctionEntry() {
        return (FunctionEntry) functionList.getSelectedItem();
    }

    
    public ArtifactEntry getSelectedArtifactEntry() {
        return (ArtifactEntry) jarArtifactList.getSelectedItem();
    }

    
    public RegionEntry getSelectedRegionEntry() {
        return (RegionEntry) regionList.getSelectedItem();
    }

    
    public CredentialProfileEntry getSelectedCredentialProfileEntry() {
        return (CredentialProfileEntry) credentialProfileList.getSelectedItem();
    }

    
    public void logDebug(String format, Object... args) {
        logger.debug(String.format(format, args));
    }

    
    public void logInfo(String format, Object... args) {
        logger.info(String.format(format, args));
    }

    
    public void logError(String format, Object... args) {
        logger.error(String.format(format, args));
    }

    
    public void logError(Throwable throwable) {
        logError(throwable.getMessage());
    }

    
    public void setFunctionTestOutput(String outputText) {
        functionTestOutputText.setText(outputText);
    }

    
    public void setTestFunctionInput(String inputText) {
        functionTestInputText.setText(inputText);
    }

    
    public void setTestFunctionInputRecentEntryList(List<TestFunctionInputEntry> entries) {
        testFunctionInputRecentFileList.removeAllItems();
        for(TestFunctionInputEntry entry : entries){
            testFunctionInputRecentFileList.addItem(entry);
        }
        if(entries.size() > 0) {
            testFunctionInputRecentFileList.setSelectedIndex(entries.size() - 1);//select last added entry
        }
    }

    
    public void setRegionList(List<RegionEntry> regions, Regions selectedRegion) {
        regionList.removeAllItems();
        RegionEntry selectedRegionEntry = null;
        for(RegionEntry entry : regions) {
            regionList.addItem(entry);
            if(selectedRegion != null && entry.getRegion().getName().equals(selectedRegion.getName())) {
                selectedRegionEntry = entry;
            }
        }
        if(selectedRegionEntry != null) {
            regionList.setSelectedItem(selectedRegionEntry);
        }
    }
}

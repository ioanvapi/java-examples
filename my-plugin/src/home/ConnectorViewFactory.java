package home;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.model.AWSLambdaException;
import com.amazonaws.services.lambda.model.Runtime;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import home.model.FunctionEntry;
import home.model.ProfileEntry;
import home.model.RegionEntry;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static home.StringUtil.getNotEmptyString;
import static org.apache.http.util.TextUtils.isEmpty;

public class ConnectorViewFactory implements ToolWindowFactory {

    private static final Logger logger = LogManager.getLogger(ConnectorViewFactory.class);

    private boolean operationInProgress = false;
    private PluginController ctrl;
    private Project project;
    private ToolWindow toolWindow;

    private JPanel mainContent;
    private JPanel leftToolbar;
    private JPanel settingsTab;
    private JPanel runTestTab;
    private JPanel panelSettings;
    private JPanel logPanel;
    private JPanel logSettingsPanel;
    private JTabbedPane tabbedPane;

    private JTextPane txtStatus;
    private JTextArea logTextBox;

    private JButton refreshFuncListButton;
    private JButton updateFunctionButton;
    private JButton refreshAllButton;
    private JButton refreshZipListButton;
    private JButton refreshRegionListButton;
    private JButton refreshProfilesListButton;

    private JComboBox<FunctionEntry> functionList;
    private JComboBox<RegionEntry> regionsList;
    private JComboBox<ProfileEntry> profilesList;
    private JComboBox<Level> logLevelsList;
    private JComboBox zipArchivesList;


    public ConnectorViewFactory() {
        this.ctrl = PluginController.getInstance();

        refreshFuncListButton.addActionListener(e -> runRefreshLambdaFunctionsList());
        refreshZipListButton.addActionListener(e -> runRefreshZipList());
        refreshRegionListButton.addActionListener(e -> runRefreshRegionList());
        refreshProfilesListButton.addActionListener(e -> runRefreshProfilesList());
    }


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        prepareUiLogger();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainContent, "", false);
        toolWindow.getContentManager().addContent(content);
        logger.debug("createToolWindowContent() executed ...");
    }

    private void runRefreshLambdaFunctionsList() {
        runOperation(() -> {
            ctrl.refreshFunctionList();
            ctrl.refreshStatus();
        }, "Refresh list of AWS Lambda functions");
    }

    private void runRefreshZipList() {
        runOperation(() -> {
            ctrl.refreshZipArtifactList();
            ctrl.refreshStatus();
        }, "Refresh list of JAR-artifacts in the project");
    }

    private void runRefreshRegionList() {
        runOperation(() -> {
            ctrl.refreshRegionList();
            ctrl.refreshStatus();
        }, "Refresh list of AWS regions");
    }

    private void runRefreshProfilesList() {
        runOperation(() -> {
            ctrl.refreshProfilesList();
            ctrl.refreshStatus();
        }, "Refresh list of credential profiles");
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
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

    public void setRegionList(List<RegionEntry> regions, Regions selectedRegion) {
        regionsList.removeAllItems();
        RegionEntry selectedRegionEntry = null;
        for (RegionEntry entry : regions) {
            regionsList.addItem(entry);
            if (selectedRegion != null && entry.getName().equals(selectedRegion.getName())) {
                selectedRegionEntry = entry;
            }
        }

        if (selectedRegionEntry != null) {
            regionsList.setSelectedItem(selectedRegionEntry);
        }
    }

    public void setProfilesList(List<ProfileEntry> profiles, String selectedProfile) {
        profilesList.removeAllItems();
        ProfileEntry selectedProfileEntry = null;
        for(ProfileEntry entry : profiles) {
            profilesList.addItem(entry);
            if (!isEmpty(selectedProfile) && entry.getName().equals(selectedProfile)) {
                selectedProfileEntry = entry;
            }
        }

        if (selectedProfileEntry != null) {
            profilesList.setSelectedItem(selectedProfileEntry);
        }
    }

    public void logInfo(String format, Object... args) {
        logger.info(String.format(format, args));
    }

    public void logDebug(String format, Object... args) {
        logger.debug(String.format(format, args));
    }

    public void logError(String format, Object... args) {
        logger.error(String.format(format, args));
    }

    public void refreshStatus(String function, String artifact, String region, String regionDescription, String credentialProfile) {
        txtStatus.setText(String.format("Func: \"%s\"; Jar: \"%s\"; Region: \"%s\"; ProfileEntry:\"%s\"",
                getNotEmptyString(function, "?"),
                getNotEmptyString(artifact, "?"),
                getNotEmptyString(region, "?"),
                getNotEmptyString(credentialProfile, "?")
        ));
        txtStatus.setToolTipText(String.format("Func: %s\nJar: %s\nRegion: %s\nProfileEntry: %s",
                getNotEmptyString(function, "?"),
                getNotEmptyString(artifact, "?"),
                getNotEmptyString(regionDescription, "?"),
                getNotEmptyString(credentialProfile, "?")
        ));
    }

    private void prepareUiLogger() {
        logger.addAppender(new AsyncAppender() {
            @Override
            public void append(LoggingEvent event) {
                super.append(event);
                logTextBox.append(String.format("\n%s: %s", event.getLevel(), event.getMessage()));
            }
        });
        logLevelsList.addItem(Level.DEBUG);
        logLevelsList.addItem(Level.INFO);
        logLevelsList.addItem(Level.ERROR);
        logger.setLevel(Level.INFO);
        logLevelsList.setSelectedItem(Level.INFO);
        logLevelsList.addItemListener(e -> {
            logger.setLevel((Level) e.getItem());
        });
    }

    private void setControlsEnabled(boolean enabled) {
        updateFunctionButton.setEnabled(enabled);

        refreshAllButton.setEnabled(enabled);
        refreshFuncListButton.setEnabled(enabled);
        refreshZipListButton.setEnabled(enabled);
        refreshRegionListButton.setEnabled(enabled);
        refreshProfilesListButton.setEnabled(enabled);

        functionList.setEnabled(enabled);
        zipArchivesList.setEnabled(enabled);
        regionsList.setEnabled(enabled);
        profilesList.setEnabled(enabled);
        logTextBox.setEnabled(enabled);
        logLevelsList.setEnabled(enabled);
    }


    private void runOperation(Runnable runnable, final String format, Object... args) {
        String title = String.format(format, args);
        if (operationInProgress)
            return;
        try {
            operationInProgress = true;
            setControlsEnabled(false);

            ProgressManager progressManager = ProgressManager.getInstance();
            progressManager.run(new Task.Backgroundable(project, title, true) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    try {
                        logDebug(title);
                        runnable.run();
                    } catch (Throwable t) {
                        Class<? extends Throwable> exceptionClass = t.getClass();
                        if (exceptionClass.equals(AWSLambdaException.class)) {
                            MessageHelper.showError(project, t.getMessage());
                            logError(t.getMessage());
                        } else if (exceptionClass.equals(SdkClientException.class)) {
                            MessageHelper.showError(project, t.getMessage());
                            logError(t.getMessage());
                        } else {
                            MessageHelper.showCriticalError(project, t);
                            logError(t.getMessage());
                        }
                    } finally {
                        setControlsEnabled(true);
                        operationInProgress = false;
                    }
                }
            });
        } catch (Throwable t) {
            MessageHelper.showError(project, t);
        }
    }



}

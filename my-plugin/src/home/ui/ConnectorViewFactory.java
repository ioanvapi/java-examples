package home.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ConnectorViewFactory implements ToolWindowFactory {

    private Project project;

    private JPanel mainContent;
    private JTextPane txtStatus;
    private JPanel leftToolbar;
    private JPanel settingsTab;
    private JPanel runTestTab;
    private JTabbedPane tabbedPane;
    private JButton updateFunctionButton;
    private JButton refreshAllButton;
    private JPanel panelSettings;
    private JComboBox functionList;
    private JButton refreshFuncListButton;
    private JComboBox zipArchivesList;
    private JButton button1;
    private JButton button2;
    private JComboBox regionsList;
    private JButton button3;
    private JComboBox profilesList;
    private JPanel logPanel;
    private JPanel logSettingsPanel;
    private JTextArea logTextBox;
    private JComboBox logLevelsList;
    private ToolWindow toolWindow;


    public ConnectorViewFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

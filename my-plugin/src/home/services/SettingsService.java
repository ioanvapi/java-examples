package home.services;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import static org.apache.http.util.TextUtils.isEmpty;

@State(name = "SettingsService", storages = @Storage("myplugin-settings.xml"))
public class SettingsService implements PersistentStateComponent<SettingsService> {

    private String selectedFunctionName;
    private String selectedZipArtifact;
    private String selectedRegionName;
    private String selectedProfileName;

    public static SettingsService getInstance() {
        return ServiceManager.getService(SettingsService.class);
    }

    public String getSelectedFunctionName() {
        return selectedFunctionName;
    }

    public void setSelectedFunctionName(String selectedFunctionName) {
        this.selectedFunctionName = selectedFunctionName;
    }

    public String getSelectedZipArtifact() {
        return selectedZipArtifact;
    }

    public void setSelectedZipArtifact(String selectedZipArtifact) {
        this.selectedZipArtifact = selectedZipArtifact;
    }

    public String getSelectedRegionName() {
        return selectedRegionName;
    }

    public void setSelectedRegionName(String selectedRegionName) {
        clearSelectedFunctionOnChangedRegion(selectedRegionName);
        this.selectedRegionName = selectedRegionName;
    }

    public String getSelectedProfileName() {
        return selectedProfileName;
    }

    public void setSelectedProfileName(String selectedProfileName) {
        this.selectedProfileName = selectedProfileName;
    }

    @Nullable
    @Override
    public SettingsService getState() {
        return this;
    }

    @Override
    public void loadState(SettingsService settingsService) {
        XmlSerializerUtil.copyBean(settingsService, this);
    }

    private void clearSelectedFunctionOnChangedRegion(String selectedRegionName) {
        //if new selected region is different than the current selected region then clear the selected function
        if (!isEmpty(selectedRegionName) && !selectedRegionName.equals(this.selectedRegionName)) {
            setSelectedFunctionName("");
        }
    }

}

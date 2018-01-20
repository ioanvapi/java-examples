package home.services;

import com.intellij.openapi.components.ServiceManager;
import home.model.FunctionEntry;
import home.model.ProfileEntry;
import home.model.RegionEntry;

import java.util.List;

public interface AwsService {

    static AwsService getInstance() {
        return ServiceManager.getService(AwsService.class);
    }

    void changeClient(String region, String profileName);

    List<FunctionEntry> getLambdaFunctionsForGo();

    List<RegionEntry> getRegions();

    RegionEntry getDefaultRegion();

    String getDefaultProfileName();

    List<ProfileEntry> getProfiles();
}

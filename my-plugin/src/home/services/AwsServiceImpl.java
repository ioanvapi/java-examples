package home.services;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.Runtime;
import home.model.FunctionEntry;
import home.model.ProfileEntry;
import home.model.RegionEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.util.TextUtils.isEmpty;


public class AwsServiceImpl implements AwsService {

    private static final String DEFAULT_REGION = Regions.EU_WEST_1.getName();
    private static final Map<String, String> regionDescriptions;

    static {
        regionDescriptions = new LinkedHashMap<>();
        regionDescriptions.put("us-east-2", "US East (Ohio)");
        regionDescriptions.put("us-east-1", "US East (N. Virginia)");
        regionDescriptions.put("us-west-1", "US West (N. California)");
        regionDescriptions.put("us-west-2", "US West (Oregon)");
        regionDescriptions.put("ap-northeast-2", "Asia Pacific (Seoul)");
        regionDescriptions.put("ap-south-1", "Asia Pacific (Mumbai)");
        regionDescriptions.put("ap-southeast-1", "Asia Pacific (Singapore)");
        regionDescriptions.put("ap-southeast-2", "Asia Pacific (Sydney)");
        regionDescriptions.put("ap-northeast-1", "Asia Pacific (Tokyo)");
        regionDescriptions.put("ca-central-1", "Canada (Central)");
        regionDescriptions.put("eu-central-1", "EU (Frankfurt)");
        regionDescriptions.put("eu-west-1", "EU (Ireland)");
        regionDescriptions.put("eu-west-2", "EU (London)");
        regionDescriptions.put("sa-east-1", "South America (São Paulo)");
    }

    private AWSLambda awsLambdaClient;
    private ArrayList<RegionEntry> regionEntries;

    public AwsServiceImpl() {
        awsLambdaClient = AWSLambdaClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .build();
    }

    //When profileName is null it will use default profile
    public void changeClient(String region, String profileName) {
        awsLambdaClient = AWSLambdaClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new ProfileCredentialsProvider(profileName))
                .build();
    }

    public List<FunctionEntry> getLambdaFunctionsForGo() {
        ListFunctionsResult result = awsLambdaClient.listFunctions();
        List<FunctionEntry> functionEntries = new ArrayList<>();

        for (FunctionConfiguration conf : result.getFunctions()) {
            FunctionEntry function = new FunctionEntry(conf);
            if (function.getRuntime().equals(Runtime.Go1X)) {
                functionEntries.add(function);
            }
        }

        return functionEntries;
    }

    //Default region is the region of default profile
    @Override
    public RegionEntry getDefaultRegion() {
        String defaultRegionName = new AwsProfileRegionProvider().getRegion();
        for (RegionEntry regionEntry : this.getRegions()) {
            if (regionEntry.getName().equals(defaultRegionName)) {
                return regionEntry;
            }
        }
        return null;
    }

    @Override
    public String getDefaultProfileName() {
        return "default";
    }

    public List<RegionEntry> getRegions() {
        if (regionEntries != null) {
            return regionEntries;
        }
        regionEntries = new ArrayList<>();
        for (Region region : RegionUtils.getRegions()) {
            String description = regionDescriptions.get(region.getName());
            if (description != null) {
                regionEntries.add(new RegionEntry(region, description));
            }
        }
        return regionEntries;
    }

    public List<ProfileEntry> getProfiles() {
        Map<String, BasicProfile> profiles = new ProfilesConfigFile().getAllBasicProfiles();
        List<ProfileEntry> profileEntries = new ArrayList<>();
        for (String profileName : profiles.keySet()) {
            BasicProfile basicProfile = profiles.get(profileName);
            RegionEntry regionEntry = getRegionForProfile(basicProfile.getProfileName());
            profileEntries.add(new ProfileEntry(basicProfile, regionEntry));
        }

        return profileEntries;
    }


    private RegionEntry getRegionForProfile(String profileName) {
        String regionName = new AwsProfileRegionProvider(profileName).getRegion();
        if (isEmpty(regionName)) {
            regionName = new AwsProfileRegionProvider(String.format("profile %s", profileName)).getRegion();
        }
        return getRegionEntry(regionName);
    }

    private RegionEntry getRegionEntry(String regionName) {
        for (RegionEntry regionEntry : this.getRegions()) {
            if (regionEntry.getName().equals(regionName)) {
                return regionEntry;
            }
        }
        return null;
    }
}

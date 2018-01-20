package home.model;

import com.amazonaws.auth.profile.internal.BasicProfile;
import static org.apache.http.util.TextUtils.isEmpty;

/*
* Model for ProfileEntry input fields
* */
public class ProfileEntry {
    private final BasicProfile basicProfile;
    private final RegionEntry region;

    public ProfileEntry(BasicProfile basicProfile, RegionEntry region) {
        this.basicProfile = basicProfile;
        this.region = region;
    }

    public String getName() {
        return this.basicProfile.getProfileName();
    }

    public RegionEntry getRegionEntry() {
        return region;
    }

    public BasicProfile getBasicProfile() {
        return basicProfile;
    }

    @Override
    public String toString() {
        String regionStr = isEmpty(this.region.getName()) ? "no region" : this.region.toString() ;
        return String.format("%s (%s)", getName(), regionStr);
    }
}

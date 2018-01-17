package home.model;

import com.amazonaws.auth.profile.internal.BasicProfile;
import static org.apache.http.util.TextUtils.isEmpty;

/*
* Model for ProfileEntry input fields
* */
public class ProfileEntry {
    private final BasicProfile basicProfile;

    public ProfileEntry(BasicProfile basicProfile) {
        this.basicProfile = basicProfile;
    }

    public String getName() {
        return this.basicProfile.getProfileName();
    }

    public BasicProfile getBasicProfile() {
        return basicProfile;
    }

    @Override
    public String toString() {
        String region = this.basicProfile.getRegion();
        String profileName = isEmpty(region) ? "no region" : region ;
        return String.format("%s (%s)", getName(), profileName);
    }
}

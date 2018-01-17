package home.model;

import com.amazonaws.regions.Region;

public class RegionEntry {
    private final Region region;
    private final String description;

    public RegionEntry(Region region, String description) {
        this.region = region;
        this.description = description;
    }

    public String getName() {
        return region.getName();
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return String.format("%s : %s", getName(), this.description);
    }
}

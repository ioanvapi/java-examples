package home.model;

public class ArtifactEntry {

    private String name;

    public ArtifactEntry(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ArtifactEntry{" +
                "name='" + name + '\'' +
                '}';
    }
}

package examples.models;

import com.intellij.openapi.project.Project;
import examples.entities.ArtifactEntry;

import java.util.Collection;

public interface ProjectModel {
    Collection<? extends ArtifactEntry> getJarArtifacts(Project project);
}

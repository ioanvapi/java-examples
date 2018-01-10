package examples;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface RepositoryService {

    @GET("/repositories")
    Call<List<Repository>> getRepositories();
}

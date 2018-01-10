package examples;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;


/*
* This example uses a service generator when you have multiple services
* for the same base url.
* */
public class Main {

    public static void main(String[] args) throws IOException {
        UserService userSvc = GithubServiceGenerator.createService(UserService.class, null);
        RepositoryService repoSvc = GithubServiceGenerator.createService(RepositoryService.class, null);

        Call<User> callSync = userSvc.getUser("ioanvapi");
        Response<User> res = callSync.execute();

        if (res.isSuccessful()) {
            User user = res.body();
            System.out.println(user);
        } else {
            System.out.println(res.errorBody());
        }

        Call<List<Repository>> repoCall = repoSvc.getRepositories();
        Response<List<Repository>> reposResponse = repoCall.execute();

        if (reposResponse.isSuccessful()) {
            List<Repository> repos = reposResponse.body();
            if (!repos.isEmpty()) {
                System.out.println(repos.get(0));
            }
        } else {
            System.out.println(reposResponse.errorBody());
        }

    }
}

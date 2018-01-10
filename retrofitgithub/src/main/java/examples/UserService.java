package examples;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;


/*
* http://www.baeldung.com/retrofit
* */
public interface UserService {
    /*
    * /users?per_page=4&page=10
    * */
    @GET("/users")
    Call<List<User>> getUsers(@Query("per_page") int perPage, @Query("page") int page);

    /*
    * /users/mojombo
    * */
    @GET("/users/{username}")
    Call<User> getUser(@Path("username") String username);
}

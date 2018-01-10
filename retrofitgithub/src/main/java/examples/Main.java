package examples;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * http://www.baeldung.com/retrofit
 * */
public class Main {

    //change this to try different approaches
    private static boolean BLOCKING = false;

    private static final String BASE_URL = "http://api.github.com";

    //Example of synchronous call to github api
    //It will block the current thread while transferring the data.
    public static void main(String[] args) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        UserService svc = retrofit.create(UserService.class);
        Call<User> call = svc.getUser("ioanvapi");

        if (BLOCKING) {
            //blocking
            syncCall(call);
        } else {
            //non blocking
            asyncCall(call);
        }
    }

    //It will NOT block the current thread while transferring the data.
    private static void asyncCall(Call<User> call) {
        //The 'enqueue' method does an asynchronous call.
        //It returns before the callback functions are invoked.
        //The callback will be invoked when response to the call is received.
        //This will execute in a separate thread.
        call.enqueue(new Callback<User>() {

            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    System.out.println(user);
                } else {
                    System.out.println(response.errorBody());
                }
            }

            public void onFailure(Call<User> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }


    //It will block the current thread while transferring the data.
    private static void syncCall(Call<User> callSync) {
        try {
            //'execute' method does a synchronous call
            Response<User> response = callSync.execute();
            if (response.isSuccessful()) {
                System.out.println(response.body());
            } else {
                System.out.println(response.errorBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

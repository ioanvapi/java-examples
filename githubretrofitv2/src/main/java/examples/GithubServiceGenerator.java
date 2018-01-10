package examples;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubServiceGenerator {
    private static final String BASE_URL = "http://api.github.com";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build());

    private static Retrofit retrofit = builder.build();

    //service creator that adds authentication header
    public static <T> T createService(Class<T> serviceClass, String token) {
        // add authentication
        if (token != null) {
            httpClient.interceptors().clear();
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request req = original.newBuilder()
                        .header("Authorization", token)
                        .build();
                return chain.proceed(req);
            });
        }
        return retrofit.create(serviceClass);
    }
}

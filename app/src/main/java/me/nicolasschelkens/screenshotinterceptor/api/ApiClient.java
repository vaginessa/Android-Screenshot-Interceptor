package me.nicolasschelkens.screenshotinterceptor.api;




import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nicolas on 8/3/16.
 */
public class ApiClient {

    private static OkHttpClient okClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", Constants.getClientAuth())
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            })
            .build();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL);

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(okClient).build();
        return retrofit.create(serviceClass);
    }
}

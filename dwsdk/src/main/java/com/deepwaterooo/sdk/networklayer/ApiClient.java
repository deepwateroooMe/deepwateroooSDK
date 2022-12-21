package com.deepwaterooo.sdk.networklayer;

// 现在仍是参考源项目,我觉得应该是去找年前年中写过的两个网络请求数据库什么的关于网络的利用框架的封装,感觉比这里写得要好
// 本质上原理应该是一样的,因为使用了相同的网络请求框架,只是版本不同而已

import android.content.Context;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.authentication.DWDialogActivity;
import com.deepwaterooo.sdk.appconfig.Logger;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * class used to create a Retrofit Builder to send network requests to an API.
 */
// 这里没有使用数据库,可能也就不得不这么封装吧,可以再参照自己年中的项目再对比理解一下
public class ApiClient {
// tmp borrow their server for tests......
    /**
     * it is basic URL of our API. We will use this URL for all network requests to an API
     */
    public static String BASE_URL = "https://services-qa.squarepanda.com/";

    public static final String DEV_URL = "https://services-dev.squarepanda.com/";
    public static final String QA_URL = "https://services-qa.squarepanda.com/";
    public static final String PROD_URL = "https://services.squarepanda.com/";

    // Release Build points to this
    public static final String BUILD_URL = "https://services-qa.squarepanda.com/";


    private static Retrofit retrofit = null;
    private static ApiInterface apiService = null;

    /**
     * Create the ApiInterface {Retrofit} instance using the configured values.
     * This ApiInterface handles server certification check also,
     * by default installing the all-trusting trust managers
     *
     * @return Retrofit ApiInterface
     * @param context
     */

    public static ApiInterface getApiInterface(Context context) throws Exception {
        if (context.getString(R.string.Enable_Production).equals("true")) {
            BASE_URL = PROD_URL;
        } else {
            BASE_URL = BUILD_URL;
        }
        if (apiService == null) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClientWithTrustAllHosts())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            }
            apiService = retrofit.create(ApiInterface.class);
        }
        return apiService;
    }

    /**
     * Trust every server - don't check for any certificate
     */
    private static OkHttpClient getOkHttpClientWithTrustAllHosts() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                }
            };

        /** Install the all-trusting trust manager*/
        OkHttpClient okHttpClient = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sc.getSocketFactory())
                .addInterceptor(interceptor)
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();
            return okHttpClient;

        } catch (Exception e) {
            Logger.error("ApiClient", "SSLContext Exception :" + e.toString());
        }

        return okHttpClient;
    }
}
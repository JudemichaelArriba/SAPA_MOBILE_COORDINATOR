package com.example.sapa.ApiAndInterface;

import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static volatile Retrofit retrofitInstance;
    private static final int TIMEOUT_MS = 300;

    private static final AtomicReference<String> detectedUrl = new AtomicReference<>(null);
    private static final String FALLBACK_URL = "http://192.168.43.100/android_api/";

    private ApiClient() {
    }

    public static Retrofit getClient(Context context) {
        if (retrofitInstance == null) {
            synchronized (ApiClient.class) {
                if (retrofitInstance == null) {


                    retrofitInstance = createRetrofit(FALLBACK_URL);
                    Log.d("ApiClient", "Using fallback URL: " + FALLBACK_URL);


                    new Thread(() -> {
                        String detected = detectServer(context);
                        if (detected != null) {
                            detectedUrl.set(detected);
                            Log.d("ApiClient", "Detected server URL: " + detected);

                            Retrofit newInstance = createRetrofit(detected);
                            retrofitInstance = newInstance;
                        }
                    }).start();
                }
            }
        }
        return retrofitInstance;
    }

    private static Retrofit createRetrofit(String baseUrl) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static String detectServer(Context context) {
        try {
            String subnet = getSubnet();
            Log.d("ApiClient", "Using subnet: " + subnet);
            for (int i = 1; i < 255; i++) {
                String testUrl = "http://" + subnet + i + "/android_api/ping.php";
                Log.d("ApiClient", "Trying URL: " + testUrl);
                if (isServerAlive(testUrl)) {
                    Log.d("ApiClient", "Server found at: " + testUrl);
                    return "http://" + subnet + i + "/android_api/";
                }
            }
        } catch (Exception e) {
            Log.e("ApiClient", "Error detecting server", e);
        }
        return null;
    }

    private static String getSubnet() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ip = inetAddress.getHostAddress();
                        Log.d("ApiClient", "Detected device IP: " + ip);
                        String[] parts = ip.split("\\.");
                        return parts[0] + "." + parts[1] + "." + parts[2] + ".";
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("ApiClient", "Failed to get device IP", ex);
        }
        return "192.168.43.";
    }

    private static boolean isServerAlive(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            Log.d("ApiClient", "Server not alive at: " + urlString);
            return false;
        }
    }

    public static String scanServerBlocking(Context context) {

        if (retrofitInstance != null)
            return detectedUrl.get() != null ? detectedUrl.get() : FALLBACK_URL;


        String detected = detectServer(context);

        if (detected != null) {
            detectedUrl.set(detected);
            retrofitInstance = createRetrofit(detected);
            Log.d("ApiClient", "Server detected: " + detected);
            return detected;
        } else {

            retrofitInstance = createRetrofit(FALLBACK_URL);
            Log.d("ApiClient", "Using fallback URL: " + FALLBACK_URL);
            return FALLBACK_URL;
        }
    }


}

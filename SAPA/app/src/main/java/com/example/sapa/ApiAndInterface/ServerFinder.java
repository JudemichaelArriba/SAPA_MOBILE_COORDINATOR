package com.example.sapa.ApiAndInterface;




import android.content.Context;
import android.net.wifi.WifiManager;

import java.net.HttpURLConnection;
import java.net.URL;
public class ServerFinder {




    public interface ServerCallback {
        void onServerFound(String baseUrl);
        void onServerNotFound();
    }

    public static void findServer(Context context, ServerCallback callback) {
        new Thread(() -> {
            try {
                String subnet = getSubnet(context);
                for (int i = 1; i < 255; i++) {
                    String host = subnet + i;
                    String testUrl = "http://" + host + "/android_api/ping.php";

                    if (isServerAlive(testUrl)) {
                        callback.onServerFound("http://" + host + "/android_api/");
                        return;
                    }
                }
                callback.onServerNotFound();
            } catch (Exception e) {
                callback.onServerNotFound();
            }
        }).start();
    }

    private static String getSubnet(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = wm.getConnectionInfo().getIpAddress();
        String ipString = String.format("%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff),
                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        return ipString.substring(0, ipString.lastIndexOf('.') + 1);
    }

    private static boolean isServerAlive(String testUrl) {
        try {
            URL url = new URL(testUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(200);
            conn.setReadTimeout(200);
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            return (responseCode == 200);
        } catch (Exception e) {
            return false;
        }
    }





















}


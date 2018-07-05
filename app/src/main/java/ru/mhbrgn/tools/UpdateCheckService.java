package ru.mhbrgn.tools;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheckService extends IntentService {

    // Here set your DownloadCenter URI (without "/" or "?" on end)
    private static final String SERVER_URI = "https://mhbrgn.gitlab.io/downloadcenter";
    private static final String TAG = "CheckUpdates";
    private static final String STATUS_ERROR = "714";

    static final String ACTION_CHECK_FOR_UPDATES = "ru.mhbrgn.CHECK_UPDATES";

    public UpdateCheckService() {
        super("UpdateCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String pkg = intent.getStringExtra("PKG");
        // Remove .debug from end
        if(pkg.endsWith(".debug")) pkg = pkg.substring(0, pkg.length()-6);

        String uri = SERVER_URI+"/"+pkg+"/data.json";
        Log.i(TAG, "onHandleIntent: Checking... Download: "+uri);
        String json = httpDownload(uri);
        if(json.equals(STATUS_ERROR)) return;

        try {

            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            JSONObject a = new JSONObject(json);

            if(a.getInt("versionCode") > info.versionCode) {
                Intent responseIntent = new Intent();
                responseIntent.setAction(ACTION_CHECK_FOR_UPDATES);
                responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                responseIntent.putExtra("NEW_VERSION", a.getInt("versionCode"));
                responseIntent.putExtra("LINK", SERVER_URI+"/"+pkg+"/"+a.getString("filename"));
                sendBroadcast(responseIntent);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private String httpDownload(String href) {
        try {
            URL url = new URL(href);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.connect();

            int code = con.getResponseCode();
            Log.i(TAG, "httpDownload: Status code "+code);
            if(code != 200) return STATUS_ERROR;

            InputStream i = new BufferedInputStream(con.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(i));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }

            r.close();
            i.close();
            con.disconnect();

            return sb.toString();

        } catch(Throwable e) {
            e.printStackTrace();
            return STATUS_ERROR;
        }
    }

}

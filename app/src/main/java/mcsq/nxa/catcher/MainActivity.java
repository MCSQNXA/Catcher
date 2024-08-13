package mcsq.nxa.catcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @Author MCSQNXA
 * @CreateTime 2024-08-13 下午9:15
 * @Description 主活动
 */
public class MainActivity extends Activity {


    public void showReadWritePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {//11
            boolean allow = Environment.isExternalStorageManager();

            if (!allow) {
                activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).setData(Uri.parse("package:" + activity.getPackageName())), 1024);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {//6.0
            String[] uses = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_PHONE_STATE};

            for (String p : uses) {
                if (activity.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(uses, 0XCF);
                    break;
                }
            }
        }
    }


    @SuppressLint({"SdCardPath", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView tv = new TextView(this);
        tv.setTextSize(23);
        tv.setText("日志在 /sdcard/Catcher.log");

        super.onCreate(savedInstanceState);
        super.setContentView(tv);

        this.showReadWritePermission(this);

        new Thread(() -> {
            while (true) {
                try {
                    ArrayList<String> commandLine = new ArrayList<>();
                    commandLine.add("logcat");
                    commandLine.add("-d");

                    Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[]{}));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        FileOutputStream out = new FileOutputStream("/sdcard/Catcher.log", true);
                        out.write(line.getBytes());
                        out.write('\n');
                        out.flush();
                        out.close();
                    }


                } catch (Exception e) {
                    try {
                        FileOutputStream out = new FileOutputStream("/sdcard/Catcher.log", true);
                        out.write(e.toString().getBytes());
                        out.write('\n');
                        out.flush();
                        out.close();
                    } catch (Exception ignored) {

                    }
                }
            }
        }).start();
    }


}

package com.takpap.toggleproxy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView tvHost;
    private TextView tvPort;
    private Button btProxy;
    private SharedPreferences sp;
    private Boolean isProxying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("ServerProxy", 0);
        tvHost = findViewById(R.id.proxyHost);
        tvPort = findViewById(R.id.proxyPort);
        btProxy = findViewById(R.id.ToggleProxy);
        initData();
        btProxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProxying = sp.getBoolean("isProxying", false);
                setSharedPreference();
                if (!isProxying) {
                    //runRootCommand("settings put global http_proxy " + getTextViewText(tvHost) + ":" + getTextViewText(tvPort));
                    String re = execRootCmd("settings put global http_proxy " + getTextViewText(tvHost) + ":" + getTextViewText(tvPort));
                    Log.d("testproxy",re);
                    Toast.makeText(MainActivity.this, "代理已经成功! "+re, Toast.LENGTH_SHORT).show();
                } else {
                    //runRootCommand("settings put global http_proxy :0");
                    execRootCmd("settings put global http_proxy :0");
                    Toast.makeText(MainActivity.this, "代理已经关闭! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**************************************************************************************/
    private void initData() {
        String host = sp.getString("host", "");
        String port = sp.getString("port", "");
        if (!host.isEmpty() && !port.isEmpty()) {
            tvHost.setText(host);
            tvPort.setText(port);
        } else {

        }

    }

    private void setSharedPreference() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putBoolean("isProxying", !isProxying);
        editor.putString("host", getTextViewText(tvHost));
        editor.putString("port", getTextViewText(tvPort));
        editor.apply();

    }

    private String getTextViewText(TextView tv) {
        return tv.getText().toString().trim();
    }

    ;

    /*************************************************************************/
    // Root 执行没有返回
    public static void runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
    }

    /*************************************************************************/
    //以 Root 执行命令获取返回值
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("testproxy","result");
        return result;
    }
}
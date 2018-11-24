package com.example.lvmufan.myapplication;

import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog progressDialog ;
    EditText usernameText = (EditText) this.findViewById(R.id.login_username);
    EditText passwordText = (EditText) this.findViewById(R.id.login_password);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) this.findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    processLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }); //等待button被按的响应

        Button registerButton = (Button) this.findViewById(R.id.sign_up_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

    }

    //process login
    private void processLogin() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String username = usernameText.getText().toString();
                    String password = passwordText.getText().toString();
                    final OkHttpClient login_client = new OkHttpClient();//用okhttp的网络架构进行登录
                    RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输用户名和密码
                            .add("username", username)
                            .add("password", password)
                            .build();

                    final Request request = new Request.Builder()
                            .url("http://10.15.82.223:9090/app_get_data/app_signincheck")//指定访问的服务器地址
                            .post(postBody)
                            .build();

                    setProgressDialog();
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(intent);

                    login_client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("++CONNECTION++", "ashe nai !!") ;
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            progressDialog.dismiss();
                            Log.d("++CONNECTION++", "asche!!");
                            String json_string = response.body().string() ;


                        }
                    });

                    Response response = login_client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);//调用parseJSONWithJSONObject()方法来解析数据

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    private void setProgressDialog(){

        progressDialog = new ProgressDialog(LoginActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Connecting to server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String message = jsonObject.getString("msg");
                String msg = unicodeToUtf8(message);//对数据进行Unicode转码为中文字符
                Log.d("msg",msg);//打印传输回来的消息
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}


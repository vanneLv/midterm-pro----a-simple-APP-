package com.example.lvmufan.myapplication;

import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

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
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog progressDialog ;
    final public static User user = new User();
    int state = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) this.findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                processLogin();
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
    private void processLogin(){//} throws IOException {
        EditText usernameText = (EditText) this.findViewById(R.id.login_username);
        EditText passwordText = (EditText) this.findViewById(R.id.login_password);
        user.setUsername(usernameText.getText().toString());
        user.setPassword(passwordText.getText().toString());

        OkHttpClient login_client = new OkHttpClient();//用okhttp的网络架构进行登录
        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输用户名和密码
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_signincheck")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = login_client.newCall(request);
        setProgressDialog();//显示联网状态

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!") ;
            }//对服务器请求失败

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                Log.d("CONNECTION", "请求成功");
                final String responseData = response.body().string() ;
                parseJSONWithJSONObject(responseData);//调用parseJSONWithJSONObject()方法来解析数据
            }//对服务器请求成功
        });
    }

    private void setProgressDialog(){//联网状态的显示
        progressDialog = new ProgressDialog(LoginActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Connecting to server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void parseJSONWithJSONObject(String responseData){
        String msg;
        try{
                JSONObject jsonObject = new JSONObject(responseData.substring(responseData.indexOf("{"), responseData.lastIndexOf("}") + 1)) ;
                String message = jsonObject.getString("msg");
                msg = MyMessageTools.unicodeToUtf8(message);//对数据进行Unicode转码为中文字符

                 if(msg.equals("登录成功")){//如果登录成功
                     String token = jsonObject.getString("token");
                     state = 1;
                     Log.d("msg", msg);//打印传输回来的消息以及token
                     Log.d("token",token);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(LoginActivity.this, "登录成功",Toast.LENGTH_LONG ).show();
                         }
                     });
                     Intent intent = new Intent();
                     intent.setClass(LoginActivity.this,MainMenuActivity.class);
                     startActivity(intent);//跳转到登录后的主界面

                     user.setToken(token);
                     SharedPreferences sp = getSharedPreferences("loginToken", MODE_MULTI_PROCESS);
                     SharedPreferences.Editor editor = sp.edit();
                     editor.putString("username", user.getUsername());//对username进行存储，在主界面个人信息中显示
                     editor.putString("token", token);//存储token验证登录情况
                     editor.apply();
                 }
                 else{//如果登录失败
                     Log.d("msg", msg);//打印传输回来的消息
                     Looper.prepare();
                     Toast.makeText(LoginActivity.this, msg,Toast.LENGTH_LONG ).show();
                     Looper.loop();;
                 }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


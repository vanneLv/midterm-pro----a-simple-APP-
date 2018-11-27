package com.example.lvmufan.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    ProgressDialog progressDialog ;
    final public static User user = new User();
    int state = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button registerButton = (Button) this.findViewById(R.id.sign_up_confirm_button);
        Button cancelButton = (Button) this.findViewById(R.id.cancel_button);

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                processRegister();
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                SignupActivity.this.finish();
            }
        });
    }

    public void processRegister() {
        EditText usernameText = (EditText) this.findViewById(R.id.signup_username);
        EditText mailText = (EditText) this.findViewById(R.id.signup_email);
        EditText passwordText = (EditText) this.findViewById(R.id.signup_password);

        user.setUsername(usernameText.getText().toString());
        user.setMail(mailText.getText().toString());
        user.setPassword(passwordText.getText().toString());
        OkHttpClient login_client = new OkHttpClient();//用okhttp的网络架构进行注册
        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输用户名邮箱和密码
                .add("username", user.getUsername())
                .add("email",user.getMail())
                .add("password", user.getPassword())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_register")//指定访问的服务器地址
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
        private void parseJSONWithJSONObject(String responseData){
            String msg;
            try{
                JSONObject jsonObject = new JSONObject(responseData.substring(responseData.indexOf("{"), responseData.lastIndexOf("}") + 1)) ;
                String message = jsonObject.getString("msg");
                msg = MyMessageTools.unicodeToUtf8(message);//对数据进行Unicode转码为中文字符

                    Log.d("msg", msg);//打印传输回来的消息
                    Looper.prepare();
                    Toast.makeText(SignupActivity.this, msg,Toast.LENGTH_LONG ).show();
                    Looper.loop();;

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    private void setProgressDialog(){//联网状态的显示
        progressDialog = new ProgressDialog(SignupActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Connecting to server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}

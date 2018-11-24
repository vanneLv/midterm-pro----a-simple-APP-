package com.example.lvmufan.myapplication;

import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ClickListener cl = new ClickListener();

        Button loginButton = (Button) this.findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(cl);

        Button registerButton = (Button) this.findViewById(R.id.sign_up_button);
        registerButton.setOnClickListener(cl);

    }

    class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    processLogin();
                    break;
                case R.id.sign_up_button:
                    Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    //process login
    public void processLogin() {
        EditText usernameText = (EditText) this.findViewById(R.id.login_username);
        EditText passwordText = (EditText) this.findViewById(R.id.login_password);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name", username);
        params.put("password", password);

        String url = "10.15.82.223:9090/app_get_data/app_signincheck ";
        this.run(params,url);
    }

    //上传数据到服务器匹配
    public void run(HashMap<String, String> params, String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, url,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                String status = null;
                try {
                    status = response.getString("status");
                    Log.e("status","status"+status);
                    status = "0";
                    Log.e("status","status"+status);
                    if (status.equals("0")) {
                        Toast.makeText(LoginActivity.this, "登录成功!",Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败！",Toast.LENGTH_LONG ).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
        requestQueue.add(jsonRequest);
        requestQueue.start();
    }

}


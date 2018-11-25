package com.example.lvmufan.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
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
import android.content.SharedPreferences;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog progressDialog;
    User user = new User();
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences sp = getSharedPreferences("loginToken", MODE_MULTI_PROCESS);
        user.setUsername(sp.getString("username", "user"));
        user.setToken(sp.getString("token"," "));
        //token = sp.getString("token",null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_name_entity) {
            //handle the name_entity
            processGetNameEntityText();
        } else if (id == R.id.nav_relation) {
            processGetRelationEntityText();

        } else if (id == R.id.nav_tool) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_sign_out) {
            //processSignOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void processGetRelationEntityText() {

    }

    private void processGetNameEntityText() {
        OkHttpClient get_entity = new OkHttpClient();//用okhttp的网络架构进行登录

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输用户名和密码
                .add("token", user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_get_entity")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = get_entity.newCall(request);
        setProgressDialouge();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                progressDialog.dismiss();
                Log.d("CONNECTION", "请求成功");
                String responseData = response.body().string();
                parseJSONWithJSONObject(responseData);//调用parseJSONWithJSONObject()方法来解析数据
            }
        });
    }

    private void processSignOut() {//} throws IOException {

    }

    private void setProgressDialouge(){
        progressDialog = new ProgressDialog(MainMenuActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Getting the entity...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void parseJSONWithJSONObject(String responseData){
        String msg;
        try{
            JSONObject jsonObject = new JSONObject(responseData.substring(responseData.indexOf("{"), responseData.lastIndexOf("}") + 1)) ;
            String message = jsonObject.getString("msg");
            msg = Message.unicodeToUtf8(message);//对数据进行Unicode转码为中文字符

            if(msg.equals("尚未登录")){
                Log.d("msg", msg);//打印传输回来的消息
                Looper.prepare();
                Toast.makeText(MainMenuActivity.this, msg,Toast.LENGTH_LONG ).show();
                Looper.loop();
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else{//如果成功
                Log.d("msg", msg);//打印传输回来的消息
                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                String sent_id = jsonObject.getString("sent_id");
                String doc_id = jsonObject.getString("doc_id");

                TextView tv = (TextView) findViewById(R.id.content_tv);
                tv.setText("title: "+ title+"content"+content+"sent_id"+sent_id+"doc_id"+doc_id);

                Looper.prepare();
                Toast.makeText(MainMenuActivity.this, msg,Toast.LENGTH_LONG ).show();
                Looper.loop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}

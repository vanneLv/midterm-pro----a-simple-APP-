package com.example.lvmufan.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.text.BreakIterator;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.SharedPreferences;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog progressDialog;
    User user = new User();
    TextView responseText;
    HighlightStructure highlight = new HighlightStructure();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        responseText = (TextView) findViewById(R.id.response_content);
        responseText.setMovementMethod(ScrollingMovementMethod.getInstance());//增加滚动功能

        //SpannableString s = new SpannableString(getResources().getString(R.string.));
        /*Pattern p = Pattern.compile("abc");
        SpannableStringBuilder style=new SpannableStringBuilder(strs);
        style.setSpan(new BackgroundColorSpan(Color.RED),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.RED),7,9,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textview.setText(style);*/

        //Matcher m = p.matcher(s);


        SharedPreferences sp = getSharedPreferences("loginToken", MODE_MULTI_PROCESS);
        user.setUsername(sp.getString("username", "user"));
        user.setToken(sp.getString("token"," "));

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
            //handle the relation entity
            processGetRelationEntityText();
        } else if (id == R.id.nav_tool) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_sign_out) {
            //user sign out
            processSignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //获取关系标注的函数
    private void processGetRelationEntityText() {
        OkHttpClient get_triples = new OkHttpClient();//用okhttp的网络架构进行登录

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输token
                .add("token", user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_get_triple")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = get_triples.newCall(request);
        setProgressDialog();

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
                parseJSONWithJSONObject_get(responseData);//调用parseJSONWithJSONObject()方法来解析数据
                String message = Message.unicodeToUtf8(responseData);
                showResponseRelationEntity(message);
            }
        });

    }
    private void showResponseRelationEntity(final String response) {
        //在子线程中更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                for(int i=0;i<highlight.getLeftEnd().size();i++){
                    SpannableStringBuilder spannable = new SpannableStringBuilder(response);
                    //设置文字的前景色
                    spannable.setSpan(new ForegroundColorSpan(Color.RED),(int)highlight.getLeftStart().get(i),(int)highlight.getLeftEnd().get(i),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(Color.RED),(int)highlight.getRightStart().get(i),(int)highlight.getRightEnd().get(i),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                responseText.setText(response);
            }
        });
    }

    //获取命名实体的函数
    private void processGetNameEntityText() {
        OkHttpClient get_entity = new OkHttpClient();//用okhttp的网络架构进行登录

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输token
                .add("token", user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_get_entity")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = get_entity.newCall(request);
        setProgressDialog();

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
                parseJSONWithJSONObject_get(responseData);//调用parseJSONWithJSONObject()方法来解析数据
                String message = Message.unicodeToUtf8(responseData);
                showResponseNameEntity(message);
            }
        });
    }
    private void showResponseNameEntity(final String response) {
        //在子线程中更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                responseText.setText(response);
            }
        });
    }


    //用户登出操作的函数
    private void processSignOut() {
        OkHttpClient sign_out = new OkHttpClient();//用okhttp的网络架构进行登录

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输用户名和密码
                .add("token", user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_logout")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = sign_out.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("CONNECTION", "请求成功");
                String responseData = response.body().string();
                parseJSONWithJSONObject_get(responseData);//调用parseJSONWithJSONObject()方法来解析数据
            }
        });

    }

    private void setProgressDialog(){
        progressDialog = new ProgressDialog(MainMenuActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Getting the data...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void parseJSONWithJSONObject_get(String responseData){
        try{
            JSONObject jsonObject = new JSONObject(responseData) ;
            if(jsonObject.has("msg")){
                String message = jsonObject.getString("msg");
                String msg = Message.unicodeToUtf8(message);//对数据进行Unicode转码为中文字符
                if(msg.equals("尚未登录")){
                    Log.d("msg", msg);//打印传输回来的消息
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainMenuActivity.this, "尚未登录",Toast.LENGTH_LONG ).show();
                        }
                    });
                }
                else if(msg.equals("登出成功")){
                    Log.d("msg", msg);//打印传输回来的消息
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainMenuActivity.this, "登出成功",Toast.LENGTH_LONG ).show();
                        }
                    });
                }
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else{//如果成功
                if(jsonObject.has("triples")){
                    String doc_id = jsonObject.getString("doc_id");
                    String sent_id = jsonObject.getString("sent_id");
                    String title = jsonObject.getString("title");
                    String sent_ctx = jsonObject.getString("sent_ctx");

                    JSONArray triplesJSArray = jsonObject.getJSONArray("triples");
                    for(int i=0;i<triplesJSArray.length();i++){
                        JSONObject triplesJSON = triplesJSArray.getJSONObject(i);
                        highlight.getLeftStart().add(triplesJSON.getInt("left_e_start"));
                        highlight.getLeftEnd().add(triplesJSON.getInt("left_e_end"));
                        highlight.getRightStart().add(triplesJSON.getInt("right_e_start"));
                        highlight.getRightEnd().add(triplesJSON.getInt("right_e_end"));
                    }//存储四组左右起始点信息，为文本高亮做准备

                    String triples = jsonObject.getString("triples");

                    Log.d("doc_id",doc_id);
                    Log.d("sent_id",sent_id);
                    Log.d("title",title);
                    Log.d("sent_ctx",sent_ctx);
                    Log.d("triples",triples);
                }
                else{
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    String sent_id = jsonObject.getString("sent_id");
                    String doc_id = jsonObject.getString("doc_id");
                    Log.d("title",title);
                    Log.d("content",content);
                    Log.d("sent_id",sent_id);
                    Log.d("doc_id",doc_id);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

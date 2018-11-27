package com.example.lvmufan.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextPaint;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
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

import android.text.Spannable;
import android.text.SpannableStringBuilder;


public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView responseTitle;
    TextView responseText;
    TableLayout tableLayout;
    ProgressDialog progressDialog;
    User user = new User();
    HighlightStructure highlight = new HighlightStructure();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        responseTitle = (TextView) findViewById(R.id.response_title);
        TextPaint tp = responseTitle.getPaint();
        tp.setFakeBoldText(true);//标题加粗功能

        responseText = (TextView) findViewById(R.id.response_content);
        responseText.setMovementMethod(ScrollingMovementMethod.getInstance());//增加滚动功能

        tableLayout = (TableLayout) findViewById(R.id.entity_relation_display);

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
                String message1 = MyMessageTools.unicodeToUtf8(MyMessageTools.getTitleFromResponse(responseData));
                String message2 = MyMessageTools.unicodeToUtf8(MyMessageTools.getContentFromResponse(responseData));
                String[] leftEntity =new String[highlight.getLeftEntity().size()];
                highlight.getLeftEntity().toArray(leftEntity);
                String[] rightEntity =new String[highlight.getRightEntity().size()];
                highlight.getRightEntity().toArray(rightEntity);
                String[] relationId =new String[highlight.getRelationId().size()];
                highlight.getRelationId().toArray(relationId);
                showResponseRelationEntity(message1,message2,leftEntity,rightEntity,relationId);
            }
        });

    }
    private void showResponseRelationEntity(final String response_title,final String response_content,final String[] response_leftEntity, final String[] response_rightEntity, final String[] response_relationId) {
        //在子线程中更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                SpannableStringBuilder spannable = new SpannableStringBuilder(response_content);
                for(int i=0;i<highlight.getLeftEnd().size();i++){
                    //设置文字的前景色
                    spannable.setSpan(new ForegroundColorSpan(Color.BLUE),(int)highlight.getLeftStart().get(i),(int)highlight.getLeftEnd().get(i),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(Color.RED),(int)highlight.getRightStart().get(i),(int)highlight.getRightEnd().get(i),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                responseTitle.setText(response_title);
                responseText.setText(spannable);

                //清空上次操作
                int len = tableLayout.getChildCount();
                if (len > 1) { //这里的判断我是为了实现动态更新数据...保留标题
                    //必须从后面减去子元素
                    for (int i = len + 1; i > 0; i--) {
                        tableLayout.removeView(tableLayout.getChildAt(i));
                    }
                }

                for(int i = 0; i < highlight.getRelationId().size();i++){
                    TableRow row = new TableRow(getApplicationContext());

                    TextView left_entity = new TextView(getApplicationContext());
                    //TableRow.LayoutParams lp_entity = (TableRow.LayoutParams) left_entity.getLayoutParams();
                    //lp_entity.width = 0;
                    //lp_entity.weight = 3;
                    //lp_entity.setMargins(16,16,16,16);
                    //left_entity.setLayoutParams(lp_entity);
                    left_entity.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    left_entity.setFocusable(true);
                    left_entity.setFocusableInTouchMode(true);
                    left_entity.setTextIsSelectable(true);
                    left_entity.setSingleLine(true);
                    left_entity.setTextSize(18);
                    //MyMessageTools.setRelationViewTextStyle(left_entity,true);
                    left_entity.setTextColor(Color.BLUE);
                    left_entity.setText(response_leftEntity[i]);
                    row.addView(left_entity);

                    TextView right_entity = new TextView(getApplicationContext());
                    //TableRow.LayoutParams lp_entity2 = (TableRow.LayoutParams) right_entity.getLayoutParams();
                    //lp_entity2.width = 0;
                    //lp_entity2.weight = 3;
                    //lp_entity2.setMargins(16,16,16,16);
                    //right_entity.setLayoutParams(lp_entity2);
                    right_entity.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    right_entity.setFocusable(true);
                    right_entity.setFocusableInTouchMode(true);
                    right_entity.setTextIsSelectable(true);
                    right_entity.setSingleLine(true);
                    right_entity.setTextSize(18);
                    //MyMessageTools.setRelationViewTextStyle(right_entity,true);
                    right_entity.setTextColor(Color.RED);
                    right_entity.setText(response_rightEntity[i]);
                    row.addView(right_entity);

                    TextView relation = new TextView(getApplicationContext());
                    //TableRow.LayoutParams lp_relation = (TableRow.LayoutParams) relation.getLayoutParams();
                    //lp_relation.width = 0;
                    //lp_relation.weight = 2;
                    //lp_relation.setMargins(16,16,16,16);
                    //relation.setLayoutParams(lp_relation);
                    relation.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    relation.setFocusable(true);
                    relation.setFocusableInTouchMode(true);
                    relation.setTextIsSelectable(true);
                    relation.setSingleLine(true);
                    relation.setTextSize(18);
                    //MyMessageTools.setRelationViewTextStyle(relation,false);
                    int relation_id = Integer.parseInt(response_relationId[i]);
                    String relation_text = null;
                    if(relation_id == 0){
                        relation_text = MyMessageTools.unicodeToUtf8("任职");
                    }
                    else if(relation_id == 1){
                        relation_text = MyMessageTools.unicodeToUtf8("亲属");
                    }
                    relation.setText(relation_text);
                    relation.setTextColor(Color.BLACK);
                    row.addView(relation);

                    tableLayout.addView(row);
                }
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
                String message1 = MyMessageTools.unicodeToUtf8(MyMessageTools.getTitleFromResponse(responseData));
                String message2 = MyMessageTools.unicodeToUtf8(MyMessageTools.getContentFromResponse(responseData));
                showResponseNameEntity(message1,message2);
            }
        });
    }
    private void showResponseNameEntity(final String response_title,final String response_content) {
        //在子线程中更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                responseTitle.setText(response_title);
                responseText.setText(response_content);

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


    //获取JSON数据的函数
    private void parseJSONWithJSONObject_get(String responseData){
        try{
            JSONObject jsonObject = new JSONObject(responseData);
            highlight.getLeftStart().clear();
            highlight.getLeftEnd().clear();
            highlight.getRightStart().clear();
            highlight.getRightEnd().clear();
            if(jsonObject.has("msg")){
                String message = jsonObject.getString("msg");
                String msg = MyMessageTools.unicodeToUtf8(message);//对数据进行Unicode转码为中文字符
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
                    String triples = jsonObject.getString("triples");

                    JSONArray triplesJSArray = jsonObject.getJSONArray("triples");
                    for(int i=0;i<triplesJSArray.length();i++){
                        JSONObject triplesJSON = triplesJSArray.getJSONObject(i);
                        highlight.getLeftStart().add(triplesJSON.getInt("left_e_start"));
                        highlight.getLeftEnd().add(triplesJSON.getInt("left_e_end"));
                        highlight.getRightStart().add(triplesJSON.getInt("right_e_start"));
                        highlight.getRightEnd().add(triplesJSON.getInt("right_e_end"));
                        highlight.getLeftEntity().add(triplesJSON.getString("left_entity"));
                        highlight.getRightEntity().add(triplesJSON.getString("right_entity"));
                        highlight.getRelationId().add(triplesJSON.getString("relation_id"));
                    }//存储四组左右起始点信息，为文本高亮做准备

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


    //输出加载框的函数
    private void setProgressDialog(){
        progressDialog = new ProgressDialog(MainMenuActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Getting the data...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}

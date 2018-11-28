package com.example.lvmufan.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
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


public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView responseTitle;
    TextView responseText;
    TableLayout tableLayout;
    ProgressDialog progressDialog;
    User user = new User();
    TriplesStructure triples = new TriplesStructure();
    EntitiesStructure Entities = new EntitiesStructure();
    boolean isStateEntity = false;
    boolean isStateTriples = false;

    private ActionMode.Callback callback2= new ActionMode.Callback(){
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater menuInflater = actionMode.getMenuInflater();
            menuInflater.inflate(R.menu.selection_action_menu,menu);
            int start = responseText.getSelectionStart();//获取选择部分的起始信息
            int end = responseText.getSelectionEnd();
            System.out.print(start);
            System.out.print(end);
            return true;//返回false则不会显示弹窗
        }
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater menuInflater = actionMode.getMenuInflater();
            menu.clear();
            menuInflater.inflate(R.menu.selection_action_menu,menu);
            return true;
        }
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            //根据item的ID处理点击事件
            int start = responseText.getSelectionStart();//获取选择部分的起始信息
            int end = responseText.getSelectionEnd();
            // 获得选中的字符
            System.out.print(start);
            System.out.print(end);
            switch (menuItem.getItemId()){
                case R.id.name:
                    Toast.makeText(MainMenuActivity.this, "人名", Toast.LENGTH_SHORT).show();
                    SpannableStringBuilder styled1 = new SpannableStringBuilder(responseText.getText());
                    styled1.setSpan(new ForegroundColorSpan(Color.RED), start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    String EntityName =  responseText.getText().toString().substring(start,end);
                    Entities.getEntityName().add(EntityName);
                    Entities.getStart().add(start);
                    Entities.getEnd().add(end);
                    Entities.getNerTag().add("PERSON");
                    Log.d(responseText.getText().toString().substring(start,end), "这是我点击标注人名后获得的字符串 ");
                    responseText.setText(styled1);//更改选中部分的颜色
                    actionMode.finish();//收起操作菜单
                    break;
                case R.id.job:
                    Toast.makeText(MainMenuActivity.this, "职位", Toast.LENGTH_SHORT).show();
                    SpannableStringBuilder styled2 = new SpannableStringBuilder(responseText.getText());
                    styled2.setSpan(new ForegroundColorSpan(Color.GREEN), start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    String EntityName1 =  responseText.getText().toString().substring(start,end);
                    Entities.getEntityName().add(EntityName1);
                    Entities.getStart().add(start);
                    Entities.getEnd().add(end);
                    Entities.getNerTag().add("TITLE");
                    Log.d(responseText.getText().toString().substring(start,end), "这是我点击标注职位后获得的字符串 ");
                    responseText.setText(styled2);
                    actionMode.finish();
                    break;
            }
            return true;//返回true则系统的"复制"、"搜索"之类的item将无效，只有自定义item有响应
        }
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        responseTitle = (TextView) findViewById(R.id.response_title);
        TextPaint tp = responseTitle.getPaint();
        tp.setFakeBoldText(true);//标题加粗功能

        //关系标注标题加粗
        TextView relationTitle1 = (TextView) findViewById(R.id.left_entity_title);
        TextPaint tp1 = relationTitle1.getPaint();
        tp1.setFakeBoldText(true);
        TextView relationTitle2 = (TextView) findViewById(R.id.right_entity_title);
        TextPaint tp2 = relationTitle2.getPaint();
        tp2.setFakeBoldText(true);
        TextView relationTitle3 = (TextView) findViewById(R.id.relation_title);
        TextPaint tp3 = relationTitle3.getPaint();
        tp3.setFakeBoldText(true);

        responseText = (TextView) findViewById(R.id.response_content);
        responseText.setCustomSelectionActionModeCallback(callback2);
        tableLayout = (TableLayout) findViewById(R.id.entity_relation_display);
        responseText.setMovementMethod(LinkMovementMethod.getInstance());

        SharedPreferences sp = getSharedPreferences("loginToken", MODE_MULTI_PROCESS);
        user.setUsername(sp.getString("username", "user"));
        user.setToken(sp.getString("token"," "));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button lastPageButton = (Button) this.findViewById(R.id.last_page_button);
        lastPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStateEntity){
                    processGetNameEntityText();
                }
                else if(isStateTriples){
                    processGetRelationTriplesText();
                }
            }
        }); //等待上一页button被按的响应

        Button nextPageButton = (Button) this.findViewById(R.id.next_page_button);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStateEntity){
                    processGetNameEntityText();
                }
                else if(isStateTriples){
                    processGetRelationTriplesText();
                }
            }
        }); //等待下一页button被按的响应

        Button uploadButton = (Button) this.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStateEntity){
                    processUploadNameEntityText();
                }
                else if(isStateTriples){
                    processUploadRelationTriplesText();
                }
            }
        }); //等待提交button被按的响应

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
            isStateEntity = true;
            isStateTriples = false;
            processGetNameEntityText();
        } else if (id == R.id.nav_relation) {
            //handle the relation entity
            isStateTriples = true;
            isStateEntity = false;
            processGetRelationTriplesText();
        } else if (id == R.id.nav_tool) {
            isStateEntity = false;
            isStateTriples = false;

        }else if (id == R.id.nav_sign_out) {
            //user sign out
            processSignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //获取关系标注的函数
    private void processGetRelationTriplesText() {
        OkHttpClient get_triples = new OkHttpClient();//用okhttp的网络架构

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输token
                .add("token", user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_get_triple")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = get_triples.newCall(request);
        //setProgressDialog_get();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //progressDialog.dismiss();
                Log.d("CONNECTION", "请求成功");
                String responseData = response.body().string();
                parseJSONWithJSONObject_get(responseData);//调用parseJSONWithJSONObject()方法来解析数据
                String message1 = MyMessageTools.unicodeToUtf8(MyMessageTools.getTitleFromResponse(responseData));
                String message2 = MyMessageTools.unicodeToUtf8(MyMessageTools.getContentFromResponse(responseData));
                String[] leftEntity =new String[triples.getSize()];
                triples.getLeftEntity().toArray(leftEntity);
                String[] rightEntity =new String[triples.getSize()];
                triples.getRightEntity().toArray(rightEntity);
                String[] relationId =new String[triples.getSize()];
                triples.getRelationId().toArray(relationId);
                showResponseRelationTriples(message1,message2,leftEntity,rightEntity,relationId);
            }
        });

    }
    private void showResponseRelationTriples(final String response_title, final String response_content, final String[] response_leftEntity, final String[] response_rightEntity, final String[] response_relationId) {
        //在子线程中更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                SpannableStringBuilder spannable = new SpannableStringBuilder(response_content);
                for(int i = 0; i< triples.getSize(); i++){
                    //设置文字的前景色
                    spannable.setSpan(new ForegroundColorSpan(Color.BLUE),(int) triples.getLeftStart().get(i),(int) triples.getLeftEnd().get(i),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new ForegroundColorSpan(Color.RED),(int) triples.getRightStart().get(i),(int) triples.getRightEnd().get(i),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                responseTitle.setText(response_title);
                responseText.setText(spannable);

                TableRow table_title = (TableRow)findViewById(R.id.relation_display_title);
                table_title.setVisibility(View.VISIBLE);
                Button lastpage = (Button) findViewById(R.id.last_page_button);
                lastpage.setVisibility(View.VISIBLE);
                Button nextpage = (Button) findViewById(R.id.next_page_button);
                nextpage.setVisibility(View.VISIBLE);
                Button upload = (Button) findViewById(R.id.upload_button);
                upload.setVisibility(View.VISIBLE);

                //清空上次操作
                int len = tableLayout.getChildCount();
                if (len > 1) { //这里的判断我是为了实现动态更新数据...保留标题
                    //必须从后面减去子元素
                    for (int i = len + 1; i > 0; i--) {
                        tableLayout.removeView(tableLayout.getChildAt(i));
                    }
                }

                for(int i = 0; i < triples.getSize(); i++){
                    TableRow row = new TableRow(getApplicationContext());
                    TableLayout.LayoutParams row_style = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
                    row_style.setMargins(16,16,16,16);
                    row.setLayoutParams(row_style);
                    row.setGravity(View.TEXT_ALIGNMENT_CENTER);

                    TextView left_entity = new TextView(getApplicationContext());
                    MyMessageTools.setRelationViewTextStyle(left_entity,true);
                    left_entity.setTextColor(Color.BLUE);
                    left_entity.setText(response_leftEntity[i]);
                    row.addView(left_entity);

                    TextView right_entity = new TextView(getApplicationContext());
                    MyMessageTools.setRelationViewTextStyle(right_entity,true);
                    right_entity.setTextColor(Color.RED);
                    right_entity.setText(response_rightEntity[i]);
                    row.addView(right_entity);

                    final TextView relation = new TextView(getApplicationContext());
                    MyMessageTools.setRelationViewTextStyle(relation,false);
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

                    //checkbox
                    CheckBox checkbox = new CheckBox(getApplicationContext());
                    checkbox.setChecked(true);
                    row.addView(checkbox);

                    tableLayout.addView(row);
                    final int finalI = i;
                    relation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int temp = (Integer) triples.getChange().get(finalI);
                                    temp++;
                                    triples.getChange().set(finalI,temp);
                                    if(temp%2==0){
                                        relation.setText(MyMessageTools.unicodeToUtf8("任职"));
                                    }
                                    else if(temp%2==1){
                                        relation.setText(MyMessageTools.unicodeToUtf8("亲属"));
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //上传关系标注的函数
    private void processUploadRelationTriplesText(){
        OkHttpClient upload_triples = new OkHttpClient();//用okhttp的网络架构

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输信息
                .add("triples", packJSONWithJSONObject_uploadTriples())
                .add("token",user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_upload_triple")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = upload_triples.newCall(request);
        //setProgressDialog_upload();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //progressDialog.dismiss();
                Log.d("CONNECTION", "请求成功");
                String responseData = response.body().string();
                parseJSONWithJSONObject_get(responseData);//调用parseJSONWithJSONObject()方法来解析数据
                processGetRelationTriplesText();
            }
        });
    }



    //获取命名实体的函数
    private void processGetNameEntityText() {
        OkHttpClient get_entity = new OkHttpClient();//用okhttp的网络架构

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输token
                .add("token", user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_get_entity")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = get_entity.newCall(request);
        //setProgressDialog_get();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //progressDialog.dismiss();

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

                TableRow table_title = (TableRow)findViewById(R.id.relation_display_title);
                table_title.setVisibility(View.INVISIBLE);
                Button lastpage = (Button) findViewById(R.id.last_page_button);
                lastpage.setVisibility(View.VISIBLE);
                Button nextpage = (Button) findViewById(R.id.next_page_button);
                nextpage.setVisibility(View.VISIBLE);
                Button upload = (Button) findViewById(R.id.upload_button);
                upload.setVisibility(View.VISIBLE);

                //清空上次操作
                int len = tableLayout.getChildCount();
                if (len > 1) { //这里的判断我是为了实现动态更新数据...保留标题
                    //必须从后面减去子元素
                    for (int i = len + 1; i > 0; i--) {
                        tableLayout.removeView(tableLayout.getChildAt(i));
                    }
                }
            }
        });
    }
    //上传实体标注的函数
    private void processUploadNameEntityText(){
        OkHttpClient upload_entities = new OkHttpClient();//用okhttp的网络架构

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输信息
                //.add("doc_id")
                .add("entities", packJSONWithJSONObject_uploadEntity())
                .add("token",user.getToken())
                .build();

        Request request = new Request.Builder()
                .url("http://10.15.82.223:9090/app_get_data/app_upload_entity")//指定访问的服务器地址
                .post(postBody)
                .build();

        Call call = upload_entities.newCall(request);
        //setProgressDialog_upload();

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CONNECTION", "请求失败 !!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //progressDialog.dismiss();
                Log.d("CONNECTION", "请求成功");
                String responseData = response.body().string();
                parseJSONWithJSONObject_get(responseData);//调用parseJSONWithJSONObject()方法来解析数据
            }
        });
    }


    //用户登出操作的函数
    private void processSignOut() {
        OkHttpClient sign_out = new OkHttpClient();//用okhttp的网络架构

        RequestBody postBody = new FormBody.Builder()//用formbody的形式向服务器传输
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
            triples.clearAllArrayList();
            if(jsonObject.has("msg")){
                String message = jsonObject.getString("msg");
                final String msg = MyMessageTools.unicodeToUtf8(message);//对数据进行Unicode转码为中文字符
                Log.d("msg", msg);//打印传输回来的消息
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainMenuActivity.this,msg,Toast.LENGTH_LONG ).show();
                    }
                });
                if(msg.equals("尚未登录")||msg.equals("登出成功")){
                    Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else{//如果成功
                if(jsonObject.has("triples")){
                    triples.setDoc_id(jsonObject.getString("doc_id"));
                    triples.setSent_id(jsonObject.getString("sent_id"));
                    triples.setTitle(jsonObject.getString("title"));
                    triples.setSent_ctx(jsonObject.getString("sent_ctx"));
                    triples.setTriplesGroup(jsonObject.getString("triples"));

                    JSONArray triplesJSArray = jsonObject.getJSONArray("triples");
                    for(int i=0;i<triplesJSArray.length();i++){
                        JSONObject triplesJSON = triplesJSArray.getJSONObject(i);
                        triples.getSub_id().add(triplesJSON.getString("id"));
                        triples.getLeftStart().add(triplesJSON.getInt("left_e_start"));
                        triples.getLeftEnd().add(triplesJSON.getInt("left_e_end"));
                        triples.getRightStart().add(triplesJSON.getInt("right_e_start"));
                        triples.getRightEnd().add(triplesJSON.getInt("right_e_end"));
                        triples.getRelationStart().add(triplesJSON.getInt("relation_start"));
                        triples.getRelationEnd().add(triplesJSON.getInt("relation_end"));
                        triples.getLeftEntity().add(triplesJSON.getString("left_entity"));
                        triples.getRightEntity().add(triplesJSON.getString("right_entity"));
                        triples.getRelationId().add(triplesJSON.getString("relation_id"));
                        triples.getChange().add(triplesJSON.getInt("relation_id"));
                        triples.getStatus().add(1);
                    }//存储四组左右起始点信息，为文本高亮做准备

                    Log.d("doc_id",triples.getDoc_id());
                    Log.d("sent_id",triples.getSent_id());
                    Log.d("title",triples.getTitle());
                    Log.d("sent_ctx",triples.getSent_ctx());
                    Log.d("triples",triples.getTriplesGroup());

                }
                else{
                    String title = jsonObject.getString("title");
                    String content = jsonObject.getString("content");
                    Entities.setSent_id(jsonObject.getString("sent_id"));
                    Entities.setDoc_id(jsonObject.getString("doc_id"));
                    Log.d("title",title);
                    Log.d("content",content);
                    Log.d("sent_id",Entities.getSent_id());
                    Log.d("doc_id",Entities.getDoc_id());
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //封装triples的JSON数据的函数
    private String packJSONWithJSONObject_uploadTriples(){
        JSONObject jsonToString = new JSONObject();
        try {
            jsonToString.put("doc_id",triples.getDoc_id());
            jsonToString.put("sent_id",triples.getSent_id());
            jsonToString.put("title",triples.getTitle());
            jsonToString.put("sent_ctx",triples.getSent_ctx());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonarray   = new JSONArray();
        for(int i = 0 ; i < triples.getSize() ;i++){     //封装5个json，构成一个数组
            JSONObject jsonobject = new JSONObject();
            try {
                jsonobject.put("id", String.valueOf(triples.getSub_id().get(i)));
                jsonobject.put("left_e_start", String.valueOf(triples.getLeftStart().get(i)));
                jsonobject.put("left_e_end", String.valueOf(triples.getLeftEnd().get(i)));
                jsonobject.put("right_e_start", String.valueOf(triples.getRightStart().get(i)));
                jsonobject.put("right_e_end", String.valueOf(triples.getRightEnd().get(i)));
                jsonobject.put("relation_start", String.valueOf(triples.getRelationStart().get(i)));
                jsonobject.put("relation_end", String.valueOf(triples.getRelationEnd().get(i)));
                jsonobject.put("left_entity", String.valueOf(triples.getLeftEntity().get(i)));
                jsonobject.put("right_entity", String.valueOf(triples.getRightEntity().get(i)));
                jsonobject.put("relation_id", String.valueOf(triples.getRelationId().get(i)));
                int cur = (Integer)triples.getChange().get(i);
                int init = Integer.valueOf(String.valueOf(triples.getRelationId().get(i)));
                if(cur%2 != init){
                    triples.getStatus().set(i,-1);
                }
                jsonobject.put("status", String.valueOf(triples.getStatus().get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonarray.put(jsonobject);
        }
        try {
            jsonToString.put("triples", jsonarray);     //给整体json前加上一个key值
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("UPLOAD_TRIPLES", jsonToString.toString());
        SharedPreferences sp = getSharedPreferences("upload_triples", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("triples", jsonToString.toString());//对上传的triples进行存储
        editor.apply();
        return  jsonToString.toString();
    }

    //封装entities的JSON数据的函数
    private String packJSONWithJSONObject_uploadEntity(){
        JSONObject jsonToString = new JSONObject();
        try {
            jsonToString.put("doc_id",Entities.getDoc_id());
            jsonToString.put("sent_id",Entities.getSent_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonarray = new JSONArray();
        for(int i = 0 ; i < Entities.getSize(); i++){
            JSONObject jsonobject = new JSONObject();
            try {
                jsonobject.put("EntityName", String.valueOf(Entities.getEntityName().get(i)));
                jsonobject.put("Start", String.valueOf(Entities.getStart().get(i)));
                jsonobject.put("End", String.valueOf(Entities.getEnd().get(i)));
                jsonobject.put("NerTag", String.valueOf(Entities.getNerTag().get(i)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonarray.put(jsonobject);
        }
        try {
            jsonToString.put("entities", jsonarray);     //给整体json前加上一个key值
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("UPLOAD_ENTITIES", jsonToString.toString());
        SharedPreferences sp = getSharedPreferences("upload_entities", MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("entities", jsonToString.toString());//对上传的entities进行存储
        editor.apply();
        return  jsonToString.toString();
    }

    //输出加载框的函数
    /*private void setProgressDialog_get(){
        progressDialog = new ProgressDialog(MainMenuActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Getting the data...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //上传数据时输出加载框的函数
    private void setProgressDialog_upload(){
        progressDialog = new ProgressDialog(MainMenuActivity.this) ;
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading the data...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }*/

}

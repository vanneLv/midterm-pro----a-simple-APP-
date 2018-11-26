package com.example.lvmufan.myapplication;

import android.app.ProgressDialog;

import org.json.JSONObject;

class MyMessageTools {

    //转中文编码
    static String unicodeToUtf8(String theString) {
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

    //获取消息中的内容
    static String getContentFromResponse(String responseData){
        try{
            JSONObject jsonObject = new JSONObject(responseData);
            //实体获取
            if(jsonObject.has("content")){
                return jsonObject.getString("content");
            }
            //关系标注获取
            else if(jsonObject.has("sent_ctx")){
                return jsonObject.getString("sent_ctx");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseData;
    }

    //获取消息中的title
    static String getTitleFromResponse(String responseData){
        try{
            JSONObject jsonObject = new JSONObject(responseData);
            //实体获取
            if(jsonObject.has("title")){
                return jsonObject.getString("title");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseData;
    }



}

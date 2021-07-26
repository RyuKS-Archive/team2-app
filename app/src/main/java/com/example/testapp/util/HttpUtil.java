package com.example.testapp.util;
import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public String request (String _url, ContentValues params) {
        HttpURLConnection conn = null;
        StringBuffer sbParams = new StringBuffer();

        if (params == null) {
            sbParams.append("");
        } else {
            boolean isAnd = false;
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if(isAnd) {
                    sbParams.append("&");
                }

                sbParams.append(key).append("=").append(value);

                if (!isAnd) {
                    if (params.size() >= 2) {
                        isAnd = true;
                    }
                }
            }
        }

        try {
            URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            String strParams = sbParams.toString();
            OutputStream os = conn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line;
            String result = "";

            while ((line = reader.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return null;
    }

    public ContentValues openstack_getAuthToken() {
        String OS_USERNAME = "admin";
        String OS_PASSWORD = "qhrwl4857!";
        String OS_PROJECT_NAME = "admin";
        String OS_USER_DOMAIN_NAME = "Default";
        String OS_PROJECT_DOMAIN_NAME = "Default";
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/auth/tokens?nocatalog";
        String OS_TOKEN = "";

        HttpURLConnection conn = null;
        ContentValues response = null;

        JSONObject responseDate = null;
        JSONObject jsonObject = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject identity = new JSONObject();
        JSONObject password = new JSONObject();
        JSONObject user = new JSONObject();
        JSONObject domain_idnetity = new JSONObject();
        JSONObject scope = new JSONObject();
        JSONObject project = new JSONObject();
        JSONObject domain_scope =new JSONObject();
        JSONArray passwordArr =new JSONArray();


        Log.e("httputl", "getAuthToken START!");
        try{
            domain_idnetity.put("name",OS_USER_DOMAIN_NAME);

            user.put("domain", domain_idnetity);
            user.put("name",OS_USERNAME);
            user.put("password",OS_PASSWORD);

            password.put("user", user);

            //체크
            passwordArr.put("password");
            identity.put("methods", passwordArr);
            //identity.put("methods", "password");

            identity.put("password", password);

            domain_scope.put("name",OS_PROJECT_DOMAIN_NAME);
            project.put("domain", domain_scope);
            project.put("name",OS_PROJECT_NAME);

            scope.put("project", project);

            auth.put("identity", identity);
            auth.put("scope", scope);

            jsonObject.put("auth", auth);

        }catch(JSONException e) {
            e.printStackTrace();
        }
        /* get auth json example
        '{ "auth": {
                "identity": {
                    "methods": ["password"]
                    "password": {
                        "user": {
                            "domain": { "name": "'"$OS_USER_DOMAIN_NAME"'"}
                            "name": "'"$OS_USERNAME"'",
                            "password": "'"$OS_PASSWORD"'"
                        }
                    }
                }
                "scope": {
                    "project": {
                        "domain": { "name": "'"$OS_PROJECT_DOMAIN_NAME"'" }
                        "name":  "'"$OS_PROJECT_NAME"'"
                        }
                }
            }
        }
        */

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");

            String strJson = jsonObject.toString();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                String code = Integer.toString(conn.getResponseCode());
                Log.e("httputil", "Response Code : " + code);

                return null;
            }

//authToken추출
            Map<String, List<String>> responseHeader = conn.getHeaderFields();
            Iterator<String> headerIt = responseHeader.keySet().iterator();
            Map<String, String> headerResult = new LinkedHashMap<String, String>();

            if(headerIt != null) {
                Log.e("httputil","--------header start--------");
                while(headerIt.hasNext()) {
                    String headerKey = (String) headerIt.next();
                    if(responseHeader.get(headerKey) != null) {
                        Log.e("httputil", headerKey +":"+ responseHeader.get(headerKey).get(0));
                        headerResult.put(headerKey, responseHeader.get(headerKey).get(0));

                        if("X-Subject-Token".equals(headerKey)) {
                            OS_TOKEN = responseHeader.get(headerKey).get(0);
                        }
                    }
                }
                Log.e("httputil","--------header end--------");
            }

            try{
                Log.e("httputil","--------json parse--------");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONObject resJson = new JSONObject(result.toString());

                Log.e("httputil","result : " + result.toString());
                JSONObject token = resJson.getJSONObject("token");
                String expires_at = token.getString("expires_at");

                Log.e("httputil","expires_at : "+ expires_at);

                response = new ContentValues();
                response.put("authToken", OS_TOKEN);
                response.put("expires_at", expires_at);

            }catch(JSONException e) {
                e.printStackTrace();
            }

            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }

        return null;
    }

    public ContentValues openstack_selectUser(String authToken) {
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/users";
        String OS_TOKEN = authToken;

        HttpURLConnection conn = null;
        ContentValues response = null;

        JSONObject responseDate = null;

        Log.e("httputl", "select user START!");

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);

            Log.e("httputil","OS_TOKEN : " + OS_TOKEN);

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String code = Integer.toString(conn.getResponseCode());
                Log.e("httputil", "Response Code : " + code);

                return null;
            }

            try{
                Log.e("httputil","--------json parse--------");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONObject resJson = new JSONObject(result.toString());

                Log.e("httputil","result : " + result.toString());
                /*
                JSONObject token = resJson.getJSONObject("users");
                String expires_at = token.getString("expires_at");

                Log.e("httputil","expires_at : "+ expires_at);

                response = new ContentValues();
                response.put("authToken", OS_TOKEN);
                response.put("expires_at", expires_at);
                */
            }catch(JSONException e) {
                e.printStackTrace();
            }

            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }

        return null;
    }

    public ContentValues openstack_createUser(String authToken) {
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/users";
        String OS_TOKEN = authToken;

        HttpURLConnection conn = null;
        ContentValues response = null;

        JSONObject responseDate = null;
        JSONObject jsonObject = new JSONObject();
        JSONObject user = new JSONObject();
        //JSONObject federated = new JSONObject();
        //JSONObject protocols = new JSONObject();
        //JSONObject options = new JSONObject();
        //JSONArray protocolsAddr =new JSONArray();

        Log.e("httputl", "create user START!");
        try {
            user.put("enabled", true);
            user.put("name", "test20");
            user.put("password", "123qwe");
            user.put("email", "dynamite_heaven@naver.com");

            jsonObject.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* create user example
        {
            "user": {
                "default_project_id": "263fd9",
                "domain_id": "1789d1",
                "enabled": true,
                "federated": [
                    {
                        "idp_id": "efbab5a6acad4d108fec6c63d9609d83",
                        "protocols": [
                            {
                                "protocol_id": "mapped",
                                "unique_id": "test@example.com"
                            }
                        ]
                    }
                ],
                "name": "James Doe",
                "password": "secretsecret",
                "description": "James Doe user",
                "email": "jdoe@example.com",
                "options": {
                    "ignore_password_expiry": true
                }
            }
        }
        */

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);
            Log.e("httputil","OS_TOKEN : "+ OS_TOKEN);


            String strJson = jsonObject.toString();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                String code = Integer.toString(conn.getResponseCode());
                Log.e("httputil", "Response Code : " + code);

                return null;
            }

            try{
                Log.e("httputil","--------json parse--------");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONObject resJson = new JSONObject(result.toString());

                Log.e("httputil","result : " + result.toString());

                /*
                JSONObject token = resJson.getJSONObject("token");
                String expires_at = token.getString("expires_at");

                Log.e("httputil","expires_at : "+ expires_at);

                response = new ContentValues();
                response.put("authToken", OS_TOKEN);
                response.put("expires_at", expires_at);
                */
            }catch(JSONException e) {
                e.printStackTrace();
            }

            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }

        return null;
    }
}
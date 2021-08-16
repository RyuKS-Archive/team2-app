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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private String pageUrl = "";
    private String httpMethod;
    private final int HTTP_ERR = 400;
    private final String project_id = "9d7d000a324f4de5908c079117f8da76";
    private final String role_id = "f6a1d237267c467c898938796aa0b16f";

    public HttpUtil() {
        this.httpMethod = "POST";
    }

    public HttpUtil(String pageUrl, String httpMethod) {
        this.pageUrl = pageUrl;
        this.httpMethod = httpMethod;
    }

    public void setUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public ContentValues request (ContentValues params) {
        HttpURLConnection conn = null;
        StringBuffer sbParams = new StringBuffer();
        //JSONObject sbParams = new JSONObject();
        int responseCode = 0;

        boolean isOutput = params != null ? true : false;

        if (isOutput) {
            boolean isFirst = true;
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : params.valueSet()) {

                key = parameter.getKey();
                value = parameter.getValue().toString();

                Log.e("HttpUtil", "key : " + key + " vlaue : " + value);

                /*
                try {
                    sbParams.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                 */

                if (isFirst)
                    isFirst = false;
                else
                    sbParams.append("&");

                try {
                    sbParams.append(URLEncoder.encode(key, "UTF-8"));
                    sbParams.append("=");
                    sbParams.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        } else {
            sbParams.append("");
            //sbParams = null;
        }

        Log.e("HttpUtil", "request start pageUrl : " + pageUrl + " httpMethod : " + httpMethod);
        try {
            URL url = new URL(pageUrl);
            conn = (HttpURLConnection) url.openConnection();

            //set Header
            conn.setRequestMethod(httpMethod);
            //conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Content_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
            conn.setDoOutput(isOutput);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(6000);

            if (isOutput) {
                Log.e("HttpUtil", "request params : " + sbParams.toString());

                String strParams = sbParams.toString();
                OutputStream os = conn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            BufferedReader reader = null;
            responseCode = conn.getResponseCode();
            Log.e("HttpUtil", "response code : " + responseCode);
            //if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (responseCode >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.e("httputil", result.toString());

            ContentValues response = new ContentValues();
            response.put("code", responseCode);
            response.put("result", result.toString());

            return response;
            //return result.toString();
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

    public ContentValues openstack_getAuthToken(ContentValues values) {
        if(values == null) {
            return null;
        }

        boolean IS_SCOPE = values.getAsBoolean("IS_SCOPE");
        String OS_USERNAME = values.getAsString("OS_USERNAME");
        String OS_PASSWORD = values.getAsString("OS_PASSWORD");
        String OS_PROJECT_NAME = values.getAsString("OS_PROJECT_NAME");
        String OS_USER_DOMAIN_NAME = values.getAsString("OS_USER_DOMAIN_NAME");
        String OS_PROJECT_DOMAIN_NAME = values.getAsString("OS_PROJECT_DOMAIN_NAME");
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/auth/tokens?nocatalog";
        String OS_TOKEN = "";

        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        JSONObject responseDate = null;
        JSONObject jsonObject = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject identity = new JSONObject();
        JSONObject password = new JSONObject();
        JSONObject user = new JSONObject();
        JSONObject domain_idnetity = new JSONObject();
        JSONObject scope = new JSONObject();
        JSONObject project = new JSONObject();
        JSONObject domain_scope = new JSONObject();
        JSONArray passwordArr = new JSONArray();

        Log.e("httputl", "getAuthToken START!");

        if (IS_SCOPE) {
            try{
                domain_idnetity.put("name",OS_USER_DOMAIN_NAME);

                user.put("domain", domain_idnetity);
                user.put("name",OS_USERNAME);
                user.put("password",OS_PASSWORD);

                password.put("user", user);

                passwordArr.put("password");
                identity.put("methods", passwordArr);

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
            {
                "auth": {
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
        } else {
            try{
                domain_idnetity.put("name",OS_USER_DOMAIN_NAME);

                user.put("domain", domain_idnetity);
                user.put("name",OS_USERNAME);
                user.put("password",OS_PASSWORD);

                passwordArr.put("password");
                identity.put("methods", passwordArr);

                password.put("user", user);
                identity.put("password", password);

                auth.put("identity", identity);

                jsonObject.put("auth", auth);

            }catch(JSONException e) {
                e.printStackTrace();
            }

            /*
            {
                "auth": {
                    "identity": {
                        "methods": [
                            "password"
                        ],
                        "password": {
                            "user": {
                                "name": "admin",
                                "domain": {
                                    "name": "Default"
                                },
                                "password": "devstacker"
                            }
                        }
                    }
                }
            }
            */
        }

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setConnectTimeout(10000);

            String strJson = jsonObject.toString();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

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

            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());

                    JSONObject token = resJson.getJSONObject("token");
                    String expires_at = token.getString("expires_at");

                    response = new ContentValues();
                    response.put("authToken", OS_TOKEN);
                    response.put("expires_at", expires_at);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public ContentValues openstack_getAuthScopeToken(String OS_TOKEN) {
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/auth/tokens?nocatalog";

        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        JSONObject jsonObject = new JSONObject();
        JSONObject auth = new JSONObject();
        JSONObject identity = new JSONObject();
        JSONObject token = new JSONObject();
        JSONObject scope = new JSONObject();
        JSONObject project = new JSONObject();
        JSONArray methodsArr = new JSONArray();

        Log.e("httputl", "getAuthToken START!");

        try{
            methodsArr.put("token");
            identity.put("methods", methodsArr);

            token.put("id", OS_TOKEN);
            identity.put("token", token);

            project.put("id", project_id);
            scope.put("project", project);

            auth.put("identity", identity);
            auth.put("scope", scope);

            jsonObject.put("auth", auth);

        }catch(JSONException e) {
            e.printStackTrace();
        }

        //{
        //    "auth": {
        //        "identity": {
        //              "methods": [
        //                  "token"
        //              ],
        //              "token": {
        //                  "id": "'$OS_TOKEN'"
        //              }
        //          },
        //          "scope": {
        //              "project": {
        //                  "id": "a6944d763bf64ee6a275f1263fae0352"
        //              }
        //          }
        //      }
        // }

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setConnectTimeout(10000);

            String strJson = jsonObject.toString();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

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

            //if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());

                    JSONObject restoken = resJson.getJSONObject("token");
                    String expires_at = restoken.getString("expires_at");

                    //response = new ContentValues();
                    response.put("authToken", OS_TOKEN);
                    response.put("expires_at", expires_at);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public ContentValues openstack_CreateUser(ContentValues values) {
        if(values == null) {
            return null;
        }

        //String default_project_id = values.get("default_project_id").toString();
        String domain_id = values.getAsString("domain_id");
        boolean enabled = values.getAsBoolean("enabled");
        //String protocol_id = values.get("protocol_id").toString();
        //String unique_id = values.get("unique_id").toString();
        //String idp_id = values.get("idp_id").toString();
        String name = values.getAsString("name");
        String password = values.getAsString("password");
        String description = values.getAsString("description");
        String email = values.getAsString("email");
        //boolean ignore_password_expiry = (boolean) values.get("ignore_password_expiry");
        String OS_TOKEN = values.getAsString("OS_TOKEN");
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/users";


        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        JSONObject responseData = null;
        JSONObject jsonObject = new JSONObject();
        JSONObject user = new JSONObject();
        JSONObject federated = new JSONObject();
        JSONObject protocols = new JSONObject();
        JSONObject options = new JSONObject();
        JSONArray protocolsAddr = new JSONArray();
        JSONArray federatedAddr = new JSONArray();

        Log.e("httputl", "create user START!");
        try {
            //user.put("default_project_id", default_project_id);
            user.put("domain_id", domain_id);
            user.put("enabled", enabled);

            //protocols.put("protocol_id", protocol_id);
            //protocols.put("unique_id", unique_id);
            //protocolsAddr.put(protocols);

            //federated.put("idp_id", idp_id);
            //federated.put("protocols", protocolsAddr);
            //federatedAddr.put(federated);

            //user.put("federated", federatedAddr);
            user.put("name", name);
            user.put("password", password);
            user.put("description", description);
            user.put("email", email);

            //options.put("ignore_password_expiry", ignore_password_expiry);
            //user.put("options", options);

            jsonObject.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* create user example
        {
            "user": {
                //"default_project_id": "263fd9",
                "domain_id": "1789d1",
                "enabled": true,
                //"federated": [
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
            conn.setConnectTimeout(10000);
            Log.e("httputil","OS_TOKEN : "+ OS_TOKEN);

            String strJson = jsonObject.toString();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

            //if(response_code != HttpURLConnection.HTTP_CREATED) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());

                    JSONObject token = resJson.getJSONObject("user");
                    //String p_id = token.getString("default_project_id") == null? "" : token.getString("default_project_id");
                    String d_id = token.getString("domain_id");
                    String u_id = token.getString("id");

                    //response.put("response_code", response_code);
                    //response.put("project_id", p_id);
                    response.put("domain_id", d_id);
                    response.put("user_id", u_id);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public HashMap openstack_ServerList(String authToken) {
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/servers/detail";
        String OS_TOKEN = authToken;

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        HashMap response = null;

        Log.e("httputl", "server list START!");

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token", OS_TOKEN);
            conn.setConnectTimeout(10000);

            Log.e("httputil","OS_TOKEN : " + OS_TOKEN);

            int response_code = conn.getResponseCode();
            response = new HashMap();
            response.put("response_code", response_code);

            Log.e("httputil", "Response Code : " + response_code);

            //if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());
                    JSONArray resServerArr = resJson.getJSONArray("servers");
                    ArrayList<ContentValues> serverList = new ArrayList<>();

                    for (int idx = 0 ; idx < resServerArr.length() ; idx++) {
                        JSONObject server = resServerArr.getJSONObject(idx);
                        ContentValues tmpValue = new ContentValues();

                        tmpValue.put("id", server.getString("id"));
                        tmpValue.put("name", server.getString("name"));
                        tmpValue.put("status", server.getString("status"));
                        tmpValue.put("hostId", server.getString("hostId"));
                        tmpValue.put("tenant_id", server.getString("tenant_id"));
                        tmpValue.put("updated", server.getString("updated"));
                        tmpValue.put("created", server.getString("created"));

                        serverList.add(tmpValue);
                    }

                    response.put("server_list", serverList);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public ContentValues openstack_UsageStatistics(ContentValues values) {
        if(values == null) {
            return null;
        }

        String OS_TOKEN = values.getAsString("OS_TOKEN");
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/os-simple-tenant-usage/" + values.getAsString("tenantId");

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        ContentValues response = null;

        Log.e("httputl", "Usage Statistics START!");

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token", OS_TOKEN);
            conn.setConnectTimeout(10000);

            Log.e("httputil","OS_AUTH_URL : " + OS_AUTH_URL);

            int response_code = conn.getResponseCode();
            response = new ContentValues();
            response.put("response_code", response_code);

            Log.e("httputil", "Response Code : " + response_code);

            //if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());
                    JSONObject tenant_usage = resJson.getJSONObject("tenant_usage");

                    response.put("start", tenant_usage.getString("start"));
                    response.put("stop", tenant_usage.getString("stop"));
                    response.put("total_hours", tenant_usage.getString("total_hours"));
                    response.put("total_local_gb_usage", tenant_usage.getString("total_local_gb_usage"));
                    response.put("total_memory_mb_usage", tenant_usage.getString("total_memory_mb_usage"));
                    response.put("total_vcpus_usage", tenant_usage.getString("total_vcpus_usage"));

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public HashMap openstack_FlavorList(String authToken) {
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/flavors";
        String OS_TOKEN = authToken;

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        HashMap response = null;

        Log.e("httputl", "FLAVOR LIST START!");

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token", OS_TOKEN);
            conn.setConnectTimeout(10000);

            Log.e("httputil","OS_TOKEN : " + OS_TOKEN);

            int response_code = conn.getResponseCode();
            response = new HashMap();
            response.put("response_code", response_code);

            Log.e("httputil", "Response Code : " + response_code);

            //if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());
                    JSONArray resServerArr = resJson.getJSONArray("flavors");
                    ArrayList<ContentValues> flavorList = new ArrayList<>();

                    for (int idx = 0 ; idx < resServerArr.length() ; idx++) {
                        JSONObject flavor = resServerArr.getJSONObject(idx);
                        ContentValues tmpValue = new ContentValues();

                        tmpValue.put("id", flavor.getString("id"));
                        tmpValue.put("name", flavor.getString("name"));

                        flavorList.add(tmpValue);
                    }

                    response.put("flavor_list", flavorList);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public HashMap openstack_ImageList(String authToken) {
        String OS_AUTH_URL = "http://210.216.61.151:12092/v2/images";
        String OS_TOKEN = authToken;

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        HashMap response = null;

        Log.e("httputl", "FLAVOR LIST START!");

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token", OS_TOKEN);
            conn.setConnectTimeout(10000);

            Log.e("httputil","OS_TOKEN : " + OS_TOKEN);

            int response_code = conn.getResponseCode();
            response = new HashMap();
            response.put("response_code", response_code);

            Log.e("httputil", "Response Code : " + response_code);

            //if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.e("httputil","result : " + result.toString());

                try{
                    JSONObject resJson = new JSONObject(result.toString());
                    JSONArray resServerArr = resJson.getJSONArray("images");
                    ArrayList<ContentValues> imageList = new ArrayList<>();

                    for (int idx = 0 ; idx < resServerArr.length() ; idx++) {
                        JSONObject flavor = resServerArr.getJSONObject(idx);
                        ContentValues tmpValue = new ContentValues();

                        tmpValue.put("id", flavor.getString("id"));
                        tmpValue.put("name", flavor.getString("name"));
                        tmpValue.put("status", flavor.getString("status"));

                        imageList.add(tmpValue);
                    }

                    response.put("image_list", imageList);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
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

    public ContentValues openstack_CreateServer(ContentValues values) {
        if(values == null) {
            return null;
        }

        String OS_TOKEN = values.getAsString("OS_TOKEN");
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/servers";

        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        JSONGenerator jsonGenerator = new JSONGenerator();

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);
            conn.setConnectTimeout(10000);
            Log.e("httputil","OS_TOKEN : "+ OS_TOKEN);

            String strJson = jsonGenerator.os_CreateServer(values);
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

            //if(response_code != HttpURLConnection.HTTP_ACCEPTED) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.e("httputil","result : " + result.toString());

            try{
                JSONObject resJson = new JSONObject(result.toString());
                JSONObject resServer = resJson.getJSONObject("server");
                //String diskConfig = resServer.getString("OS-DCF:diskConfig");
                String id = resServer.getString("id");
                String adminPass = resServer.getString("adminPass");

                response.put("id", id);
                response.put("adminPass", adminPass);

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

    public ContentValues openstack_StartServer(ContentValues values) {
        if(values == null) {
            return null;
        }

        String OS_TOKEN = values.getAsString("OS_TOKEN");
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/servers/" + values.getAsString("id") + "/action";

        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        JSONGenerator jsonGenerator = new JSONGenerator();

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);
            conn.setConnectTimeout(20000);
            Log.e("httputil","OS_AUTH_URL : "+ OS_AUTH_URL);

            String strJson = jsonGenerator.os_StartServer();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

            //if(response_code != HttpURLConnection.HTTP_ACCEPTED) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.e("httputil","result : " + result.toString());

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

    public ContentValues openstack_StopServer(ContentValues values) {
        if(values == null) {
            return null;
        }

        String OS_TOKEN = values.getAsString("OS_TOKEN");
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/servers/" + values.getAsString("id") + "/action";

        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        JSONGenerator jsonGenerator = new JSONGenerator();

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);
            conn.setConnectTimeout(20000);
            Log.e("httputil","OS_AUTH_URL : "+ OS_AUTH_URL);

            String strJson = jsonGenerator.os_StopServer();
            Log.e("httputil","json : "+ strJson);

            OutputStream os = conn.getOutputStream();
            os.write(strJson.getBytes("UTF-8"));
            os.flush();
            os.close();

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

            //if(response_code != HttpURLConnection.HTTP_ACCEPTED) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.e("httputil","result : " + result.toString());

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

    public ContentValues openstack_DeleteServer(ContentValues values) {
        if(values == null) {
            return null;
        }

        String OS_TOKEN = values.getAsString("OS_TOKEN");
        String OS_AUTH_URL = "http://210.216.61.151:12874/v2.1/servers/" + values.getAsString("id");

        HttpURLConnection conn = null;
        ContentValues response = null;
        BufferedReader reader = null;

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);
            conn.setConnectTimeout(10000);
            Log.e("httputil","OS_AUTH_URL : "+ OS_AUTH_URL);

            response = new ContentValues();

            int response_code = conn.getResponseCode();
            response.put("response_code", response_code);
            Log.e("httputil", "Response Code : " + response_code);

            //if(response_code != HttpURLConnection.HTTP_NO_CONTENT) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.e("httputil","result : " + result.toString());

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

    public String openstack_AddRole(ContentValues values) {
        if(values == null) {
            return null;
        }

        /*
        /v3/projects/{project_id}/users/{user_id}/roles/{role_id}
         */
        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/projects/" + project_id + "/users/" + values.getAsString("user_id") + "/roles/" + role_id;
        String OS_TOKEN = values.get("OS_TOKEN").toString();
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        Log.e("httputl", "Add Role START!");

        try{
            URL url = new URL(OS_AUTH_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Auth-Token",OS_TOKEN);
            conn.setConnectTimeout(10000);

            //Log.e("httputil","OS_TOKEN : "+ OS_TOKEN);
            Log.e("httputil","url : "+ OS_AUTH_URL);

            int response_code = conn.getResponseCode();
            Log.e("httputil", "Response Code : " + response_code);

            //if(response_code != HttpURLConnection.HTTP_NO_CONTENT) {
            if (response_code >= HTTP_ERR) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.e("httputil","result : " + result.toString());

            return Integer.toString(response_code);
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

    /*
    public ContentValues openstack_createRole(ContentValues values) {

        if(values == null) {
            return null;
        }

        String OS_AUTH_URL = "http://210.216.61.151:12050/v3/roles";
        String OS_TOKEN = values.get("OS_TOKEN").toString();
        //String domain_id = values.get("domain_id").toString();
        String name = values.get("name").toString();
        String description = values.get("description").toString();

        HttpURLConnection conn = null;
        ContentValues response = null;

        JSONObject responseData = null;
        JSONObject jsonObject = new JSONObject();
        JSONObject role = new JSONObject();

        Log.e("httputl", "create role START!");
        try {
            //role.put("domain_id", domain_id);
            role.put("name", name);
            role.put("description", description);

            jsonObject.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //{
        //    "role": {
        //        "description": "My new role"
        //        "domain_id": "92e782c4988642d783a95f4a87c3fdd7",
        //        "name": "developer"
        //    }
        //}

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
     */

    /*
    public ContentValues openstack_getCreateUserToken(ContentValues values) {
        if(values == null) {
            return null;
        }

        String OS_USER_DOMAIN_NAME = values.get("OS_USER_DOMAIN_NAME").toString();
        String OS_USERNAME = values.get("OS_USERNAME").toString();
        String OS_PASSWORD = values.get("OS_PASSWORD").toString();
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
        JSONObject domain_scope = new JSONObject();
        JSONArray passwordArr = new JSONArray();


        Log.e("httputl", "getCreateUserToken START!");
        try{
            domain_idnetity.put("name",OS_USER_DOMAIN_NAME);

            user.put("domain", domain_idnetity);
            user.put("name",OS_USERNAME);
            user.put("password",OS_PASSWORD);

            password.put("user", user);

            passwordArr.put("password");
            identity.put("methods", passwordArr);
            identity.put("password", password);
            auth.put("identity", identity);

            jsonObject.put("auth", auth);

        }catch(JSONException e) {
            e.printStackTrace();
        }


        //{ "auth": {
        //        "identity": {
        //            "methods": ["password"]
        //            "password": {
        //                "user": {
        //                    "domain": { "name": "'"$OS_USER_DOMAIN_NAME"'"}
        //                    "name": "'"$OS_USERNAME"'",
        //                    "password": "'"$OS_PASSWORD"'"
        //                }
        //            }
        //        }
        //    }
        //}
        //        "scope": {
        //            "project": {
        //                "domain": { "name": "'"$OS_PROJECT_DOMAIN_NAME"'" }
        //                "name":  "'"$OS_PROJECT_NAME"'"
        //                }
        //        }
        //    }
        //}

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
     */

    /*
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
     */

}
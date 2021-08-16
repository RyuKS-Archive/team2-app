package com.example.testapp.util;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONGenerator {

    /* create server
        {
            "server" : {
                "name" : "new-server-test",
                "imageRef" : "61ffc40a-7028-4163-bcb6-188672deaf4e",
                "flavorRef" : "1",
                "security_groups": [
                    {
                        "name": "default"
                    }
                ],
                "networks": [
                    {
                        "uuid": "66778ebf-f406-433d-a63b-1f9ac331b449"
                    }
                ]
            }
        }
     */
    public String os_CreateServer(ContentValues values) {
        JSONObject jsonObject = new JSONObject();
        JSONObject server = new JSONObject();
        JSONObject security_groups = new JSONObject();
        JSONObject networks = new JSONObject();
        JSONArray security_groups_Arr = new JSONArray();

        JSONArray network_Arr = new JSONArray();

        Log.e("httputl", "create server START!");

        try {
            server.put("name", values.getAsString("instanceName"));
            //server.put("imageRef", "61ffc40a-7028-4163-bcb6-188672deaf4e");
            server.put("imageRef", "89d601e6-1a32-4ad4-a546-d4d79d4d4156");
            server.put("flavorRef", "0");

            security_groups.put("name", "default");
            security_groups_Arr.put(security_groups);

            server.put("security_groups", security_groups_Arr);

            networks.put("uuid", "66778ebf-f406-433d-a63b-1f9ac331b449");
            network_Arr.put(networks);
            server.put("networks", network_Arr);

            jsonObject.put("server", server);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    /* start server
        {
            "os-start" : null
        }
     */
    public String os_StartServer() {
        JSONObject jsonObject = new JSONObject();

        Log.e("httputl", "start server START!");

        try {
            jsonObject.put("os-start", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    /* stop server
        {
            "os-stop" : null
        }
     */
    public String os_StopServer() {
        JSONObject jsonObject = new JSONObject();

        Log.e("httputl", "stop server START!");

        try {
            jsonObject.put("os-stop", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
}

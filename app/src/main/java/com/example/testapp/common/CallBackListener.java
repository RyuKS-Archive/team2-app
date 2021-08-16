package com.example.testapp.common;

public interface CallBackListener {
    void changeServerStatus (boolean status, String id);

    void deleteServer (String id);

    void showUsageStatistics (String serverName, String tenantId);

}

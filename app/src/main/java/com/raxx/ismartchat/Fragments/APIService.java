package com.raxx.ismartchat.Fragments;

import com.raxx.ismartchat.Notifications.MyResponse;
import com.raxx.ismartchat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAd5vDsY0:APA91bGDO-bUo3eW9adu2Ap3eHgp_P9g6AQZRsQcGHIxFUT5pRbWCSpN6f79PbsCnF-W7vtF5FdzyN4xomz_d5GJ3itF7rWjweeJKUjqjsrvbwwzgOjLyyEpM52S3l6ZCSeS1t1kzcWE"

    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

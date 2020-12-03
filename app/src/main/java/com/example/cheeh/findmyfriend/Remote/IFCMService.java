package com.example.cheeh.findmyfriend.Remote;

import com.example.cheeh.findmyfriend.Model.MyResponse;
import com.example.cheeh.findmyfriend.Model.Request;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            //key taken from Firebase Project Settings > Cloud Messaging
            //                      > Project Credential > Server Key
        "Content-Type:application/json",
            "Authorization: key=AAAA8wtPCNM:APA91bHQRNuvh5UE3Nch1wM-MgB0N_wsSsyKpLXr3BkRcztnxgRptTGm7ya8iEiQmaoXFXdEJCbrmRS58vWKjaluhxmsg1kwcOhXZxDfj0h9GZAQr4k8WO66hA_IOfjcjE0BgvqjRpkE"
    })

    @POST("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}

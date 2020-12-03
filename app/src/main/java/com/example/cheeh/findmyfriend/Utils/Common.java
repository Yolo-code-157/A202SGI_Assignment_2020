package com.example.cheeh.findmyfriend.Utils;

import com.example.cheeh.findmyfriend.Model.User;
import com.example.cheeh.findmyfriend.Remote.IFCMService;
import com.example.cheeh.findmyfriend.Remote.RetrofitClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;

public class Common {
    //database child declaration into firebase cloud storage
    public static final String USER_INFORMATION = "UserInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUid";
    public static final String TOKENS = "Tokens";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUid" ;
    public static final String TO_UID = "ToUid";
    public static final String TO_NAME = "ToName";
    public static final String FRIEND_REQUEST = "FriendRequests";
    public static final String PUBLIC_LOCATION = "PublicLocation";

    public static User loggedUser;
    public static User trackingUser;

    //FCM broadcast for request notification
    public static IFCMService getFCMService(){
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService.class);
    }

    //converting timestamp to date
    public static Date convertTimeStampToDate(long time){
        return new Date(new Timestamp(time).getTime());
    }

    //Getting dd-mm-yy HH:mm format for last location marker details
    public static String getDateFormatted(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();
    }
}

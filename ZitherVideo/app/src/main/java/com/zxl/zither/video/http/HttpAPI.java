package com.zxl.zither.video.http;

import com.zxl.zither.video.model.response.FileInfoResponse;
import com.zxl.zither.video.model.response.LoginResponseBean;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.model.response.VideoFileInfoResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface HttpAPI {
    @GET("register")
    public Observable<ResponseBaseBean> register(@Query("user_name")String userName, @Query("pass_word")String passWord);
//    public Call<ResponseBody> register(@Query("user_name")String userName, @Query("pass_word")String passWord);

    @GET("login")
    public Observable<LoginResponseBean> login(@Query("user_name")String userName, @Query("pass_word")String passWord);
//    public Call<ResponseBody> login(@Query("user_name")String userName, @Query("pass_word")String passWord);

    @GET("get_video_file_list")
    public Observable<VideoFileInfoResponse> getVideoFileList();

    @POST("upload_video_file")
    public Observable<ResponseBaseBean> uploadVideoFile(@QueryMap Map<String,String> params, @Body RequestBody multipartBody);
//    public Call<ResponseBody> uploadVideoFile(@QueryMap Map<String,String> params, @Body RequestBody multipartBody);

    @POST("upload_file")
    public Call<ResponseBody> uploadFile(@Body RequestBody multipartBody);
}

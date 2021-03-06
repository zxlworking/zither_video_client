package com.zxl.zither.video.http;

import com.zxl.zither.video.model.response.EvaluateSelfResponseBean;
import com.zxl.zither.video.model.response.FileInfoResponse;
import com.zxl.zither.video.model.response.LoginResponseBean;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.model.response.VideoFileInfoResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
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

    @GET("delete_video_file")
    public Observable<ResponseBaseBean> deleteVideoFileList(@Query("video_id")List<String> videoIds);

//    @Multipart
    @POST("upload_video_file")
//    public Observable<ResponseBaseBean> uploadVideoFile(@QueryMap Map<String,String> params, @PartMap HashMap<String,RequestBody> bodyMap);
    public Observable<ResponseBaseBean> uploadVideoFile(@QueryMap Map<String,String> params, @Body RequestBody multipartBody);

    @POST("test/star/evaluate_self?test_param=test_param_value")
    public Observable<EvaluateSelfResponseBean> uploadStarImgFile(@QueryMap Map<String,String> params, @Body RequestBody multipartBody);
//    public Call<ResponseBody> uploadStarImgFile(@QueryMap Map<String,String> params, @Body RequestBody multipartBody);

    @GET("/test/star/star_info_list")
    public Observable<EvaluateSelfResponseBean> getStarInfoList(@Query("page")int page, @Query("page_size")int pageSize);

    @POST("upload_file")
    public Call<ResponseBody> uploadFile(@Body RequestBody multipartBody);
}

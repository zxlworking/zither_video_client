package com.zxl.zither.video.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.zither.video.R;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.common.DebugUtil;
import com.zxl.zither.video.http.FileRequestBody;
import com.zxl.zither.video.http.HttpUtils;
import com.zxl.zither.video.http.RetrofitCallback;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.data.EvaluateSelfInfo;
import com.zxl.zither.video.model.response.EvaluateSelfResponseBean;
import com.zxl.zither.video.model.response.ResponseBaseBean;
import com.zxl.zither.video.utils.SharePreUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhangxiaolong on 19-7-5.
 */
public class UpLoadStarFileActivity extends BaseActivity {

    private static final String TAG = "UpLoadStarFileActivity";

    private static final int OPEN_GALLERY_REQUEST_CODE = 3;

    private View mUploadImgFileView;
    private TextView mUploadProgressTv;
    private ImageView mStarImg;
    private TextView mChooseStarTv;
    private RecyclerView mSearchResultRecyclerView;

    private String mUploadImgFilePath;

    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    private SearchResultAdapter mSearchResultAdapter;

    private RetrofitCallback mRetrofitCallback = new RetrofitCallback() {
        @Override
        public void onSuccess(Call call, Response response) {
            DebugUtil.d(TAG, "RetrofitCallback::onSuccess");
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    mUploadProgressTv.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "上传完成", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onLoading(final long total, final long progress) {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    float p = (float) (100 * progress * 1.0 / total);

                    mUploadProgressTv.setVisibility(View.VISIBLE);
                    mUploadProgressTv.setText("已上传" + mDecimalFormat.format(p) + "%");
                    if (p >= 100) {
                        mUploadProgressTv.setVisibility(View.GONE);
                    }
                }
            });
            DebugUtil.d(TAG, "RetrofitCallback::onLoading = " + (progress * 1.0 / total));
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            DebugUtil.d(TAG, "RetrofitCallback::onLoading");
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    mUploadProgressTv.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    public int getResLayout() {
        return R.layout.activity_upload_star_file;
    }

    @Override
    public void initView() {

        mUploadImgFileView = findViewById(R.id.upload_img_file_view);
        mUploadProgressTv = findViewById(R.id.upload_progress_tv);
        mStarImg = findViewById(R.id.star_img);
        mChooseStarTv = findViewById(R.id.choose_star_tv);
        mSearchResultRecyclerView = findViewById(R.id.search_result_recycler_view);

//        mSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mSearchResultAdapter = new SearchResultAdapter();
        mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);

        mStarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mOpenGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mOpenGalleryIntent.setType("image/*");
                startActivityForResult(mOpenGalleryIntent, OPEN_GALLERY_REQUEST_CODE);
            }
        });

        mUploadImgFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUploadImgFilePath)) {
                    Toast.makeText(mActivity, "图片不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                File uploadVideoImgFile = new File(mUploadImgFilePath);

                RequestBody imgRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadVideoImgFile);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.addFormDataPart("value", uploadVideoImgFile.getName(), imgRequestBody);
                builder.setType(MultipartBody.FORM);
                MultipartBody multipartBody = builder.build();
                FileRequestBody fileRequestBody = new FileRequestBody(multipartBody, mRetrofitCallback);


                Map<String, String> params = new HashMap<>();

                HttpUtils.getInstance().uploadStrImgFile(params, fileRequestBody, new NetRequestListener() {
                    @Override
                    public void onSuccess(ResponseBaseBean responseBaseBean) {
                        EvaluateSelfResponseBean evaluateSelfResponseBean = (EvaluateSelfResponseBean) responseBaseBean;
                        DebugUtil.d(TAG, "uploadStrImgFile::onSuccess::evaluateSelfResponseBean = " + evaluateSelfResponseBean);
                        if (evaluateSelfResponseBean.mResponseBaseBean.code != 0) {
                            Toast.makeText(mActivity, "未找到匹配信息", Toast.LENGTH_SHORT).show();
                            mSearchResultAdapter.setData(null);
                        } else {
                            Toast.makeText(mActivity, "上传完成", Toast.LENGTH_SHORT).show();
                            mSearchResultAdapter.setData(evaluateSelfResponseBean.mEvaluateSelfInfoList);
                        }
                    }

                    @Override
                    public void onNetError() {

                    }

                    @Override
                    public void onNetError(Throwable e) {

                    }

                    @Override
                    public void onServerError(ResponseBaseBean responseBaseBean) {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugUtil.d(TAG, "onActivityResult::requestCode = " + requestCode);
        DebugUtil.d(TAG, "onActivityResult::resultCode = " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_GALLERY_REQUEST_CODE:
                    if (data == null) {
                        DebugUtil.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::data null");
                    } else {
                        DebugUtil.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::data = " + data.getData());
                        String mGalleryPath = CommonUtils.parseGalleryPath(mActivity, data.getData());
                        DebugUtil.d(TAG, "onActivityResult::GALLERY_OPEN_REQUEST_CODE::mGalleryPath = " + mGalleryPath);
                        mUploadImgFilePath = mGalleryPath;

                        mChooseStarTv.setVisibility(View.GONE);

                        Glide.with(mActivity).load(mUploadImgFilePath).into(mStarImg);
                    }
                    break;
            }
        }
    }

    public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultHolder> {

        private List<EvaluateSelfInfo> mEvaluateSelfInfoList = new ArrayList<>();

        public void setData(List<EvaluateSelfInfo> list) {
            mEvaluateSelfInfoList.clear();
            if(list != null){
                mEvaluateSelfInfoList.addAll(list);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_search_star_result_view, viewGroup, false);
            return new SearchResultHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchResultHolder searchResultHolder, int i) {
            final EvaluateSelfInfo evaluateSelfInfo = mEvaluateSelfInfoList.get(i);

            
            //DebugUtil.d(TAG, "onBindViewHolder::evaluateSelfInfo = " + evaluateSelfInfo);

            Glide.with(searchResultHolder.mItemSearchStarImg).load(evaluateSelfInfo.mStarInfo.mStarImgUrl).into(searchResultHolder.mItemSearchStarImg);

            searchResultHolder.mItemSearchStarName.setText(evaluateSelfInfo.mStarInfo.mStarName);

            double mSimilarity = 100 * evaluateSelfInfo.mSimilarity;
            searchResultHolder.mItemSearchStarSimilarity.setText("相似度:" + mDecimalFormat.format(mSimilarity) + "%");

            searchResultHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, WebViewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(WebViewActivity.EXTRA_URL, evaluateSelfInfo.mStarInfo.mStarDetailUrl);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEvaluateSelfInfoList.size();
        }
    }

    public class SearchResultHolder extends RecyclerView.ViewHolder {

        public ImageView mItemSearchStarImg;

        public TextView mItemSearchStarName;
        public TextView mItemSearchStarSimilarity;

        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);

            mItemSearchStarImg = itemView.findViewById(R.id.item_search_star_img);
            mItemSearchStarName = itemView.findViewById(R.id.item_search_star_name);
            mItemSearchStarSimilarity = itemView.findViewById(R.id.item_search_star_similarity);
        }
    }
}

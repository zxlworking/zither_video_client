package com.zxl.zither.video.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zxl.zither.video.R;
import com.zxl.zither.video.common.CommonUtils;
import com.zxl.zither.video.common.DebugUtil;
import com.zxl.zither.video.http.HttpUtils;
import com.zxl.zither.video.http.listener.NetRequestListener;
import com.zxl.zither.video.model.data.EvaluateSelfInfo;
import com.zxl.zither.video.model.response.EvaluateSelfResponseBean;
import com.zxl.zither.video.model.response.ResponseBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaolong on 19-7-9.
 */
public class StarInfoListActivity extends BaseActivity {

    private static final String TAG = "StarInfoListActivity";

    private Button mSearchStarBtn;

    private RecyclerView mRecyclerView;

    private StarInfoAdapter mStarInfoAdapter;

    private int mCurrentPage = 0;
    private int mPageSize = 10;

    private boolean isLoading = false;

    @Override
    public int getResLayout() {
        return R.layout.activity_star_info_list;
    }

    @Override
    public void initView() {
        mSearchStarBtn = findViewById(R.id.search_star_btn);
        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mStarInfoAdapter = new StarInfoAdapter();
        mRecyclerView.setAdapter(mStarInfoAdapter);

        mSearchStarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UpLoadStarFileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        getDataFromNet(mCurrentPage, mPageSize);
    }

    public void getDataFromNet(int page, int pageSize){
        if(isLoading){
            return;
        }
        isLoading = true;
        HttpUtils.getInstance().getStarInfoList(mCurrentPage, mPageSize, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                EvaluateSelfResponseBean evaluateSelfResponseBean = (EvaluateSelfResponseBean) responseBaseBean;
                DebugUtil.d(TAG, "uploadStrImgFile::onSuccess::evaluateSelfResponseBean = " + evaluateSelfResponseBean);
                if (evaluateSelfResponseBean.mResponseBaseBean.code != 0) {
                    Toast.makeText(mActivity, "没有更多信息", Toast.LENGTH_SHORT).show();
                    mStarInfoAdapter.setData(null);
                } else {
                    mCurrentPage++;
                    mStarInfoAdapter.setData(evaluateSelfResponseBean.mEvaluateSelfInfoList);
                }
                isLoading = false;
            }

            @Override
            public void onNetError() {
                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                isLoading = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                isLoading = false;
            }
        });
    }

    public class StarInfoAdapter extends RecyclerView.Adapter<StarInfoHolder> {

        private List<EvaluateSelfInfo> mEvaluateSelfInfoList = new ArrayList<>();

        public void setData(List<EvaluateSelfInfo> list) {
            if(list != null){
                mEvaluateSelfInfoList.addAll(list);
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public StarInfoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_search_star_result_view, viewGroup, false);
            return new StarInfoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StarInfoHolder starInfoHolder, int position) {
//            int screenWidth = CommonUtils.screenWidth();
//            int screenHeight = CommonUtils.screenHeight();
//            int width = (screenWidth - CommonUtils.px2dip(16)) / 3;
//            int height = width * screenHeight / screenWidth;
//            starInfoHolder.mItemSearchStarImg.getLayoutParams().width = width;
//            starInfoHolder.mItemSearchStarImg.getLayoutParams().height = height;

            final EvaluateSelfInfo evaluateSelfInfo = mEvaluateSelfInfoList.get(position);

            Glide.with(starInfoHolder.mItemSearchStarImg).load(evaluateSelfInfo.mStarInfo.mStarImgUrl).into(starInfoHolder.mItemSearchStarImg);

            starInfoHolder.mItemSearchStarName.setText(evaluateSelfInfo.mStarInfo.mStarName);

            if(position == getItemCount() - 1){
                getDataFromNet(mCurrentPage, mPageSize);
            }

            starInfoHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

    public class StarInfoHolder extends RecyclerView.ViewHolder {

        public ImageView mItemSearchStarImg;

        public TextView mItemSearchStarName;
        public TextView mItemSearchStarSimilarity;

        public StarInfoHolder(@NonNull View itemView) {
            super(itemView);

            mItemSearchStarImg = itemView.findViewById(R.id.item_search_star_img);
            mItemSearchStarName = itemView.findViewById(R.id.item_search_star_name);
            mItemSearchStarSimilarity = itemView.findViewById(R.id.item_search_star_similarity);
            mItemSearchStarSimilarity.setVisibility(View.GONE);
        }
    }
}

package com.kilo.dev.androidnews.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.kilo.dev.androidnews.Global;
import com.kilo.dev.androidnews.R;
import com.kilo.dev.androidnews.net.HomePageData;
import com.kilo.dev.androidnews.news.NewsDetailActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ct_OS on 2018-1-2.
 */

public class NewsCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private Banner mHeaderBanner;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    public NewsCardAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView mText;
        ImageView mImage;
        TextView mTime;
        TextView mFrom;
        TextView mTitle;
        ImageView ivCardArrow;
        ConstraintLayout layoutCardInfo;
        LinearLayout layoutCardTitle;
        boolean isExtend = false;
        boolean isAnima = false;

        public NewsViewHolder(View view) {
            super(view);
            this.view = view;
            mText = view.findViewById(R.id.tv_card_text);
            mTitle = view.findViewById(R.id.tv_card_title);
            mFrom = view.findViewById(R.id.tv_card_from);
            mTime = view.findViewById(R.id.tv_card_time);
            mImage = view.findViewById(R.id.iv_card_img);
            ivCardArrow = view.findViewById(R.id.iv_card_arrow);
            layoutCardInfo = view.findViewById(R.id.layout_card_info);
            layoutCardTitle = view.findViewById(R.id.layout_card_title);
        }
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        Banner mBanner;

        public BannerViewHolder(View view) {
            super(view);
            mBanner = view.findViewById(R.id.banner);
            mHeaderBanner = mBanner;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Global.getInstance().getCurrentHomePageData().getT1348647909107().get(position).getAds() == null)
            return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return Global.getInstance().getCurrentHomePageData().getT1348647909107().size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = mInflater.inflate(R.layout.item_home_banner,
                    viewGroup, false);
            return new BannerViewHolder(view);
        }
        View view = mInflater.inflate(R.layout.item_home_news,
                viewGroup, false);
        return new NewsCardAdapter.NewsViewHolder(view);
    }


    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        HomePageData.T1348647909107Bean item = Global.getInstance().getCurrentHomePageData().getT1348647909107().get(i);
        if (viewHolder instanceof NewsViewHolder) {
            ((NewsViewHolder) viewHolder).mTitle.setText(item.getLtitle());
            Glide.with(mContext).load(item.getImgsrc()).into(((NewsViewHolder) viewHolder).mImage);
            ((NewsViewHolder) viewHolder).mText.setText(item.getDigest());
            ((NewsViewHolder) viewHolder).mTime.setText(item.getPtime());
            ((NewsViewHolder) viewHolder).mFrom.setText(item.getSource().replace("$", ""));
            ((NewsViewHolder) viewHolder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext,NewsDetailActivity.class);
                            i.putExtra("id",item.getPostid());
                    mContext.startActivity(i);
                }
            });
            ((NewsViewHolder) viewHolder).layoutCardTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeNewsAnim(((NewsViewHolder) viewHolder));
                }
            });

            //开局隐藏
            ((NewsViewHolder) viewHolder).layoutCardInfo.getLayoutParams().height = 0;
            ((NewsViewHolder) viewHolder).layoutCardInfo.requestLayout();

        } else if (viewHolder instanceof BannerViewHolder) {
            List imgs = new ArrayList();
            List titles = new ArrayList();
            for (HomePageData.T1348647909107Bean.AdsBean bean:item.getAds()){
                imgs.add(bean.getImgsrc());
                titles.add(bean.getTitle());
            }
            ((BannerViewHolder) viewHolder).mBanner
                    .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                    .setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 360));
            ((BannerViewHolder) viewHolder).mBanner
                    .setImageLoader(new GlideImageLoader())
                    .setImages(imgs)
                    .setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            ToastUtils.showShort("跳转页面");
                        }
                    })
                    .setBannerTitles(titles)
                    .start();
        }
    }

    public Banner getHeaderBanner(){
        return mHeaderBanner;
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */
            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);

            //用fresco加载图片简单用法，记得要写下面的createImageView方法
            Uri uri = Uri.parse((String) path);
            imageView.setImageURI(uri);
        }
    }

    private void changeNewsAnim(NewsViewHolder viewHolder){
        if (viewHolder.isAnima){
            return;
        }
//        int high = viewHolder.layoutCardInfo.getMeasuredHeight();
        int high = 180;
        ValueAnimator va ;
        viewHolder.isAnima = true;
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator1,animator2;
        if (viewHolder.isExtend){
            animator1 = ObjectAnimator.ofFloat(viewHolder.layoutCardInfo,"translationY",0,-high).setDuration(250);
            animator2 = ObjectAnimator.ofFloat(viewHolder.ivCardArrow,"rotation",0,180);
            va = ValueAnimator.ofInt(high,0);
            viewHolder.isExtend = false;
        }else {
            animator1 = ObjectAnimator.ofFloat(viewHolder.layoutCardInfo,"translationY",-high,0).setDuration(250);
            animator2 = ObjectAnimator.ofFloat(viewHolder.ivCardArrow,"rotation",180,0);
            va = ValueAnimator.ofInt(0,high);
            viewHolder.isExtend = true;
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                viewHolder.layoutCardInfo.getLayoutParams().height = h;
                viewHolder.layoutCardInfo.requestLayout();
            }
        });
        va.setDuration(250);

        animator2.setDuration(250).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {viewHolder.isAnima = false;}

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        animatorSet.play(animator1).with(animator2).with(va);
        animatorSet.start();
    }
}
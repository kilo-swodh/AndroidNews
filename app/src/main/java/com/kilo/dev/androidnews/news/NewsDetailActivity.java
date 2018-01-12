package com.kilo.dev.androidnews.news;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.ToastUtils;
import com.kilo.dev.androidnews.R;
import com.kilo.dev.androidnews.common.BaseActivity;
import com.kilo.dev.androidnews.common.util.StatusbarUI;
import com.kilo.dev.androidnews.net.NewsDetailData;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kilo.dev.androidnews.Appconfig.getNewsDetailA;
import static com.kilo.dev.androidnews.Appconfig.getNewsDetailB;
import static com.kilo.dev.androidnews.Appconfig.urlHostCm;

/**
 * Created by ct_OS on 2018-1-3.
 */

public class NewsDetailActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progress)
    ProgressBar progress;
    private String postId = "";
    private NewsDetailData currentData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        postId = getIntent().getStringExtra("id");
        initData();
    }

    private void initData() {
        EasyHttp.get(getNewsDetailA + postId + getNewsDetailB)
                .baseUrl(urlHostCm)
                .readTimeOut(8 * 1000)//局部定义读超时
                .writeTimeOut(8 * 1000)
                .connectTimeout(8 * 1000)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.showShort("网络异常"+e.getMessage());
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            String jsonNoHeader = response.substring(20, response.length());
                            String jsonFine = jsonNoHeader.substring(0, jsonNoHeader.length() - 1);
                            currentData = gson.fromJson(jsonFine, NewsDetailData.class);
                            initWeb();
                        }
                    }
                });
    }

    private void initWeb() {
        WebSettings webSetting = webview.getSettings();

        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String html = "<!DOCTYPE html>" +
                "<html lang=\"zh\">" +
                "<head>" +
                "<meta charset=\"UTF-8\" />" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />" +
                "<title>Document</title>" +
                "<style>" +
                "body img{" +
                "width: 100%;" +
                "height: 100%;" +
                "}" +
                "body video{" +
                "width: 100%;" +
                "height: 100%;" +
                "}" +
                "div{width:100%;height:30px;} #from{width:auto;float:left;color:gray;} #time{width:auto;float:right;color:gray;}" +
                "</style>" +
                "</head>" +
                "<body>"
                + "<p><h2>" + currentData.getTitle() +"</h2></p>"
                + "<p><div><div id=\"from\">"+currentData.getSource()+
                "</div><div id=\"time\">"+currentData.getPtime()+"</div></div></p>"
                + currentData.getBody() + "</body>" +
                "</html>";
        if (currentData.getVideo() != null) {
            for (NewsDetailData.VideoBean videoBean : currentData.getVideo()) {
                html = html.replace(videoBean.getRef(),
                        "<video src=\"" + videoBean.getMp4_url() +
                                "\" controls=\"controls\" poster=\"" + videoBean.getCover() + "\"></video>");
            }
        }
        if (currentData.getImg() != null) {
            for (NewsDetailData.ImgBean imgBean : currentData.getImg()) {
                html = html.replace(imgBean.getRef(), "<img src=\"" + imgBean.getSrc() + "\"/>");
            }
        }
        progress.setVisibility(View.GONE);
        webview.loadData(html, "text/html; charset=UTF-8", null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.comments:
                ToastUtils.showShort("跳转页面");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    protected void onDestroy() {
        webview.destroy();
        webview = null;
        super.onDestroy();
    }
}

package com.cornflower.pay.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.cornflower.pay.R;

/**
 * Created by xiejingbao on 2016/3/23.
 *
 * 带进度条webview
 */
public class ProgressWebView extends WebView {

    private ProgressBar progressBar;

    public ProgressWebView(Context context) {
        this(context,null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);

        progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,10,0,0));
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_status));
        addView(progressBar);

        setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    progressBar.setVisibility(View.GONE);

                }else
                {
                    if (progressBar.getVisibility() == GONE)
                        progressBar.setVisibility(VISIBLE);
                    progressBar.setProgress(newProgress);
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        if(progressBar!=null){
            LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
            lp.x = l;
            lp.y = t;
            progressBar.setLayoutParams(lp);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }
}

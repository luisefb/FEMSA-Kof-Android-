package mx.app.ambassador.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.joanzapata.pdfview.PDFView;

import mx.app.ambassador.R;

/**
 * Created by kreativeco on 20/04/15.
 */
public class LibraryActivity extends SectionActivity {

    RelativeLayout rlVideo, rlPdf;
    PDFView pdfView;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Biblioteca");

        getWindow().setFormat(PixelFormat.UNKNOWN);
        rlVideo   = (RelativeLayout) findViewById(R.id.rl_video);
        rlPdf     = (RelativeLayout) findViewById(R.id.rl_pdf);
        pdfView   = (PDFView) findViewById(R.id.pdf_view);
        videoView = (VideoView)findViewById(R.id.video_kof);

    }



    /*--------------*/
	/* CLICK EVENTS */

    public void clickShowPdf(View v) {
        int i = Integer.parseInt(v.getTag().toString());
        showPDF(i);
    }

    public void clickShowVideo(View v) {
        int i = Integer.parseInt(v.getTag().toString());
        showVideo(i);
    }

    public void clickHidePdf(View v){
        hide(rlPdf);
    }

    public void clickHideVideo(View v){
        videoView.stopPlayback();
        hide(rlVideo);
    }


	/*----------------*/
	/* CUSTOM METHODS */

    protected void hide(final View r) {

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(r, "alpha",  1.0f, 0.0f);
        alpha1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                r.setVisibility(View.GONE);
            }
        });
        alpha1.start();

    }

    protected void show(View r) {

        r.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(r, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(r, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(r, "alpha",  0.0f, 1.0f);

        scaleX.setDuration(400);
        scaleY.setDuration(400);
        alpha1.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(alpha1);
        set.start();

    }

    public void showVideo(int tag){

        int id = getResources().getIdentifier("guidelines_video_" + String.format("%02d", tag), "raw", getPackageName());

        Log.e("", "android.resource://mx.app.ambassador/" + id);

        Uri uri = Uri.parse("android.resource://mx.app.ambassador/" + id);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        show(rlVideo);

    }

    public void showPDF(int tag){
        String pdf = "guidelines_pdf_" + String.format("%02d", tag) + ".pdf";

        Log.e("", pdf);

        pdfView.fromAsset(pdf).defaultPage(1).load();
        show(rlPdf);
    }

}

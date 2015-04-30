package mx.app.ambassador.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.joanzapata.pdfview.PDFView;

import mx.app.ambassador.R;

/**
 * Created by kreativeco on 30/04/15.
 */
public class InfographicsActivity extends SectionActivity {

    RelativeLayout rlPdf;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infographics);
        overridePendingTransition(R.anim.slide_left_from, R.anim.slide_left);
        setStatusBarColor(SectionActivity.STATUS_BAR_COLOR);
        setTitle("Infografía");

        getWindow().setFormat(PixelFormat.UNKNOWN);
        rlPdf     = (RelativeLayout) findViewById(R.id.rl_pdf);
        pdfView   = (PDFView) findViewById(R.id.pdf_view);

    }

    public void clickSection(View v) {

        int i = Integer.parseInt(v.getTag().toString());
        showPDF(i);
    }

    public void clickHidePdf(View v){
        hide(rlPdf);
    }

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

    public void showPDF(int tag){

        String pdf = "";

        if (tag==1) {
            pdf = "3_1_auxiliar.pdf";
        } else if(tag==2){
            pdf = "3_2_vendedor.pdf";
        } else if(tag==3){
            pdf = "3_3_promotor.pdf";
        } else if(tag==4){
            pdf ="3_4_preventa.pdf";
        }

        if (!pdf.equals("")) {
            pdfView.fromAsset(pdf).defaultPage(1).load();
            show(rlPdf);
        }
    }
}
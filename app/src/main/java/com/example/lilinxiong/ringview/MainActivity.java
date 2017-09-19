package com.example.lilinxiong.ringview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lilinxiong.ringview.view.CircleMenuLayout;
import com.example.lilinxiong.ringview.view.RingView;
import com.example.lilinxiong.ringview.view.bean.Bean;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private CircleMenuLayout cl_group;
    private RingView rl_view;
    private int[] ringColors = new int[4];
    private int[] mTextsColor = new int[4];
    private String[] mTexts = new String[]{"2000\n|\n5000", "5000\n以内", "1000\n以内", "1000\n|\n2000"};

    private int[] mImages = new int[]{
            R.drawable.shape_circle_yellow_50, R.drawable.shape_circle_ring_yellow,
            R.drawable.shape_circle_yellow_50, R.drawable.shape_circle_yellow_50};
    //旋转的角度
    private static final int ROTATION_ANGLE = 90;
    //完成动画的时间
    private static final int SUCCESSFUL_ANIM_TIME = 1000;
    //旋转标志
    private Integer tag = 0;
    //默认选中的是1
    private int oldElect = 1;
    //map
    private Map<Integer, Bean> maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定控件
        initView();
        //初始化数据
        initData();
        //设置监听
        initEvent();
    }

    private void initEvent() {
        cl_group.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {
            @Override
            public void itemClick(View iv, int pos, View tv) {
                //旋转动画
                groupRotating(pos);
                //背景圆弧换颜色动画
                rl_view.setElect(oldElect, pos, 80, 50);
                //之前选中的
                Bean oldBean = maps.get(oldElect);
                //现在选中的
                Bean newBean = maps.get(pos);
                //得到ImageView的宽高
                int width = oldBean.getIv().getWidth();
                int height = oldBean.getIv().getHeight();
                //ImageView缩放、TextView换颜色
                zoomImageViewAnim(oldBean, newBean, width, height);
                oldElect = pos;
            }
        });
    }

    private void zoomImageViewAnim(Bean oldBean, Bean newBean, final int width, final int height) {
        //当前选中的缩小，换背景，字体换颜色
        //将来选中的缩小，换背景，字体换颜色
        final TextView oldTv = oldBean.getTv();
        final ImageView oldIv = oldBean.getIv();

        final TextView newTv = newBean.getTv();
        final ImageView newIv = newBean.getIv();

        ValueAnimator shrinkImageAnimWidth = new ValueAnimator();
        ValueAnimator shrinkImageAnimHeight = new ValueAnimator();
        shrinkImageAnimWidth = ValueAnimator.ofInt(width, 0);
        shrinkImageAnimHeight = ValueAnimator.ofInt(height, 0);
        //缩小
        AnimatorSet set = new AnimatorSet();
        set.playTogether(shrinkImageAnimWidth, shrinkImageAnimHeight);
        set.setDuration(500).start();
        shrinkImageAnimWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int w = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                params.width = w;
                //改变宽度
                oldIv.setLayoutParams(params);
                newIv.setLayoutParams(params);
            }
        });
        shrinkImageAnimHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                params.height = h;
                //改变高度
                oldIv.setLayoutParams(params);
                newIv.setLayoutParams(params);
            }
        });
        shrinkImageAnimWidth.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //缩小动画，完成，开启放大动画
                //文字换颜色
                oldTv.setTextColor(Color.BLACK);
                newTv.setTextColor(Color.WHITE);
                //ImageView换背景
                oldIv.setBackgroundResource(R.drawable.shape_circle_yellow_50);
                newIv.setBackgroundResource(R.drawable.shape_circle_ring_yellow);

                AnimatorSet set1 = new AnimatorSet();
                ValueAnimator bigImageAnimWidth = new ValueAnimator();
                ValueAnimator bigImageAnimHeight = new ValueAnimator();
                bigImageAnimWidth = ValueAnimator.ofInt(0, width);
                bigImageAnimHeight = ValueAnimator.ofInt(0, height);

                set1.playTogether(bigImageAnimWidth, bigImageAnimHeight);
                set1.setDuration(500).start();

                bigImageAnimWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int w = (int) animation.getAnimatedValue();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                        params.width = w;
                        //改变宽度
                        oldIv.setLayoutParams(params);
                        newIv.setLayoutParams(params);
                    }
                });

                bigImageAnimHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int h = (int) animation.getAnimatedValue();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                        params.height = h;
                        //改变高度
                        oldIv.setLayoutParams(params);
                        newIv.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    //旋转
    private void groupRotating(int pos) {
        ValueAnimator anim = new ValueAnimator();
        int newElect = pos;
        int angle = 0;
        if (oldElect == 0 && newElect == 3) {
            //当前选中的是0，未来选中的是3，则顺时针旋转180
            angle = ROTATION_ANGLE;
        } else if (oldElect == 3 && newElect == 0) {
            //当前选中的是3，未来选中的是0，则逆时针旋转180
            angle = -ROTATION_ANGLE;
        } else {
            //若不属于以上两种情况，按普通的处理
            angle = (oldElect - newElect) * ROTATION_ANGLE;
        }
        //范围，基准角度----基准角度+要旋转的角度
        anim = ValueAnimator.ofInt(tag, tag + angle);
        anim.setDuration(SUCCESSFUL_ANIM_TIME).start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int angle = (int) valueAnimator.getAnimatedValue();
                cl_group.setmStartAngle(angle);
            }
        });
        //背景圆环同步旋转
        rl_view.setRingRotating(tag, tag + angle);
        tag = tag + angle;
    }
    private void initData() {
        //文本颜色
        mTextsColor[0] = Color.BLACK;
        mTextsColor[1] = Color.WHITE;
        mTextsColor[2] = Color.BLACK;
        mTextsColor[3] = Color.BLACK;
        //圆环颜色
        ringColors[0] = getResources().getColor(R.color.white_50);
        ringColors[1] = getResources().getColor(R.color.white_30);
        ringColors[2] = getResources().getColor(R.color.white_50);
        ringColors[3] = getResources().getColor(R.color.white_30);
        //设置Item
        cl_group.setMenuItemIconsAndTexts(mImages, mTexts, mTextsColor);
        maps = cl_group.getMaps();
        //设置圆环颜色
        rl_view.setDoughnutColors(ringColors);
        //设置动画完成的时间
        rl_view.setAnimTime(SUCCESSFUL_ANIM_TIME);
    }

    private void initView() {
        cl_group = (CircleMenuLayout) findViewById(R.id.cl_group);
        rl_view = (RingView) findViewById(R.id.id_circle_menu_item_center);
    }
}

package com.example.jishibao;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.gestruelock.*;

public class GuestActivity extends AppCompatActivity implements ExpandLockView.OnLockPanelListener, ExpandLockView.OnUpdateIndicatorListener, ExpandLockView.OnUpdateMessageListener, ExpandLockView.OnFinishDrawPasswordListener {

    private ExpandLockView mLockviewExpand;
    private IndicatorLockView lockviewIndicator;
    private TextView tvMessage;
    private Animation mShakeAnimal;
    private Vibrator mVibrator;
    private int first;
    private int mode;
    //返回信息如果是正确的
    private String succeeMsg="再次输入密码,密码已设置,密码正确,密码正确,请输入新密码";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =getIntent();
        //getXxxExtra方法获取Intent传递过来的数据
        mode=intent.getIntExtra("mode",0);
        first=intent.getIntExtra("first",0);
          Log.d("msg_mode",String.valueOf(mode));
        setContentView(R.layout.password_main);
        mLockviewExpand = (ExpandLockView) findViewById(R.id.lockviewExpand);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        lockviewIndicator = (IndicatorLockView) findViewById(R.id.lockviewIndicator);
        mVibrator =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE); //震动
//        mLockviewExpand.getPaintL().setStrokeWidth(20); //获取paint 修改连接线段的样式
//        mLockviewExpand.setLock_trackColor(0xff0000); //给路径设置不同颜色
        //加载动画资源文件
        mShakeAnimal = AnimationUtils.loadAnimation(this, R.anim.shake);
        Toast.makeText(GuestActivity.this,"手势密码",Toast.LENGTH_SHORT).show();
        mLockviewExpand.setActionMode(mode);//set mode  设置手势密码
 //       mLockviewExpand.setActionMode(1);//set mode  验证手势密码
//        mLockviewExpand.setActionMode(2);//set mode  更换手势密码


//        mLockviewExpand.setHiddenTrack(true); //隐藏轨迹和按钮
        mLockviewExpand.setShowError(true); //显示失败视图
//        mLockviewExpand.setLockTime(2);//设置显示的锁住的时间

        //设置各种回调事件
        mLockviewExpand.setOnLockPanelListener(this);
        mLockviewExpand.setOnUpdateIndicatorListener(this);
        mLockviewExpand.setOnUpdateMessageListener(this);
        mLockviewExpand.setOnFinishDrawPasswordListener(this);
    }

//    @Override
    public void initData() {

    }
    //密码盘被锁住发生的回调
    @Override
    public void onLockPanel() {

    }
    //更新小点显示图
    @Override
    public void onUpdateIndicator() {
        if (mLockviewExpand.getPointTrace().size() > 0) {
            lockviewIndicator.setPath(mLockviewExpand.getPointTrace());
        }
    }
    //返回信息如果是正确的
    @Override
    public void onUpdateMessage(String message) {
        if (succeeMsg.contains(message)){
            tvMessage.setTextColor(0xff434242);//设置提示文字颜色
        }else {//Error
            tvMessage.setTextColor(0xffe44d4d);
            tvMessage.startAnimation(mShakeAnimal); //动画效果
        }
        tvMessage.setText(message);
    }
    //vibration 震动对应的接口
    @Override
    public void vibration(String time) {
        if ("long".equals(time)){
            mVibrator.vibrate(new long[]{50,200},-1);//长震动
        }else {
            mVibrator.vibrate(new long[]{50,50},-1);//震动
        }
    }
    //设置密码成功
    @Override
    public void onSetPassword() {
        Toast.makeText(this, "密码设置成功", Toast.LENGTH_SHORT).show();
        onFinish(1);
        finish();
    }

    //解开密码锁成功
    @Override
    public void onOpenLock() {
        Toast.makeText(this, "成功解锁", Toast.LENGTH_SHORT).show();
        onFinish(1);
        finish();
    }

    /* 禁止返回按钮的点击 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction()== KeyEvent.ACTION_DOWN && (first == 1 &&mode == 1) ) {
            return true;
        } else {
            onFinish(-1);
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onFinish(int result){
        Intent i = new Intent();
        i.putExtra("result", result);
        setResult(2, i);//设置resultCode，onActivityResult()中能获取到
    }
}
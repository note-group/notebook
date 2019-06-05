package com.example.jishibao;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class PaintActivity extends AppCompatActivity {
    private ImageView iv;
    private Bitmap baseBitmap;
    private Button btn_save;
    private Button btn_resume;
    private Canvas canvas;
    private Paint paint;
    public static Note pnote;  //该涂鸦对应的Note
    float radio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_activity);

        verifyStoragePermissions(this);
        radio = 5;
        iv = (ImageView)findViewById(R.id.iv);
        paint = new Paint();
        paint.setStrokeWidth(radio);
        paint.setColor(Color.BLACK);
        //iv = (ImageView)findViewById(R.id.iv);
        btn_resume = (Button)findViewById(R.id.btn_resume);
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(click);
        btn_resume.setOnClickListener(click);
        iv.setOnTouchListener(touch);
    }

    private View.OnTouchListener touch = new View.OnTouchListener(){
        float startX, startY;   // 定义手指开始点击的坐标
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                // 第一次绘图初始化内存图片，背景默认为白色
                case MotionEvent.ACTION_DOWN:
                    if(baseBitmap == null){
                        baseBitmap = Bitmap.createBitmap(iv.getWidth(),iv.getHeight(),
                                Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(Color.WHITE);
                    }
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 在屏幕上移动
                    float stopX = motionEvent.getX();
                    float stopY = motionEvent.getY();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            radio += 0.1;
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    paint.setStrokeWidth(radio);
                    // 根据两点坐标绘制线
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    // 将图片显示在imageview
                    iv.setImageBitmap(baseBitmap);
                    break;
                case MotionEvent.ACTION_UP:
                    radio = 5;
                    break;
                    default:
                        break;
            }
            return true;
        }
    };

    private View.OnClickListener click = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_resume:
                    resumeCanvas();
                    break;
                case R.id.btn_save:
                    saveBitmap();
                    break;
                    default:
                        break;
            }
        }
    };

    // 保存图片
    protected void saveBitmap()
    {
        /*原先的图片保存
        try{
            // 保存到SD卡
            String fileName = "/sdcard/"+System.currentTimeMillis() + ".png";
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Toast.makeText(this, "图片已保存到SD卡", Toast.LENGTH_SHORT).show();

//            new Activity_Anrichen().addpicture(baseBitmap);
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
//            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
//            sendBroadcast(intent);
        }catch (Exception e){
            Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        */
        pnote.AddBitmap(baseBitmap);
    }

    // 清除绘画内容
    protected void resumeCanvas(){
        if (baseBitmap != null){
            baseBitmap = Bitmap.createBitmap(iv.getWidth(),iv.getHeight(),Bitmap.Config.ARGB_8888);
            canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            iv.setImageBitmap(baseBitmap);
            Toast.makeText(this, "已清除画板内容", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity){
        try{
            int permission = ActivityCompat.checkSelfPermission(activity,"android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

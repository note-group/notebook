package com.example.jishibao;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import net.dankito.richtexteditor.android.RichTextEditor;
import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class Activity_Anrichen extends AppCompatActivity implements View.OnClickListener {
    public static boolean isAdd;   //编辑标志.为true表示编辑,为false表示新建
    public static Note thisnote;   //本Activity的当前便签
    private RichTextEditor editor;
    private Button btn;
    private AllCommandsEditorToolbar editorToolbar;
    private LinearLayout mainlay;
    private List<Image> imageList=new ArrayList<>();
    private boolean bStart;  //开始录音标志
    private boolean bPlaying;  //正在播放音频标志
    private boolean bVideo;  //正在播放视频标志
    private boolean bPre;   //准备标志
    private SurfaceView video;  //视频控件
    private Menu mainmenu;   //主菜单
    private String notekey;  //原便签标题
    android.widget.EditText text;
    ImageView[] imageview;
    int imagecount=0;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anri);
        imageview=new ImageView[3];
        bPre=false;
        video=(SurfaceView)findViewById(R.id.surface);
        SurfaceHolder holder=video.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                NoteManager.player.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        NoteManager.sview=video;
        imagecount=0;
        bVideo=false;
        imageview[0]=(ImageView)findViewById(R.id.image1) ;
        imageview[1]=(ImageView)findViewById(R.id.image2) ;
        imageview[2]=(ImageView)findViewById(R.id.image3) ;
        bStart=true;
        bPlaying=false;
        editor = (RichTextEditor) findViewById(R.id.editor);
        Context context = editor.getContext();
        editor.toString();
         text=(EditText)findViewById(R.id.title_text);
        Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);
        text.setTypeface(font);
        text.setTextSize(24);
        editorToolbar = (AllCommandsEditorToolbar) findViewById(R.id.editorToolbar);
        editorToolbar.setEditor(editor);
        editor.getHtml();
        editor.setEditorFontSize(20);
        editor.setPadding((int) (4 * getResources().getDisplayMetrics().density));
        editor.setBackgroundColor(getResources().getColor(R.color.content));
        // some properties you also can set on editor
        // editor.setEditorBackgroundColor(Color.YELLOW);
        // editor.setEditorFontColor(Color.MAGENTA);
        // editor.setEditorFontFamily("cursive");
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        // show keyboard right at start up
        editor.focusEditorAndShowKeyboardDelayed();
        init();
       /* RecyclerView recyclerView=(RecyclerView)findViewById(R.id.image_recycleview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ImageAdapter adapter=new ImageAdapter(imageList);
        recyclerView.setAdapter(adapter);
recyclerView.smoothScrollToPosition(0);*/

        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(thisnote.getVideo()==null)
                {
                    Toast.makeText(MainActivity.mthis,"没有视频哦~",Toast.LENGTH_SHORT);
                    return;
                }
                else if(thisnote.getVideo().compareTo("")==0)
                {
                    Toast.makeText(MainActivity.mthis,"没有视频哦~",Toast.LENGTH_SHORT);
                    return;
                }
                if(!bVideo)
                {
                        video.setBackgroundColor(getResources().getColor(R.color.alphacontent));
                        if(!bPre)
                        {
                            if(!thisnote.LoadVideo(video))
                            {
                                return;
                            }
                            bPre=true;
                        }
                        MainActivity.manager.PlayVideo();
                }
                else
                {
                    video.setBackgroundColor(getResources().getColor(R.color.content));
                    MainActivity.manager.StopVideo();
                    bPre=false;
                }
                bVideo=!bVideo;
            }
        });

    }
    public void init()
    {
        //若该便签原已存在
        if (!isAdd && thisnote != null)
        {
            notekey = thisnote.getTitle();
            text.setText(thisnote.getTitle());
            editor.setHtml(thisnote.getText());
            int i = 0;
            while (i < thisnote.getBitmapNum() && i < imageview.length)
            {
                imageview[i].setImageBitmap(thisnote.getBitmap(i));
                i++;
            }
        } else if (isAdd)  //若该便签为新增便签
        {
            thisnote = new Note(" ");
        }
        if (thisnote.getVideo() == null || thisnote.getVideo().compareTo("") == 0)
        {
            video.setClickable(false);
        }
        else
        {
            video.setClickable(true);
            try
            {
                NoteManager.player.reset();
                NoteManager.player.setDataSource(thisnote.getVideo());
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
            video.setBackgroundColor(getResources().getColor(R.color.havecontent));
        }
        editor.setFocusable(true);
        editor.setFocusableInTouchMode(true);
        editor.requestFocus();
        editor.requestFocusFromTouch();
        NoteManager.player.reset();
    }


    public void addpicture(Bitmap bitmap)
    {
        if(imagecount>=3)
        {
            Toast.makeText(this,"最多放入三张图片",Toast.LENGTH_SHORT).show();
        }
        else
            {
            switch (imagecount)
            {
                case 0:imageview[0].setImageBitmap(bitmap);break;
                case 1:imageview[1].setImageBitmap(bitmap);break;
                case 2:imageview[2].setImageBitmap(bitmap);;break;
            }
            imagecount++;
        }
    }
    @Override
    public void onBackPressed() {
        if(editorToolbar.handlesBackButtonPress() == false)
        {
            super.onBackPressed();
            thisnote.setTitle(text.getText().toString());
            thisnote.setText(editor.getHtml());
            if(isAdd)
            {
                MainActivity.manager.AddNote(thisnote);
            }
            else
            {
                MainActivity.manager.RemoveNote(notekey);
                MainActivity.manager.AddNote(thisnote);
            }
            if(bVideo)
            {
                MainActivity.manager.StopVideo();
            }
            if(bPlaying)
            {
                thisnote.StopMusic();
            }
            thisnote.Save();
            if(!MainActivity.manager.RefreshMetaData())
            {
                Toast.makeText(this,"便签保存失败！",Toast.LENGTH_SHORT);
                return;
            }
            Toast.makeText(this,"便签已保存",Toast.LENGTH_SHORT);
        }
    }
    public void onClick(View v)
    {
        String a=editor.getHtml();
        Toast.makeText(this,editor.getHtml()+"",Toast.LENGTH_SHORT).show();
        //editor.setHtml(a+"<p>asdgjhfgsdajhf</p>");
    }
    //从涂鸦界面返到编辑界面
    @Override
    protected void onRestart()
    {
        super.onRestart();
        int i=0;
        while (i < thisnote.getBitmapNum() && i < imageview.length)
        {
            imageview[i].setImageBitmap(thisnote.getBitmap(i));
            i++;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        mainmenu=menu;
        MenuItem item=menu.findItem(R.id.start);
        //若存在录音，则激活“播放录音”按钮
        if(thisnote!=null)
        {
            if(thisnote.getSoundNum()!=0)
            {
                item.setEnabled(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent=null;
        thisnote.setTitle(text.getText().toString());
        thisnote.setText(editor.getHtml());
        switch (item.getItemId())
        {
            //添加图片:
            case R.id.photo:
                intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,101);
            //    Toast.makeText(this,"add audio",Toast.LENGTH_SHORT).show();
                break;
                //添加视频:
            case R.id.video:
                intent=new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,102);
               // Toast.makeText(this,"add vidio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.recording:
                if(thisnote.getSoundNum()==1)
                {
                    Toast.makeText(this,"不能再录音了！",Toast.LENGTH_LONG).show();
                    return true;
                }
                if(bStart)
                {
                    item.setTitle("结束录音");
                    thisnote.StartAudioRecord();
                    Toast.makeText(this,"开始录音！说几句话吧！",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    item.setTitle("开始录音");
                    thisnote.EndAudioRecord(true,0);
                    Toast.makeText(this,"录音已结束!",Toast.LENGTH_SHORT).show();
                    MenuItem sitem=mainmenu.findItem(R.id.start);
                    sitem.setEnabled(true);
                }
                bStart=!bStart;
                break;
                //涂鸦界面
            case R.id.paint:
                Intent intent1 = new Intent(Activity_Anrichen.this, PaintActivity.class);
                PaintActivity.pnote=thisnote;
                startActivity(intent1);
                //Toast.makeText(this,"add graffiti_item",Toast.LENGTH_SHORT).show();
                break;
            case R.id.save:
                MainActivity.manager.RemoveNote(thisnote.getTitle());
                thisnote=MainActivity.manager.AddNote(text.getText().toString());
                thisnote.setText(editor.getHtml());
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
               // Toast.makeText(this,""+editor.getHtml(),Toast.LENGTH_SHORT).show();
                break;
                //开始播放录音：
            case R.id.start:
                if(!bPlaying)
                {
                    thisnote.StartMusic(0);
                    item.setTitle("停止播放");
                }
                else
                {
                    thisnote.StopMusic();
                    item.setTitle("开始播放");
                }
                bPlaying=!bPlaying;
                break;
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }
    //相应活动结果
    //101=添加图片
    //102=添加视频
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode== Activity.RESULT_OK&&requestCode==101&&data!=null)
        {
           Uri selpic=data.getData();
           String[] picpath={MediaStore.Images.Media.DATA};
           Cursor cur=getContentResolver().query(selpic,picpath,null,null,null);
           cur.moveToFirst();
           int colindex=cur.getColumnIndex(picpath[0]);
           String imgpath=cur.getString(colindex);
           thisnote.AddBitmap(0,imgpath,true);
           addpicture(thisnote.getBitmap(thisnote.getBitmapNum()-1));
           cur.close();
        }else if(resultCode== Activity.RESULT_OK&&requestCode==102&&data!=null)
        {
            Uri selpic=data.getData();
            String[] picpath={MediaStore.Video.Media.DATA};
            Cursor cur=getContentResolver().query(selpic,picpath,null,null,null);
            cur.moveToFirst();
            int colindex=cur.getColumnIndexOrThrow(picpath[0]);
            String videopath=cur.getString(colindex);
            thisnote.AddVideo(videopath);
            cur.close();
        }
    }

}

package com.example.jishibao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import net.dankito.richtexteditor.android.RichTextEditor;
import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class Activity_Anrichen extends AppCompatActivity implements View.OnClickListener {

    private RichTextEditor editor;
    private Button btn;
    private AllCommandsEditorToolbar editorToolbar;
    private List<Image> imageList=new ArrayList<>();
    EditText text;
ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    int imagecount=0;
    private DBHelper dbHelper;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anri);

        imageView1=(ImageView)findViewById(R.id.image1) ;
        imageView2=(ImageView)findViewById(R.id.image2) ;
        imageView3=(ImageView)findViewById(R.id.image3) ;
        imageView4=(ImageView)findViewById(R.id.image4) ;


        editor = (RichTextEditor) findViewById(R.id.editor);
        Context context = editor.getContext();
        editor.toString();
         text=(EditText)findViewById(R.id.title_text);
        Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC);
        text.setTypeface(font);


        dbHelper=new DBHelper(this,"Book.db",null,1);
        editorToolbar = (AllCommandsEditorToolbar) findViewById(R.id.editorToolbar);
        editorToolbar.setEditor(editor);
        editor.getHtml();
        editor.setEditorFontSize(20);
        editor.setPadding((int) (4 * getResources().getDisplayMetrics().density));

        // some properties you also can set on editor
        // editor.setEditorBackgroundColor(Color.YELLOW);
        // editor.setEditorFontColor(Color.MAGENTA);
        // editor.setEditorFontFamily("cursive");

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

    }


    public void init()
    {

    }


    public void addpicture(Bitmap bitmap)
    {
        if(imagecount>=4)
        {
            Toast.makeText(this,"最多放入四张图片",Toast.LENGTH_SHORT).show();
        }
        else{
            switch (imagecount)
            {
                case 0:imageView1.setImageBitmap(bitmap);break;
                case 1:imageView2.setImageBitmap(bitmap);break;
                case 2:imageView3.setImageBitmap(bitmap);;break;
                case 3:imageView4.setImageBitmap(bitmap);break;
            }
        }
    }
    @Override
    public void onBackPressed() {
        if(editorToolbar.handlesBackButtonPress() == false) {
            super.onBackPressed();
        }
    }
    public void onClick(View v)
    {
        String a=editor.getHtml();
        Toast.makeText(this,editor.getHtml()+"",Toast.LENGTH_SHORT).show();
        //editor.setHtml(a+"<p>asdgjhfgsdajhf</p>");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_audio:
                Toast.makeText(this,"add audio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_vidio:
                Toast.makeText(this,"add vidio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_recording:
                Toast.makeText(this,"add recording:",Toast.LENGTH_SHORT).show();
                break;
            case R.id.graffiti_item:
                Toast.makeText(this,"add graffiti_item",Toast.LENGTH_SHORT).show();
                break;
            case R.id.keep:
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("title",""+text.getText());
                values.put("body",""+editor.getHtml());
                db.insert("Book",null,values);
                values.clear();
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,""+editor.getHtml(),Toast.LENGTH_SHORT).show();

                Cursor cursor=db.query("Book",null,null,null,null,null,null);
                if(cursor.moveToFirst())
                {
                    do{
                        Toast.makeText(this,cursor.getString(cursor.getColumnIndex("title")),Toast.LENGTH_SHORT).show();
                    }while(cursor.moveToNext());
                }
                cursor.close();;
                break;
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

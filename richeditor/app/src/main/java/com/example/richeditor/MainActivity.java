package com.example.richeditor;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import net.dankito.richtexteditor.android.RichTextEditor;
import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RichTextEditor editor;
    private Button btn;
    private AllCommandsEditorToolbar editorToolbar;
private List<Image> imageList=new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=(Button)findViewById(R.id.btn1);
        btn.setOnClickListener(this);
        editor = (RichTextEditor) findViewById(R.id.editor);
        Context context = editor.getContext();
        editor.toString();
        EditText text=(EditText)findViewById(R.id.title_text);
        Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC);
text.setTypeface(font);

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
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.image_recycleview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ImageAdapter adapter=new ImageAdapter(imageList);
        recyclerView.setAdapter(adapter);
    }

public void init()
{
    Image image=new Image(R.drawable.pic1);
imageList.add(image);
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
        editor.setHtml(a+"<p>asdgjhfgsdajhf</p>");
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
        }
        return true;
    }
}

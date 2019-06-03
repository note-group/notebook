package com.example.jishibao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.widget.EditText;

import net.dankito.richtexteditor.android.RichTextEditor;

public class watch_activity extends AppCompatActivity {
    private RichTextEditor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_activity);
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        String body=intent.getStringExtra("body");
        editor = (RichTextEditor) findViewById(R.id.editor_watch);
        EditText editText=(EditText)findViewById(R.id.title_text_watch);
        editText.setText(title);
        editor.setHtml(Html.toHtml(new SpannableString(body)));
    }
}

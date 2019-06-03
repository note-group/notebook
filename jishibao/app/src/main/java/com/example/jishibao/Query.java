package com.example.jishibao;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;

import java.util.ArrayList;
import java.util.List;

import kotlin.reflect.jvm.internal.impl.renderer.RenderingFormat;

public class Query extends AppCompatActivity {
RecyclerView recyclerView;
    private List<Item> itemList=new ArrayList<>();
private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        recyclerView=(RecyclerView)findViewById(R.id.query_recyclerview);

        dbHelper=new DBHelper(this,"Book.db",null,1);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ItemAdapter adapter=new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);

        Intent intent=getIntent();
        String query=intent.getStringExtra("query_string");
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("Book",null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                String title_html=cursor.getString(cursor.getColumnIndex("title"));
                String body_html=cursor.getString(cursor.getColumnIndex("body"));
                String title= Html.fromHtml(title_html).toString();
                String body= Html.fromHtml(body_html).toString();
                if(title.contains(query))
                {
                    Item item1=new Item(""+title,""+body);
                    itemList.add(item1);
                }
            }while(cursor.moveToNext());
        }
    }
}

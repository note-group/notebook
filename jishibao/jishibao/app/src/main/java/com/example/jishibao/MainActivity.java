package com.example.jishibao;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Item> itemList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           initItem();
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.main_recyclerview);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ItemAdapter adapter=new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation);
        navigationView.setCheckedItem(R.id.setting);
        navigationView.setCheckedItem(R.id.item1);
        navigationView.setCheckedItem(R.id.item2);
        navigationView.setCheckedItem(R.id.item3);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId())
               {
                   case R.id.setting:
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.item1:
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.item2:
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.item3:
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
               }
               return true;
            }
        });
    }



    private void initItem()
    {
        for(int i=0;i<3;i++)
        {
            Item item1=new Item("1","11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
            itemList.add(item1);
            Item item2=new Item("1","222222222222222222222222222222222222222");
            itemList.add(item2);
            Item item3=new Item("1","33333333333333333333333333333333333333");
            itemList.add(item3);
            Item item4=new Item("1","4444444444444444444444441111111114444444444444444");
            itemList.add(item4);
            Item item5=new Item("1","555555555555555555555555551111111111111111111111111155555555555555555");
            itemList.add(item5);
            Item item6=new Item("1","666666666666666666666666666666666666666666");
            itemList.add(item6);
            Item item7=new Item("1","77777777777777777777777771111111111111111111111111111177777777777777777777777");
            itemList.add(item7);
            Item item8=new Item("1","88888888888888888888888888881111111111111111111111111111111111888888888888888888888888");
            itemList.add(item8);
            Item item9=new Item("1","999999999999999111111111111111111111111111111111111111111111111111111111111111111111111111111111111111199");
            itemList.add(item9);
            Item item10=new Item("1","1231232312312312");
            itemList.add(item10);
            Item item11=new Item("1","1111111fffdsad111111111");
            itemList.add(item11);
            Item item12=new Item("1","11111111111111111111");
            itemList.add(item12);
            Item item13=new Item("1","111111111aaas11111111111111");
            itemList.add(item13);
            Item item14=new Item("1","1111111111asdfsadf111111111");
            itemList.add(item14);
            Item item15=new Item("1","111111111111111111111111");
            itemList.add(item15);
            Item item16=new Item("1","111111111111111111111111");
            itemList.add(item16);
            Item item17=new Item("1","11114562434asdf sadfsdaf11111");
            itemList.add(item17);

        }
    }
}

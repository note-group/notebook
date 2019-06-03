package com.example.jishibao;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.example.jishibao.GuestActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private List<Item> itemList=new ArrayList<>();
    private SharedPreferences mShared;
    private String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShared = getSharedPreferences("share_data", MODE_PRIVATE);
//        String desc = "共享参数中保存的信息如下：";
//                 Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
//                 for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
//                         String key = item_map.getKey();
//                         Object value = item_map.getValue();
//                         if (value instanceof String) {
//                                 desc = String.format("%s\n　%s的取值为%s", desc, key,
//                                                 mShared.getString(key, ""));
//                             } else if (value instanceof Integer) {
//                                 desc = String.format("%s\n　%s的取值为%d", desc, key, mShared.getInt(key, 0));
//                             } else if (value instanceof Float) {
//                                 desc = String.format("%s\n　%s的取值为%f", desc, key, mShared.getFloat(key, 0.0f));
//                             } else if (value instanceof Boolean) {
//                                 desc = String.format("%s\n　%s的取值为%b", desc, key, mShared.getBoolean(key, false));
//                             } else if (value instanceof Long) {
//                                 desc = String.format("%s\n　%s的取值为%d", desc, key, mShared.getLong(key, 0l));}
//                 else {
//                                 desc = String.format("%s\n参数%s的取值为未知类型", desc, key);
//                             }
//                    }
//        Log.d("msg",desc);
        pass = mShared.getString("gesturePw","");
        Log.d("msg_pass",pass);
        if(pass.length()!=0) {
            Intent intent = new Intent(MainActivity.this,GuestActivity.class);
            intent.putExtra("mode",1);
            intent.putExtra("first",1);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);
        initItem();
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.main_recyclerview);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        final ItemAdapter adapter=new ItemAdapter(itemList);
        adapter.setOnremoveListnner(new ItemAdapter.OnremoveListnner() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void ondelect(final int x) {
                    //弹出一个dialog，用用户选择是否删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog alertDialog = builder.setTitle("系统提示：")
                            .setMessage("确定要删除该便签吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    itemList.remove(x);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this,"已成功删除第"+(x+1)+"个元素",Toast.LENGTH_SHORT).show();
                                    //后台删除列表
                                }
                            }).create();
                    alertDialog.show();
            }
        });
        recyclerView.setAdapter(adapter);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation);
        navigationView.setCheckedItem(R.id.setting);
        navigationView.setCheckedItem(R.id.update);
        navigationView.setCheckedItem(R.id.delete);
        navigationView.setCheckedItem(R.id.item1);
        navigationView.setCheckedItem(R.id.item2);
        navigationView.setCheckedItem(R.id.item3);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId())
               {
                   case R.id.setting:
                       pass = mShared.getString("gesturePw","");
                       Log.d("msg_pass",pass);
                       if(pass.length()==0) {
                           showGuestModal(0);
                       }else{Toast.makeText(MainActivity.this,"手势密码已存在",Toast.LENGTH_SHORT).show();}
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.update:
                       pass = mShared.getString("gesturePw","");
                       Log.d("msg_pass",pass);
                       if(pass.length()!=0) {
                           showGuestModal(0);
                           showGuestModal(1);
                       }
                       else{Toast.makeText(MainActivity.this,"当前无手势密码",Toast.LENGTH_SHORT).show();}
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.delete:
                       pass = mShared.getString("gesturePw","");
                       Log.d("msg_pass",pass);
                       if(pass.length()!=0) {
                           showGuestModal(1);
                       } else{Toast.makeText(MainActivity.this,"当前无手势密码",Toast.LENGTH_SHORT).show();}
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.item1:
                       showGuestModal(0);
                       Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.item2:
//                       showGuestModal(0);
//                       showGuestModal(1);
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

    public void showGuestModal(int i){
        Intent intent = new Intent(MainActivity.this,GuestActivity.class);
        intent.putExtra("mode",i);
        startActivityForResult(intent, 1);
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 super.onActivityResult(requestCode, resultCode, data);
                 //当otherActivity中返回数据的时候，会响应此方法
                 //requestCode和resultCode必须与请求startActivityForResult()和返回setResult()的时候传入的值一致。

                    //删除手势
                    if(requestCode==1&&resultCode==2)
                     {
                         int result=data.getIntExtra("result", 0);
                         if(result==1){
                             SharedPreferences sharedPref = getSharedPreferences("share_data", MODE_PRIVATE);
                             //打开SharedPreferences的编辑状态
                             SharedPreferences.Editor editor = sharedPref.edit();
                             //存储数据，用户名，键值对的形式
                             editor.putString("gesturePw", "");
                             //提交，保存数据
                             editor.apply();
                         }
                     }
             }

}

package com.example.jishibao;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private List<Item> itemList=new ArrayList<>();
    private SearchView mSearchView;
    private FloatingActionButton floatingActionButton;
    private SharedPreferences mShared;
    private String pass;
    private String permis[];             //权限字符串
    public static MainActivity mthis;    //主活动
    public static NoteManager manager;   //数据管理器
    private boolean bInit;              //初始化标志
    private ItemAdapter adapter;        //适配器
    RecyclerView recyclerView;
    private PullToRefreshView mPullToRefreshView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bInit=false;
        mShared = getSharedPreferences("share_data", MODE_PRIVATE);
        String desc = "共享参数中保存的信息如下：";
        final Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
            String key = item_map.getKey();
            Object value = item_map.getValue();
            if (value instanceof String) {
                desc = String.format("%s\n　%s的取值为%s", desc, key,
                        mShared.getString(key, ""));
            } else if (value instanceof Integer) {
                desc = String.format("%s\n　%s的取值为%d", desc, key, mShared.getInt(key, 0));
            } else if (value instanceof Float) {
                desc = String.format("%s\n　%s的取值为%f", desc, key, mShared.getFloat(key, 0.0f));
            } else if (value instanceof Boolean) {
                desc = String.format("%s\n　%s的取值为%b", desc, key, mShared.getBoolean(key, false));
            } else if (value instanceof Long) {
                desc = String.format("%s\n　%s的取值为%d", desc, key, mShared.getLong(key, 0l));}
            else {
                desc = String.format("%s\n参数%s的取值为未知类型", desc, key);
            }
        }
        Log.d("msg_desc",desc);
        pass = mShared.getString("gesturePw","");
        Log.d("msg_pass",pass);
        if(pass.length()!=0) {
            Intent intent = new Intent(MainActivity.this,GuestActivity.class);
            intent.putExtra("mode",1);
            intent.putExtra("first",1);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);

        mPullToRefreshView = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleFresh();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.clearFocus();
        mSearchView.setFocusable(false);
        recyclerView=(RecyclerView)findViewById(R.id.main_recyclerview);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        RequestAll();
        mthis=this;
        //EditText editText;
        //添加便签事件：
        floatingActionButton=(FloatingActionButton)findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Activity_Anrichen.class);
                Activity_Anrichen.isAdd=true;
                startActivity(intent);
            }
        });

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
                       pass = mShared.getString("gesturePw","");
                       Log.d("msg_pass",pass);
                       if(pass.length()==0) {
                           showGuestModal(0);
                           Toast.makeText(MainActivity.this,"手势设置",Toast.LENGTH_SHORT).show();
                       }else{Toast.makeText(MainActivity.this,"手势密码已存在",Toast.LENGTH_SHORT).show();}
                       break;
                       //Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                      // startActivity(intent);
                   case R.id.update:
                       pass = mShared.getString("gesturePw","");
                       Log.d("msg_pass",pass);
                       if(pass.length()!=0) {
                           showGuestModal(0);
                           showGuestModal(1);
                           Toast.makeText(MainActivity.this,"手势修改",Toast.LENGTH_SHORT).show();  break;
                       }
                       else{Toast.makeText(MainActivity.this,"当前无手势密码",Toast.LENGTH_SHORT).show();}
                       //Toast.makeText(MainActivity.this,"setting clicked",Toast.LENGTH_SHORT).show();  break;
                   case R.id.delete:
                       pass = mShared.getString("gesturePw","");
                       Log.d("msg_pass",pass);
                       if(pass.length()!=0) {
                           showGuestModal(3);
                           Toast.makeText(MainActivity.this,"手势删除",Toast.LENGTH_SHORT).show();
                       } else{Toast.makeText(MainActivity.this,"当前无手势密码",Toast.LENGTH_SHORT).show();}
                       break;
                       //上传
                   case R.id.item1:
                       manager.Upload();
                       Toast.makeText(MainActivity.this,"上传成功！",Toast.LENGTH_SHORT).show();  break;
                       //下载
                   case R.id.item2:
                       manager.Download();
                       Toast.makeText(MainActivity.this,"下载成功！",Toast.LENGTH_SHORT).show();  break;
                       //改变存储路径
                   case R.id.item3:
                       Toast.makeText(MainActivity.this,"这个功能还没做呢,亲~~~",Toast.LENGTH_SHORT).show();  break;
               }
               return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(MainActivity.this,query+"",Toast.LENGTH_SHORT).show();
                /*
Intent intent=new Intent(MainActivity.this,Query.class);
intent.putExtra("query_string",query);
startActivity(intent);
*/
                return false;
            }

            // 当搜索内容改变时触发该方法
            @TargetApi(25)
            @Override
            public boolean onQueryTextChange(String newText)
            {
                ArrayList<Note> re=MainActivity.manager.Search(newText);
                itemList.clear();
                int i=0;
                while(i<re.size())
                {
                    Note n=re.get(i);
                    itemList.add(new Item(n.getTitle(),Html.fromHtml(n.getText(),0).toString()));
                    i++;
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NoteManager.recoder != null) {
            NoteManager.recoder.release();
            NoteManager.recoder = null;
        }
    }
    @TargetApi(25)
    private void initItem()
    {
        if(adapter==null) {
            adapter = new ItemAdapter(itemList);
            adapter.setOnremoveListnner(new ItemAdapter.OnremoveListnner() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void ondelect(final int  x) {
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
                                    String title = itemList.get(x).getTitle().toString();
                                    itemList.remove(x);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this,"已成功删除",Toast.LENGTH_SHORT).show();
                                    //后台删除列表
                                    deleteItem(title);
                                }
                            }).create();
                    alertDialog.show();
                }
            });
            recyclerView.setAdapter(adapter);
        }
        //填充数据
        itemList.clear();
        ArrayList<Note> notelist=manager.getNoteList();
        int i=0;
        while(i<notelist.size())
        {
            Note n=notelist.get(i);
            Item item=new Item(n.getTitle(),Html.fromHtml(n.getText(),0).toString());
            itemList.add(item);
            i++;
        }
        adapter.notifyDataSetChanged();
    }

    public void showGuestModal(int i){
        Intent intent = new Intent(MainActivity.this,GuestActivity.class);
        if(i==3){
            intent.putExtra("mode",1);
            intent.putExtra("type",1);
        }
        else{intent.putExtra("mode",i);}
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

    protected void onRestart() {
        super.onRestart();
        initItem();
    }
    public void RequestAll()
    {
        permis=new String[5];
        permis[0]=new String(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permis[1]=new String(Manifest.permission.READ_EXTERNAL_STORAGE);
        permis[2]=new String(Manifest.permission.RECORD_AUDIO);
        permis[3]=new String(Manifest.permission.KILL_BACKGROUND_PROCESSES);
        permis[4]=new String(Manifest.permission.VIBRATE);
        if(ContextCompat.checkSelfPermission(this, permis[0])!= PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this, permis[1])!= PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this, permis[2])!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permis[3])!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permis[4])!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,permis[0]))
            {
                Toast t= Toast.makeText(this,"求求你了,给个权限吧!",Toast.LENGTH_LONG);
                t.show();
            }
            else
            {
                ActivityCompat.requestPermissions(this,permis,1);
            }
        }
        else
            {
            if(!bInit)
            {
                manager=new NoteManager();
                manager.Init();
                initItem();
                bInit=true;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            int i=0;
            while(i<permissions.length)
            {
                if(grantResults[i]== PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this,"权限:"+permissions[i]+"申请成功!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this,"权限:"+permissions[i]+"申请失败!",Toast.LENGTH_SHORT).show();
                }
                i++;
            }
            if(!bInit)
            {
                manager=new NoteManager();
                manager.Init();
                initItem();
                bInit=true;
            }
        }
    }

    private void handleFresh()
    {
        initItem();
    }

    private void deleteItem(String title){
        manager.RemoveNote(title);
    }
}

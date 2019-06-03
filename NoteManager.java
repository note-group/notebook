package com.example.note;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Base64;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//笔记管理器NoteManager
/*
* 初始化:new一个NoteManager对象，然后调用该对象的Init方法
*
*********************功能详解*************************
* 1.添加便签
* a.调用NoteManager.AddNote(String)方法添加Note对象，传入便签标题作为参数。
* ->若添加成功，该方法返回你刚刚添加的Note对象
* ->若添加失败，该方法返回null
* b.开始编辑便签。
* c.调用a中的Note对象的setText(String)方法存储便签内容(即HTML内容)
*   调用a中的Note对象的setTitle(String)方法存储便签标题
* d.用户需要结束编辑时，询问用户是否需要保存该便签
* ->若是。则调用a中获得的Note对象的Save方法。
* ->若否。则调用NoteManager.RemoveNote(String)方法，传入a中便签标题作为参数。
*   之后请勿再次使用a中的对象
*
* 2.添加图片:需要事先准备好文件路径
* a.调用NoteManager.getNote(String)方法获取Note对象，需要传入便签标题为参数，同时返回一个Note对象
* (如果你已经拥有了Note对象，则步骤a可以省略)
* b.调用a中的Note对象的AddBitmap方法，传入图片的路径
* c.调用a中的Note对象的getBitmap(int)方法，传入图片的ID，于是你得到了一个Bitmap对象
* d.用你的ImageView的setBitmap方法尽情地显示图片吧!
*
* 3.添加音频:
* a.调用NoteManager.getNote(String)方法获取Note对象，需要传入便签标题为参数
* (如果你已经拥有了Note对象，则步骤a可以省略)
* b.调用a中的Note对象的StartAudioRecord来开始录音。
* c.调用a中的Note对象的EndAudioRecord来结束录音。
* d.调用a中的Note对象的StartMusic(int)来播放音乐，传入录音的ID
*   调用a中的Note对象的StopMusic()来结束音乐
*
* 4.添加视频:需要有至少1个SurfaceView来显示视频
* a.调用NoteManager.getNote(String)方法获取Note对象，需要传入便签标题为参数
* (如果你已经拥有了Note对象，则步骤a可以省略)
* b.调用a中的Note对象的AddVideo(String)方法添加视频，需要传入视频文件的路径作为参数
* c.调用你的全局NoteManager对象的SetVideoTarget(SurfaceView)来将你的视频输出目标设置为传入参数的SurfaceView
* d.调用全局NoteManager对象的PlayVideo方法来播放视频
*   调用全局NoteManager对象的PauseVideo方法来暂停视频
*   调用全局NoteManager对象的StopVideo方法来停止播放视频
*
* 5.移除便签
* a.调用你的全局NoteManager对象的RemoveNote(String)方法，需要传入便签标题作为参数
* 注意：该操作将删除该便签关联的所有文件与文件夹!
*
* 6.移除图片
* a.调用NoteManager.getNote(String)方法获取Note对象，需要传入便签标题为参数
* (如果你已经拥有了Note对象，则步骤a可以省略)
* b.调用a中Note对象的RemoveBitmap(int)方法，需要传入图片ID，即可移除图片
*
* 7.上传文件
* a.调用NoteManager对象的Upload方法
*
* 8.下载文件
* a.调用NoteManager对象的Download方法
*
**************************** 注意事项 **********************************
* 1：程序终止前，请在主活动的onDestroy方法中调用NoteManager对象的SaveAllNote方法。
*       否则，所有的图片/文字/视频/音频/元数据都不会保存。
* 2：在程序运行时，应保证NoteManager对象全局唯一！
* 3：NoteManager的初始化工作必须在成功获取权限之后执行！
*
*/
public class NoteManager
{
    private static String metapath="/storage/emulated/0/note.txt";  //路径文件
    private static String loadpath="/storage/emulated/0/notedata";  //上传路径
    //path:全局存储路径.在设置中更改
    public static String path;       // 存储路径
    public static RThread reth;      //录音线程/播放线程
    public static AudioRecord recoder;  //录音机
    public static int buffersize;  //缓存大小
    public static AudioTrack track;  //播放机
    public static int tracksize;   //播放缓存
    public static MediaPlayer player;  //视频播放器
    private HashMap<String,Note> notedata;  //数据部分
    public NoteManager()
    {
        //如果录音器没有初始化，则初始化它
        if(NoteManager.recoder==null)
        {
            NoteManager.buffersize=AudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);
            NoteManager.recoder=new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                    48000,               //48k采样率
                    AudioFormat.CHANNEL_IN_STEREO,      //双声道
                    AudioFormat.ENCODING_PCM_16BIT,    //PCM16位编码
                    NoteManager.buffersize);
        }
        //如果播放器没有初始化，则初始化它
        if(NoteManager.track==null)
        {
            tracksize=AudioTrack.getMinBufferSize(48000,AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);
            track=new AudioTrack(AudioManager.STREAM_MUSIC,
                    48000,
                    AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    tracksize,
                    AudioTrack.MODE_STREAM);
        }
        notedata=new HashMap<String,Note>();
        player=new MediaPlayer();
    }
    //单独保存
    public void SaveNote(String Title)
    {
        Note n=notedata.get(Title);
        n.Save();
    }
    //添加新笔记.返回这个新增的Note实例
    public Note AddNote(String Title)
    {
        Note n=new Note(Title);
        if(notedata.get(Title)==null)
        {
            notedata.put(Title, n);
            return n;
        }
        else
        {
            return null;
        }
    }
    //删除便签
    public void RemoveNote(String Title)
    {
        //删除
        Note n=notedata.get(Title);
        if(n!=null)
        {
            n.Delete();  //删除所有内容
            notedata.remove(Title);  //清除
        }
    }
    //获取笔记
    public Note getNote(String Title)
    {
        return notedata.get(Title);
    }
    //在程序开始时，请调用该函数完成初始化
    public void Init()
    {
        //读取元数据文件
        File metaf=new File(metapath);
        FileInputStream fins=null;
        byte pathbuffer[]=new byte[1024];
        byte metabuffer[]=new byte[4096];
        try
        {
            if(!metaf.exists())
            {
                throw new Exception("没有找到元数据文件!请确认根目录!");
            }
            fins=new FileInputStream(metaf);
            fins.read(pathbuffer,0,1024);
            String direcstr= new String(pathbuffer, StandardCharsets.UTF_8);
            int last=direcstr.indexOf("\0");
            direcstr=direcstr.substring(0,last);
            NoteManager.path=new String(Environment.getExternalStorageDirectory()+"/"+direcstr);
            fins.close();
            File pathdirec=new File(NoteManager.path);
            if(!pathdirec.exists())
            {
                if(!pathdirec.mkdirs())
                {
                    throw new Exception("文件夹创建失败!");
                }
            }
            //开启线程
            reth=new RThread();
            reth.start();
            //读取便签数据
            File metadata=new File(NoteManager.path+"/metadata.txt");
            if(!metadata.exists())
            {
                metadata.createNewFile();
                return;
            }
            FileInputStream metains=new FileInputStream(metadata);
            int readbyte=0;
            String metastr=new String("");
            while(metains.read(metabuffer,0,4096)!=-1)
            {
                String temp=new String(metabuffer);
                temp=temp.substring(0,readbyte);
                metastr=metastr+temp;
            }
            //分析便签数据
           String[] notestr=metastr.split("\r\n");
            int i=0;
            while(i<notestr.length)
            {
                Note n=Note.toNote(notestr[i]);
                notedata.put(n.getTitle(),n);
                i++;
            }
            //分析过程结束OVER
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //存储所有便签
    public void SaveAllNote()
    {
        try
        {
            File f=new File(NoteManager.path+"/metadata.txt");
            FileOutputStream fouts=new FileOutputStream(f);
            Iterator<HashMap.Entry<String, Note>> i = notedata.entrySet().iterator();
            while (i.hasNext())
            {
                HashMap.Entry<String, Note> e = (HashMap.Entry<String, Note>) i.next();
                Note n = e.getValue();
                //写入元数据
                fouts.write(n.toString().getBytes());
                //保存
                n.Save();
            }
            fouts.flush();
            fouts.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String getMetaPath()
    {
        return metapath;
    }
    public static void setMetaPath(String MetaPath)
    {
        metapath=MetaPath;
    }
    public static String getLoadPath()
    {
        return loadpath;
    }
    //将视频画面输出至SurfaceView.需要提供一个SurfaceView控件
    public void SetVideoTarget(SurfaceView Target)
    {
        player.setDisplay(Target.getHolder());
    }
    //开始播放
    public void PlayVideo()
    {
        player.start();
    }
    //暂停播放
    public void PauseVideo()
    {
        player.pause();
    }
    //停止播放
    public void StopVideo()
    {
        player.stop();
    }
    //上传
    public boolean Upload()
    {
        //检查目标文件夹是否存在：
        File dload=new File(loadpath);
        if(!dload.exists())
        {
            if(!dload.mkdir())
            {
                return false;
            }
        }
        //打开原目录：
        File dorg=new File(NoteManager.path);
        if(!dorg.exists())
        {
            return false;
        }
        //获取原目录下所有文件：
        File[] forg=dorg.listFiles();
        int i=0;
        while(i<forg.length)
        {
            try
            {
                File fload = new File(loadpath + "/" + forg[i].getName());
                if (!fload.exists())
                {
                    if(!fload.createNewFile())
                    {
                        return false;
                    }
                }
                //复制文件
                FileInputStream fins=new FileInputStream(forg[i].getAbsolutePath());
                FileOutputStream fouts=new FileOutputStream(fload.getAbsolutePath());
                byte[] buffer=new byte[4096];
                int re=fins.read(buffer,0,4096);
                while(re!=-1)
                {
                    fouts.write(buffer,0,re);
                    re=fins.read(buffer,0,4096);
                }
                fouts.flush();
                fouts.close();
                fins.close();
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
                return false;
            }catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }
            i++;
        }
        return true;
    }
    //下载
    public boolean Download()
    {
        //检查目标文件夹是否存在：
        File dload=new File(loadpath);
        if(!dload.exists()||!dload.isDirectory())
        {
            return false;
        }
        //检查原存储文件夹是否存在：
        File dorg=new File(NoteManager.path);
        if(!dorg.exists())
        {
            return false;
        }
        File[] fload=dload.listFiles();
        int i=0;
        while(i<fload.length)
        {
            try
            {
                File fnew=new File(NoteManager.path+"/"+fload[i].getName());
                if(fnew.exists())
                {
                    i++;
                    continue;
                }
                else
                {
                    if(!fnew.createNewFile())
                    {
                        return false;
                    }
                }
                FileInputStream fins=new FileInputStream(fload[i]);
                FileOutputStream fouts=new FileOutputStream(fnew);
                byte[] buffer=new byte[4096];
                int re=fins.read(buffer,0,4096);
                while(re!=-1)
                {
                    fouts.write(buffer,0,re);
                    re=fins.read(buffer,0,4096);
                }
                fouts.flush();
                fouts.close();
                fins.close();
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
                return false;
            }catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }
            i++;
        }
        return true;
    }
}

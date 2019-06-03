package com.example.note;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

//用于表示1个笔记的实体类Node
public class Note
{
    private String title;  //标题
    private String text;  //HTML文本
    private ArrayList<PICTURE> picture;  //图片
    private ArrayList<SOUND> sound;     //音频
    private String video;  // 视频名
    public Note(String Title)
    {
        title=Title;
        text="";
        picture=new ArrayList<PICTURE>();
        sound=new ArrayList<SOUND>();
        video=new String();
    }
    //添加视频
    public void AddVideo(String VideoPath)
    {
        video=VideoPath;
        try {
            NoteManager.player.reset();
            NoteManager.player.setDataSource(VideoPath);  // 获取视频文件
            NoteManager.player.prepare();  //装载视频文件
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

    }
    //删除视频
    public void RemoveVideo()
    {
        video=null;
        NoteManager.player.reset();
    }
    //保存便签(包括所有图片/音频/视频文件，以及保存了标题和内容的html文件)
    public void Save()
    {
        //保存图片
        int i=0;
        FileOutputStream fous=null;
        while(i<picture.size())
        {
            PICTURE p=picture.get(i);
            File pf=new File(p.getPath());
            try {
                if (pf.exists()) {
                    if(!pf.delete())
                    {
                        throw new Exception("原文件"+p.getPath()+"删除失败!");
                    }
                }
                if(!pf.createNewFile())
                {
                    throw new Exception("创建文件"+p.getPath()+"删除失败!");
                }
                fous=new FileOutputStream(pf);
                p.getPicture().compress(Bitmap.CompressFormat.PNG,100,fous);
                fous.flush();
                fous.close();
            }catch(FileNotFoundException fnfe)
            {
                fnfe.printStackTrace();
            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            i++;
        }
        //保存音频
        i=0;
        int datafrag=0;
        try {
            while (i < sound.size()) {
                SOUND s = sound.get(i);
                ArrayList<byte[]> sdata = s.getSoundData();
                FileOutputStream sout=new FileOutputStream(s.getName());
                while (datafrag < sdata.size())
                {
                    sout.write(sdata.get(datafrag));
                    datafrag++;
                }
                sout.flush();
                sout.close();
                datafrag = 0;
                i++;
            }
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        //保存文本
        File fnote=new File(NoteManager.path+"/"+title+".html");
        try {
            FileOutputStream fnoteout = new FileOutputStream(fnote);
            fnoteout.write(text.getBytes());
            fnoteout.flush();
            fnoteout.close();
        }catch (IOException ioe)
        {
            ioe.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        //视频仅保存路径
    }
    //添加图片
    public void AddBitmap(int Offset,String Path,boolean LoadNow)
    {
        //加载图片
        PICTURE p=new PICTURE(picture.size(),Offset,Path,LoadNow);
        //更改图片路径至存储路径
        p.setPath(NoteManager.path+"/"+title+String.valueOf(p.getID())+".png");
        //添加图片
        picture.add(p);
    }
    public void RemoveBitmap(int ID)
    {
        //删除
        picture.remove(ID);
        //更新ID
        int i=0;
        while(i<picture.size())
        {
            PICTURE p=picture.get(i);
            p.setID(i);
            i++;
        }
    }
    //获取指定图片
    public Bitmap getBitmap(int ID)
    {
        return picture.get(ID).getPicture();
    }
    //开始录音
    public void StartAudioRecord()
    {
        NoteManager.recoder.startRecording();
        NoteManager.reth.StartRecord();
    }
    //结束录音
    //bSave:传入true如果需要保存。若要放弃保存，传入false
    //Offset:录音插入位置
    public void EndAudioRecord(boolean bSave,int Offset)
    {
        NoteManager.reth.StopRecord();
        NoteManager.recoder.stop();
        if(bSave)
        {
            String name=NoteManager.path+"/"+title+String.valueOf(sound.size())+".pcm";
            SOUND s=new SOUND(sound.size(),Offset,name);
            NoteManager.reth.setFilepath(name);
            s.setSoundData(NoteManager.reth.getRecordData());
            sound.add(s);
        }
        else
        {
            NoteManager.reth.getRecordData().clear();
        }
    }
    //删除录音
    public void RemoveAutdioRecord(int ID)
    {
        //删除
        sound.remove(ID);
        //更新ID
        int i=0;
        while(i<sound.size())
        {
            SOUND s=sound.get(i);
            s.setID(i);
            i++;
        }
    }
    //开始播放声音
    public void StartMusic(int MusicID)
    {
        SOUND s=sound.get(MusicID);
        if(s==null)
        {
            return;
        }
        NoteManager.reth.setFilepath(s.getName());
        NoteManager.track.play();
        NoteManager.reth.StartMusic();
    }
    //停止播放声音
    public void StopMusic()
    {
        NoteManager.reth.StopMusic();
        NoteManager.track.stop();
    }
    public void setText(String Text)
    {
        text=Text;
    }
    public void setTitle(String Title)
    {
        title=Title;
    }
    public String getText()
    {
        return text;
    }
    public String getTitle()
    {
        return title;
    }
    //删除与本便签相关的所有文件
    public void Delete()
    {

    }
    //转为元字符串
    public String toString()
    {
        String re=title;
        int i=0;
        while(i<picture.size())
        {
            PICTURE p=picture.get(i);
            re=re+" P:"+p.getPath();
            i++;
        }
        i=0;
        while(i<sound.size())
        {
            SOUND s=sound.get(i);
            re=re+" R:"+s.getName();
            i++;
        }
        if(video!=null)
        {
            re=re+" V:"+video;
        }
        re=re+"\r\n";
        return re;
    }
    //由元字符串生成Note对象
    public static Note toNote(String s)
    {
        String[] snote=s.split(" ");
        Note re=new Note(snote[0]);
        int i=1;
        while(i<snote.length)
        {
            switch(snote[i].charAt(0))
            {
                case 'P':  //图片信息
                    re.picture.add(new PICTURE(re.picture.size(),0,snote[i].substring(2),true));
                    break;
                case 'R':  //音频信息
                    re.sound.add(new SOUND(re.sound.size(),0,snote[i].substring(2)));
                    break;
                case 'V':  //视频信息
                    re.video=snote[i].substring(2);
                    break;
                default:
                    return null;
            }
            i++;
        }
        return re;
    }
}

//图片类
class PICTURE
{
    private int id;         //图片ID
    private int offset;    //图片相对于文字内容的偏移量
    private String path;    //添加图片时,该图片的路径
    private Bitmap bitmap;  //对应的位图数据
    PICTURE(int ID,int Offset,String FilePath,boolean LoadNow)
    {
        id=ID;
        offset=Offset;
        path=FilePath;
        if(LoadNow)
        {
            Load();
        }
    }
    //加载图片
    public void Load()
    {
        try
        {
            File f=new File(path);
            FileInputStream fins=null;
            if(f.exists()&&f.isFile())
            {
                fins=new FileInputStream(f);
            }
            else
            {
                return;
            }
            bitmap= BitmapFactory.decodeStream(fins);
            fins.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public int getID()
    {
        return id;
    }
    public int getOffset()
    {
        return offset;
    }
    public String getPath()
    {
        return path;
    }
    public Bitmap getPicture()
    {
        return bitmap;
    }
    public void setOffset(int Offset)
    {
        offset=Offset;
    }
    public void setPath(String Path)
    {
        path=Path;
    }
    public void setID(int ID)
    {
        id=ID;
    }

}

//音频
class SOUND
{
    private int id;
    private int offset;
    private String name;  //音频路径
    private ArrayList<byte[]> sdata;  //音频数据
    public SOUND(int ID,int Offset,String Name)
    {
        id=ID;
        offset=Offset;
        name=Name;
        sdata=new ArrayList<byte[]>();
    }
    public int getID()
    {
        return id;
    }
    public int getOffset()
    {
        return offset;
    }
    public String getName()
    {
        return name;
    }
    public void setID(int ID)
    {
        id=ID;
    }
    public void setOffset(int Offset)
    {
        offset=Offset;
    }
    public void setName(String Name)
    {
        name=Name;
    }
    public void setSoundData(ArrayList<byte[]> Data)
    {
        sdata=Data;
    }
    public ArrayList<byte[]> getSoundData()
    {
        return sdata;
    }
}




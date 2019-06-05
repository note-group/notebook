package com.example.jishibao;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioRecord;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//用于在录音时读取数据的线程
public class RThread extends Thread implements Runnable
{
    private boolean bworking;
    private boolean bplayvideo;
    private boolean bwrite;
    private boolean bplaysound;
    private ArrayList<byte[]> data;
    private String filepath;
    private FileOutputStream fous;
    private FileInputStream fins;
    RThread()
    {
        bworking=true;
        bplayvideo=false;
        bplaysound=false;
        bwrite=false;
        data=new ArrayList<byte[]>();
        filepath=new String();
    }
    public String getFilePath()
    {
        return filepath;
    }
    public void setFilepath(String Path)
    {
        filepath=Path;
    }
    @Override
    public void run()
    {
        while(bworking)
        {
            if(bplayvideo)
            {
                byte[] tempv=new byte[NoteManager.buffersize];
                int re=NoteManager.recoder.read(tempv,0,NoteManager.buffersize);
                if(re!=AudioRecord.ERROR_INVALID_OPERATION)
                {
                    data.add(tempv);
                }
            }
            else if(bwrite)
            {
                Save();
                bwrite=false;
            }
            else if(bplaysound)
            {
                try
                {
                    byte[] temps=new byte[NoteManager.tracksize];
                    if(fins.available()>0)
                    {
                        fins.read(temps, 0, NoteManager.tracksize);
                        NoteManager.track.write(temps, 0, NoteManager.tracksize);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                //DO NOTHING
            }
        }
    }
    //开始录音
    public void StartRecord()
    {
        bplayvideo=true;
        data.clear();
    }
    //结束录音
    public void StopRecord()
    {
        bplayvideo=false;
        if(fous!=null)
        {
            fous=null;
        }
    }
    //开始写入
    public void Write()
    {
        bwrite=true;
    }
    //实际写入
    private void Save()
    {
        try
        {
            File fout=new File(filepath);
            if(fout.exists())
            {
                if(!fout.delete())
                {
                    throw new Exception("覆盖原文件失败");
                }
            }
            if(!fout.createNewFile())
            {
                throw new Exception("创建新音频失败!");
            }
            fous = new FileOutputStream(fout);
            int i=0;
            while(i<data.size())
            {
                fous.write(data.get(i));
                i++;
            }
            fous.flush();
            fous.close();
        }catch(FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
            bwrite=false;
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            bwrite=false;
        }catch(Exception e)
        {
            e.printStackTrace();
            bwrite=false;
        }
        bwrite=false;
    }
    //程序结束时，调用该方法来结束线程
    public void End()
    {
        bworking=false;
    }
    public void StartMusic()
    {
        bplaysound=true;
        try {
            fins = new FileInputStream(filepath);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    public void StopMusic()
    {
        bplaysound=false;
        if(fins!=null)
        {
            try {
                fins.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<byte[]> getRecordData()
    {
        return data;
    }

}

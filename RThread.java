package com.example.note;


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
                    fins=new FileInputStream(filepath);
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
        boolean bOK=true;
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
            bOK=false;
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            bOK=false;
        }catch(Exception e)
        {
            e.printStackTrace();
            bOK=false;
        }
        Looper.prepare();
        final AlertDialog dianote=new AlertDialog.Builder(MainActivity.mthis).create();
        if(bOK) {
            dianote.setTitle("提示");
            dianote.setMessage("音频写入完成!");
            dianote.setButton(DialogInterface.BUTTON_POSITIVE, "好的",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dianote.cancel();
                        }
                    });
        }
        else
        {
            dianote.setTitle("错误");
            dianote.setMessage("音频写入失败!");
            dianote.setButton(DialogInterface.BUTTON_POSITIVE, "好的",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dianote.cancel();
                        }
                    });
        }
        dianote.show();
    }
    //程序结束时，调用该方法来结束线程
    public void End()
    {
        bworking=false;
    }
    public void StartMusic()
    {
        bplaysound=true;
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

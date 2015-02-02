/*
 * 
 * DIrect Thread implementation of ProcessService. No longer used
 * 
 */
package doohickey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author Ruaridhi Bannatyne
 */
public class ProcessThread extends Thread{

    private LinkedList<File> l;
    private LinkedList<File> done;
    private int workDone;
    private String DESTD;
    private String TARGET; //needs to be LL
    private int BUFFERSIZE = 250;
    private boolean retain; //whether copied files are to be retained upon cancellation
    private int total;
    private int noDone;
    public ProcessThread (LinkedList<File> l, String dest, String target)
    {
     // this.addEventFilter(EventType.ROOT, null);l = l;   
        this.l = l;
        done = new LinkedList<File>();
        DESTD = dest;
        TARGET = target;
        total = l.size();
        noDone = 0;
        this.setDaemon(true);
    }

    
   @Override         //no need of totalWork property
    public void run() {
        try {
            while(l.peek() !=null)
            {
              /* if(this.isCancelled())
               {
                   if(retain) return true;
                   else {
                       while(done.peek() !=  null) done.pop().delete();
                       return false;
                   }
               }//need to fire an event. Here or with a timer which sends updates every 500ms- 1s?  */
                process(l.poll());
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            }
    } //ends call

    private void process(File f)
    {//should: clone file from source dir, remove "_raw" from name, copy to destinatin dir
    //name: destination directory and source name()substring after last \\
        String newName = DESTD + "\\" + shorten(f.getName());

        //System.out.println(newName);
        if(f == null || !f.exists()) System.out.println("process input null");
        File output = new File(newName);
        try{
            FileInputStream in = new FileInputStream(f);
            FileOutputStream out = new FileOutputStream(output);
            byte[] buffer = new byte[BUFFERSIZE];
            while(in.read(buffer)>-1)
            {
                try{
               out.write(buffer);
                }
                catch(IOException e)
                {
                System.out.println("Write error!");
                }
            }
            out.flush();
            in.close();
            out.close();
        done.add(f); //error here: nullPointerException
        noDone++;
        }
    catch(Exception e)
    {
        System.out.println("Input file not found!");
        e.printStackTrace(System.out);
    }
    } //ends process



    public String shorten(String input)
        {
        //removes _raw. Should point to TARGET instead
         if(TARGET == null) return input;
         int targetIndex = input.indexOf(TARGET); //writing own stuff: faster?
         if(targetIndex > -1) {
             String start = input.substring(0, targetIndex); //magic String will need to go
             String end = input.substring(targetIndex+TARGET.length());
             return start+end;   
         }
         else return input;
    }

   public double getWorkDone()
   {
       return noDone/total;
   }
   
   
   public void interrupt(boolean retain)
   {
     if(!retain)
     {
      for(File f: done) f.delete();
     }
   }

}

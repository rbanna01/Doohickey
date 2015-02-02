/*
 * 
 * Used to process a given list of files in a single thread.
 * Copies and may change name if needed. 
 */

package doohickey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Ruaridhi Bannatyne
 */
public class ProcessService extends Service {
    private LinkedList<File> toCopy;
    private final String DESTD;
    private final String TARGET; 
    private boolean retain; //whether copied files are to be retained upon cancellation. Not currently implemented
    private ProcessTask current;
    private int jobSize;
    private int copied;
        
    public ProcessService (String dest, String target)
    {
        DESTD = dest;
        TARGET = target;
    }
    
    @Override
    public Task createTask() 
    {
       current = new ProcessTask(toCopy, DESTD, TARGET);
       return current;
    }
 
    protected void setRetain(boolean whether)
    {
        this.retain = whether;
    }
    
    public void addJob(LinkedList<File> in)
    {
        this.toCopy = in;
        jobSize = in.size();
    }
    
    public void cancel(boolean retain)
    {
       this.retain = retain;
       current.cancel();
    }
    
    public double getHeadway() {
        return copied/jobSize;
    }
    
    public LinkedList<File> getRemaining()
    {
       return this.current.getL();
    }
        
    private class ProcessTask extends Task {
        private final LinkedList<File> l;
        private final LinkedList<File> done;
        private final String DESTD;
        private final String TARGET;
        private final int BUFFERSIZE = 250;
        
        public ProcessTask(LinkedList<File> job, String destination,String toRemove)
        {
            this.l = job;
            this.DESTD = destination;
            this.TARGET = toRemove;
            done = new LinkedList<>();
            copied = 0;
        }
        
        
        @Override
        protected Boolean call() {
            try {
                while(l.peek() !=null)
                {
                   if(this.isCancelled())
                   {/* Not necessarily useful
                       if(retain) return true;
                       else {
                           while(done.peek() !=  null) done.pop().delete();
                           return false;
                       } */
                       return false;
                   }
                    process(l.poll());
                    copied++;
                }
                return true;
            }
            catch(Exception e) {
                e.getMessage();
                return false;}
        } //ends call
     
        private LinkedList<File> getL() { return this.l;}
        private void process(File f)
        {//should: clone file from source dir, remove target string from name, copy to destination dir
        //name: destination directory and source name()substring after last \\
            String newName = DESTD + "\\" + shorten(f.getName());
            //System.out.println(newName);
            if(!f.exists()) System.out.println("process input null");
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
            }
        catch(Exception e)
        {
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
    }; //ends ProcessTask
   
    
    
} //ends ProcessService

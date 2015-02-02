/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package doohickey;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
/**
 *
 * @author rbanna01
 */
public class Test {
    File dummy;
    String source = "D:\\dummyS";
    String dest = "D:\\dummyD";
    public static void main(String[] args)
    { // 
//        Test t = new Test();
     /*   String name = "D:\\dummyS\\1210101_raw01(1).pic";
        
        t.dummy = new File(name);
        /*System.out.println(t.dummy.getName());
        System.out.println(dummy.getName().substring(dummy.getName().length()-3));
        System.out.println(dummy.toString()); 
        File output = t.process(t.dummy);
        //process and shorten okay*/
      //  Properties p = System.getProperties();
      //  p.list(System.out);
    System.out.println(Runtime.getRuntime().availableProcessors());
    }
    
    public class Props{
        private String source;
        private String dest;
        
        public Props() {}
        
        public String getSource() { return source; }
        public String getDest() { return dest;}
        
        public void setSource(String toAdd) { source = toAdd;}
        public void setDest(String toAdd) { dest = toAdd; }
        
        
    } //ends props
    
    
    
    
    public File getDummy() { return dummy;}
    
    
    
    public File process(File input)
    {//should: clone file from source dir, remove "_raw" from name, copy to destinatin dir
        //name: destination directory and source name()substring after last \\
        String newName = dest + "\\" + shorten(input.getName());
        int size = 50;
        System.out.println(newName);
        File output = new File(newName);
        try{
        FileInputStream in = new FileInputStream(input);
        FileOutputStream out = new FileOutputStream(output);
        byte[] buffer = new byte[size];
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
        }
        catch(Exception e)
        {
        System.out.println("Input file not found!");
        System.out.println(e.getMessage());
        }
        //*/
    return output;
    }
    
    public String shorten(String input)
    {
    //removes _raw. Should point to TARGET instead
     String target = "_raw";   
     int targetIndex = input.indexOf(target); //writing own stuff: faster?
     String start = input.substring(0, targetIndex); //magic String will need to go
     String end = input.substring(targetIndex+target.length());
     return start+end;   
    }



}
    
    


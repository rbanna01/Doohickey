/* 
 * Front end: handles user input and coordinates other components with appropriate dependencies.
 * 
 * 
To do: implement retain stuff if needed
 */

package doohickey;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


/**
 *
 * @author rbanna01
 */
public class Doohickey extends Application {
  //will need: TextField for entering directory, buttons: unpack, browse gooy selector, set current as default,
  //quit. Label as statusbar. XML storage of default location?
    private Button cancel;
    private Button okay;
    private Button go;
    private Button browseSource;
    private Button browseDest;
    private CheckBox setDefaultSource;
    private CheckBox setDefaultDest;
    private Button quit;
    private Label status;
    private TextField from;
    private TextField to;
    private TextField type;
    private TextField toRemove;
    private Label sourceLabel;
    private Label destLabel;
    private Label formatLabel;
    private Label targetLabel;
    private CheckBox formatCbox;
    private CheckBox targetCbox;
    private DirectoryChooser dC;
     //constants
    private int BUFFERSIZE = 50;
    private final int DEFAULT_WIDTH = 700;
    private final int DEFAULT_HEIGHT = 300;
    private final String TITLE = "Doohickey";
    private final String SOURCECHECK_TXT = "Set default source directory.";
    private final String DESTCHECK_TXT = "Set default destination directory.";
    private final String DEFAULT_STATUS = "Enter source and destination directories, then press go";
    private final String FROM_DEFAULT = "Source directory:";
    private final String TO_DEFAULT = "Destination directory:";
    private final String FORMAT_DEFAULT = "Format of file to copy:";
    private final String TARGET_DEFAULT = "String to remove from copy name:";
    private final String FORMAT_CBOX = "Set default format";
    private final String TARGET_CBOX = "Set default target";
    private final String PROPERTYNAME = "props.txt";
    private final String BUTTON_HALT = "Abort";
    private final String BUTTON_GO = "Go";
    
    private File PROPERTIES = new File(System.getProperty("user.home") + "\\" + PROPERTYNAME);                
    private ProgressPopup pP;
    //private LinkedList<Service> input;
    private Props DEFAULTPROPS; 
    private final String formatHash = String.valueOf(new String("format").hashCode());
    private final String targetHash = String.valueOf(new String("target").hashCode());
    private final String sourceHash = String.valueOf(new String("source").hashCode());
    private final String destHash = String.valueOf(new String("destination").hashCode());
    //vars
    private boolean defaultFormatChange = false;
    private boolean defaultTargetChange = false;
    private boolean defaultSourceChange = false;
    private boolean defaultDestChange = false;
    private boolean running = false;
    private String defaultSource;
    private String defaultDest;
    private String sourceD;
    private String destD;
    private File source;
    private File dest;
    private LinkedList<File> toCopy;
    private LinkedList<File> directories;
    private String target;
    private String format;
    private int files;
    private int jobSize;
    private Stage stage;
    private Handler h;
    private Timer t;
    private ExecutorService eS;
    private Doohickey app = this;
    private DoubleBinding done;
    private ObservableDoubleValue oDV;
    private double totalWork;
    private Stage temp;
    private boolean confirm;
           
    //Holds the tasks used to copy
    LinkedList<ProcessService> services;
    //Used to obtain progress of all ProcessServices    
    private final DoubleBinding dB = new DoubleBinding() {
      
        @Override
        protected double computeValue() {
            if (services == null) return -5; //accounts for erroneous initalization
            double temp = 0;
            for (ProcessService s: services) temp+= s.getHeadway();
            //System.out.println("Progress: " + temp/4);
            return temp/4;
        }           
    }; //ends dB                
    private Button getButton(String text)
    {
        Button output = new Button(text);
        output.setOnAction(h);
        return output;
    }
    
    private CheckBox getCheckBox(String input)
    {
        CheckBox output = new CheckBox(input);
        output.setOnAction(h);
        return output;
    }
    
    @Override
    public void start(Stage primaryStage) {
        DEFAULTPROPS = initProps();
        directories = new LinkedList<>();
        toCopy = new LinkedList<>();
        sourceD = String.valueOf(DEFAULTPROPS.getSource());
        destD = String.valueOf(DEFAULTPROPS.getDest());
        format = String.valueOf(DEFAULTPROPS.getFormat());
        target = String.valueOf(DEFAULTPROPS.getTarget());
        stage = primaryStage;
        h = new Handler();
        go = getButton(BUTTON_GO);
        browseSource = getButton("Select source directory");
        browseDest = getButton("Select destination");
        setDefaultSource = getCheckBox(SOURCECHECK_TXT);
        setDefaultDest = getCheckBox(DESTCHECK_TXT);
        quit = getButton("Quit");
        status = new Label(DEFAULT_STATUS);
        // barListener inits here if used in this window.
        sourceLabel = new Label(FROM_DEFAULT);
        destLabel = new Label(TO_DEFAULT);
        formatLabel = new Label(FORMAT_DEFAULT);
        targetLabel = new Label(TARGET_DEFAULT);
        from = new TextField(DEFAULTPROPS.getSource());
        to = new TextField(DEFAULTPROPS.getDest());
        type = new TextField(DEFAULTPROPS.getFormat());
        toRemove = new TextField(DEFAULTPROPS.getTarget());
        //Putting it all in place:
        formatCbox = getCheckBox(FORMAT_CBOX);
        targetCbox = getCheckBox(TARGET_CBOX);
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(5, 5, 5, 5));
        root.add(status, 1, 0);
        root.add(sourceLabel, 0, 1);
        root.add(from, 1, 1);
        root.add(browseSource, 2, 1);
        root.add(setDefaultSource, 0, 3);
        root.add(to, 1,4);
        root.add(browseDest,2, 4);
        root.add(destLabel, 0, 4);
        root.add(setDefaultDest, 0, 5);
        root.add(formatLabel, 0, 6);
        root.add(type, 1, 6);
        root.add(formatCbox, 0, 7);
        root.add(targetLabel, 0, 8);
        root.add(toRemove, 1, 8);
        root.add(targetCbox, 0, 9);
        root.add(go,2, 5);
        root.add(quit, 2, 6);
        //Showing scene
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //Gets properties from saved file, if any.
    public Props initProps()
    {
        if (PROPERTIES.isFile() && PROPERTIES.exists())
                {
                if (!PROPERTIES.canRead()) { return new Props();}
                try{    
                        Scanner s = new Scanner(PROPERTIES);
                        Props output = new Props();
                        //format: source x
                        // destination y
                        // format z
                        //target t
                        while (s.hasNextLine())
                        {
                            String line = s.nextLine();
                            if (line.indexOf(sourceHash) != -1)
                            {
                                String source = line.substring(line.indexOf(sourceHash)+ sourceHash.length()+1);
                                source = source.trim();
                                output.setSource(source);
                            }
                            else output.setSource("");
                            if (line.indexOf(destHash) !=-1)
                            {
                                defaultDest = line.substring(line.indexOf(destHash) + destHash.length()+1);
                                defaultDest = defaultDest.replace(".", "");
                                defaultDest = defaultDest.trim();
                                output.setDest(defaultDest);
                            }
                            else output.setDest("");
                            if (line.indexOf(formatHash) != -1)
                            {
                                format = line.substring(line.indexOf(formatHash)+ formatHash.length()+1);
                                format.trim();
                               // System.out.println(format);
                            } //format stuff here
                            else output.setFormat("");
                            if (line.indexOf(targetHash) != -1)
                            {
                            
                                target = line.substring(line.indexOf(targetHash) + targetHash.length()+1);
                                target = target.trim();
                                output.setTarget(target);
                            }
                            else output.setTarget("");
                        } //ends while
                        s.close();
                        return output;
                        }          
                catch (Exception e)
                        { return new Props(); }
                }  
        else {
            return new Props();
            }
        }
    //Used to handle events from buttons in main window and confirm dialogue
    private class Handler implements EventHandler
    { //what about CheckBoxes?
        @Override
        public void handle(Event evt)
        {
          Object evtSource = evt.getSource();  
          if (evtSource == go) { //toDo: needs a state var so operation can be cancelled. Button should be changed to read BUTTON_HALT. Also need to handle completion...
              if (running) {  
                  //cancellation stuff goes here, then rearm
                  boolean retain = false;
                  //boolean whether = checkCancel(); TODO
                  for(ProcessService t: services) t.cancel(retain);
                  go.setText(BUTTON_GO);
                  running = false;
              }
              else {
              sourceD = from.getText();
              destD = to.getText();
              target = toRemove.getText();
              format = type.getText();
              //System.out.println(format);
              //System.out.println(destD);
              if (sourceD == null) 
              {
                  status.setText("No source directory has been entered");
                  from.requestFocus();        
                  return;
              }
              else if (destD == null)
              {
                  status.setText("No destination directory has been entered");
                  to.requestFocus();
                  return;
              }
              source = new File(sourceD);
              dest = new File(destD);
              if (validate(dest) && validate(source))
              { 
                  running = true;
                  arm(false);
                  status.setText("Working");
                  directories.add(source);
                  files = 0;            
                  status.setText("Checking directories...");
                  while(directories.peek() != null)
                  {
                   File[] contents = directories.poll().listFiles();
                   for (File f: contents)
                   {
                       if (f.isDirectory()) directories.add(f);
                       else
                       { //doesn't detect files; check
                           String name = f.getName();
                           if (format != null) {
                           if (name.substring(name.lastIndexOf('.')+1).equals(format)) 
                           {
                               toCopy.add(f);
                                ++files;
                           }
                           }
                           else{
                               toCopy.add(f);
                               ++files;
                           } 
                        }
                    } 
                    }
                  //System.out.println(files + " files");
                  totalWork = files;
                  //Splits files into jobs and passes to appropriate services
                  try{
                    int processors = Runtime.getRuntime().availableProcessors();
                    eS = Executors.newFixedThreadPool(processors);
                    jobSize = files/processors;
                    int jobs = files/jobSize;
                    int remainder = files % jobSize;
                    // System.out.println("job size " + jobSize); //needs to be displayed as a percentage
                    // System.out.println("remainder " + remainder); 
                    status.setText("Copying " + files + " files.");
                    LinkedList<LinkedList<File>> jobList = new LinkedList<>();
                    LinkedList<File> job;
                    int limit = jobs-1;
                    for (int i = 0 ; i < limit; i++)
                    { 
                        job = new LinkedList<>();
                        for (int j = 0 ; j < jobSize; j++)
                          {
                              job.add(toCopy.poll());
                          }
                        //  System.out.println(job.size());
                        jobList.add(job);
                      } //ends for
                     jobList.add(toCopy); //catches any awkward leftovers
                    services = new LinkedList<>();
                    while (jobList.peek()!= null)
                           {
                           ProcessService p = new ProcessService(destD, target);
                           p.addJob(jobList.poll());
                          p.setExecutor(eS);
                           services.add(p);
                           } //ends for-each
                    totalWork = files;
                    for(ProcessService p: services) p.start();
                    
                    //Initialize and show ProgressPopup
                    pP = new ProgressPopup(dB, services);
                    Stage popupStage = pP.getStage();
                    popupStage.showAndWait();
                    
                    //After processing has finished, display results and allow user to interact with interface
                    showSummary();
                    running = false;
                    arm(true);
                }
                catch(ArithmeticException e)
                {
                status.setText("No matching files found in target directory");    
                return;
                }
                }
                status.setText(DEFAULT_STATUS);             
              }
          }    
          else if (evtSource == browseSource)
          { //show browser for a source dir and pass return to all needed vars
              if (dC == null) dC = new DirectoryChooser();
              dC.setTitle("Select source directory");
              source = dC.showDialog(stage);
              from.setText(source.toString());
          }
          else if (evtSource == browseDest)
          { //show destination popup and assign returned value to all appropriate vars
              if (dC == null) dC = new DirectoryChooser();
              dC.setTitle("Select destination directory");
              dest = dC.showDialog(stage);
              to.setText(dest.toString());
          }
          else if (evtSource == quit)   doQuit();
          //Any of the following 4 indicate a change to the default settings which needs to be saved later.
          else if (evtSource == setDefaultSource) {
              if (defaultSourceChange) defaultSourceChange = false;
              else defaultSourceChange = true;
          }
          else if (evtSource == setDefaultDest) {
              if (defaultDestChange) defaultDestChange = false;
              else defaultDestChange = true;                  
              }
          else if (evtSource == formatCbox) {
              if (defaultFormatChange) defaultFormatChange = false;
              else defaultFormatChange = true;                  
          }
          else if (evtSource == targetCbox) {
              if (defaultTargetChange) defaultTargetChange = false;
              else defaultTargetChange = true;
          }
          else if (evtSource == okay) {
              confirm =  true;
              temp.close();
          }
          else if (evtSource == cancel) {
              confirm = false;
              temp.close();
          }
          
        } //ends handle
    }  // ends handler  
                
    private void doQuit() {
        if(confirm("Quit?")) {
            String nextSource;
            String nextDest;
            String nextFormat;
            String nextTarget;
            if (defaultSourceChange) nextSource = from.getText();
            else nextSource = DEFAULTPROPS.getSource();
            if (defaultDestChange) nextDest = to.getText();
            else nextDest = DEFAULTPROPS.getDest();
            if (defaultTargetChange) nextTarget = toRemove.getText();
            else nextTarget = DEFAULTPROPS.getTarget();
            if (defaultFormatChange) nextFormat = type.getText();
            else nextFormat = DEFAULTPROPS.getFormat();
            try{
                //format: source x
                // destination y
            PROPERTIES = new File(System.getProperty("user.home") + "\\" + PROPERTYNAME);            
            FileWriter fW = new FileWriter(PROPERTIES);
            fW.write(sourceHash + " " + nextSource + "\r\n");
            fW.write(destHash + " " + nextDest+ "\r\n");
            fW.write(targetHash + " "+ nextTarget + "\r\n");
            fW.write(formatHash + " " + nextFormat  + "\r\n");
            fW.flush();
            fW.close();
            stage.close();
            Platform.exit();
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
       
    public boolean validate(File f)
    { 
        if ((!f.exists() || !f.canRead())) {
            status.setText("Check input directories; error.");
            return false;
        }
        return true;
    }
        
    private boolean confirm(String in)
    {
      temp= new Stage();  
      Label dummy = new Label(in);
      okay = new Button("OK");
      okay.setOnAction(h);
      okay.setDefaultButton(true);
      cancel = new Button("Cancel");
      cancel.setCancelButton(true);
      cancel.setOnAction(h);
      FlowPane fP = new FlowPane();
      fP.setPadding(new Insets(5,5,5,5));
      fP.getChildren().addAll(dummy, okay, cancel);
      Scene options = new Scene(fP, 200, 50);
      temp.setScene(options);
      temp.showAndWait();
      return confirm;
    }
    //Could need to be set by other components
    protected void setRunning(boolean whether)
    {
        this.running = whether;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
      
  //arms or disarms interface as specified
  private void arm(boolean whether)
  {
   if (whether)
   {
       browseSource.arm();
       browseDest.arm();
       quit.arm();
       go.arm();
       toRemove.setEditable(true);
       to.setEditable(true);
       from.setEditable(true);
       type.setEditable(true);
       setDefaultSource.arm();
       setDefaultDest.arm();
   }
   else {
       quit.disarm();
       go.disarm();
       browseSource.disarm();
       browseDest.disarm();
       setDefaultSource.disarm();
       setDefaultDest.disarm();
       from.setEditable(false);
       to.setEditable(false);
       toRemove.setEditable(false);
       type.setEditable(false);
   }
  }
  private void showSummary()
  {  
    Stage s = new Stage();
    s.setAlwaysOnTop(true);
    LinkedList<String> notCopied = new LinkedList<>();
    FlowPane f = new FlowPane();
    f.setOrientation(Orientation.VERTICAL);
    boolean success = true;
    int size = 0;
    int longest = 50;
    for (ProcessService pS: services)
    {
        if (pS.getState() != Worker.State.SUCCEEDED)
        {
             success = false;
             for (File file: pS.getRemaining())
             {
                 String temp = file.getName();
                 notCopied.add(temp);
                 if(temp.length() > longest) longest = temp.length();
                 size++;
             }
        }
    }
    String successOutput = "Copied " + files + " files to " + destD;
    String failOutput = "Files not copied: \n";
    for (String string: notCopied) failOutput += string + "\n";
    TextArea summaryText; 
    if (success) summaryText = new TextArea(successOutput);
    else {
        summaryText = new TextArea(failOutput);
        summaryText.setPrefRowCount(size);
        summaryText.setPrefHeight(size*40);
        summaryText.setPrefColumnCount(longest);
    }
    summaryText.setEditable(false);
    Button b = new Button("Ok");
    b.setOnAction( e -> {
        s.close();
        arm(true);
        });
    f.setPadding(new Insets(20, 20, 20, 20));
    f.getChildren().addAll(summaryText, b);
    Scene sc;
    //really needs a  pane with a scrollbar
    if (size <=10) sc = new Scene(f, 200, 100+ (40*size));
    else sc= new Scene(f, 200, 400);
    s.setScene(sc);
    s.show();
}
  
}


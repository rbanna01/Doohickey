/* Move all to main class, just display on main window....
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doohickey;

import java.util.Timer;
import java.util.TimerTask;
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;




/**
 *
 * @author Ruaridhi Bannatyne
 */
public class ProgressPopup implements EventHandler{
    private final FlowPane fP;
    private final Button cancelButton;
    //number of active services
    private final short services;
    private final String TITLE = "Task progress";
    private final String PROMPT_TEXT = "Retain files already copied?";
    private final BarListener pBar;
    //total number of fles to be copied
    private double totalWork;
    //number of servies which have successfully terminated
    private Button yesButton;
    private Button noButton;
    private Button confirmCancelButton;
    //does this need services, or just the number of services?
    private final Timer t;
    private final Stage stage;
    private Doohickey app;
    private DoubleBinding dB;
    private short remaining;
    
    TimerTask tT = new TimerTask() {
       @Override
       public void run() 
       { //name input needs to change
           Platform.runLater( new Runnable() {
               @Override
                public void run() {
                dB.invalidate();
                }
           });
    } //ends run
       
   }; //ends t def 
    
    public ProgressPopup(DoubleBinding d, LinkedList<ProcessService> lIn)
    {
    dB = d;
    services = (short) lIn.size();
    remaining = services;
    for(ProcessService pS: lIn) pS.addEventHandler( WorkerStateEvent.ANY, this);
    stage = new Stage();           
    stage.setTitle(TITLE);
    cancelButton = new Button("Cancel");
    cancelButton.setCancelButton(true);
    cancelButton.setOnAction(e -> doQuit());
    pBar = new BarListener(dB);
    pBar.setProgress(0);
    dB.addListener(pBar);
    fP = new FlowPane(Orientation.VERTICAL);
    fP.setPadding(new Insets(15,15,15,15));
    fP.getChildren().addAll(pBar, cancelButton); //pBar
    Scene scene = new Scene(fP, 200, 50);
    stage.setScene(scene);
    stage.setAlwaysOnTop(true);
    t = new Timer(true);
    }
    
    public Stage getStage() { 
       t.schedule(tT, 0, 1000);    
       return stage;
    }

    //needs to account for cancellation or error too
    @Override
    public void handle(Event wSE)
    {
     //called when a service has completed; if all have completed, this closes.        
     EventType eT = wSE.getEventType();   
     if(eT == WorkerStateEvent.WORKER_STATE_SUCCEEDED 
             || eT == WorkerStateEvent.WORKER_STATE_CANCELLED
             || eT == WorkerStateEvent.WORKER_STATE_FAILED) { 
        remaining--;
     }  
     //System.out.println("Service terminated");
     if(remaining == 0)
     {
        t.cancel();
        stage.close();
     }
    }
    
    private void doQuit() {
        if(t!= null)t.cancel();
        stage.close();
    } //ends doQuit
/*
//needed?
protected void doUpdate(double progress)
{
    //pBar.setProgress(progress); 
    if(progress >= 100) {
        app.setRunning(false);
        stage.close();
    }
    //play success sound?
} //ends doUpdate */

//Used to monitor progress and update ProgressPopup. No longer needed?
    

} //ends ProgressPopup
    
/*
 * A ProgressBar which can be registered as a listener for
 * a DoubleBinding's invalidation; more self-sufficient than default class
 * 
 */
package doohickey;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author Ruaridhi Bannatyne
 */
class BarListener extends ProgressBar implements InvalidationListener {
    private final DoubleBinding dB; //the binding for which this is the listener
    
      public BarListener(DoubleBinding in)
      {
          this.dB = in;
      } //ends constructor
    
    //Sets this ProgrssBar's progress to the value of dB when the latter is invalidated
      @Override
      public void invalidated(Observable o)
      {
          this.setProgress(dB.getValue());          
      }
  } //ends BarListener
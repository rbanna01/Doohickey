/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doohickey;

/**
 *
 * @author Ruaridhi Bannatyne
 */
public class Props {
        private String source;
        private String dest;
        private String format;
        private String target;
        public Props() {}
        
        public String getSource() { return source; }
        public String getDest() { return dest;}
        public String getFormat() {return format;}
        public String getTarget() { return target;}
        
        public void setSource(String toAdd) { source = new String(toAdd);}
        public void setDest(String toAdd) { dest = new String(toAdd);}
        public void setTarget(String newTarget) { target = new String(newTarget); }
        public void setFormat(String newFormat) { format = new String(newFormat);}
        
} //ends props
        


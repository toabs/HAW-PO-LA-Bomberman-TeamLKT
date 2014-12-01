/**
 * 
 */
package klt;

import java.util.HashSet;
import java.util.Set;

import klt.util.Actions_E;

import org.rlcommunity.rlglue.codec.types.Observation;

/* ************************************************************** */
/**
 * @author ToRoSaR
 * 17.11.2014
 */
/* *********************************************************** */
public class ObservationWithActions extends Observation
{
    private Set<Actions_E> actions = new HashSet<Actions_E>();
    
    /* ************************************************************** */
    /**
     * ObservationWithActions
     * @param actions
    */ /************************************************************* */
    public ObservationWithActions(int numInts, int numDoubles)
    {
        super(numInts, numDoubles);
    }

    /* ************************************************************** */
    /**
     * addAction
     * @param action
    */ /************************************************************* */
    public void addAction(Actions_E action) {
        this.actions.add(action);
    }
    
    /* ************************************************************** */
    /**
     * getActions
     * @return
    */ /************************************************************* */
    public Set<Actions_E> getActions() {
        return actions;
    }

    /**
     * Modified the toString method to reduce the size of the observation string so they consume less memory in total
     * @return
     */
    @Override
    public String toString(){
        String raw = super.toString();
        return trimObservationString(raw);
    }

    public static String trimObservationString(String input){
        String[] temp = input.split("\n");
        String res = temp[temp.length-1].trim();
        return res;
    }
}

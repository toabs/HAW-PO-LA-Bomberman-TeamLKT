package klt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Tobi on 23.11.2014.
 *
 * A logging utility which is designed to log a number of q value changes.
 * The workflow is the following:
 * 1. For each change of the q values you add an element to the utility.
 * 2. Get the element you added and fill it with the relevant information
 * 3. When everything is added, let the utility print the changes on the stdout
 */
public class AgentLogUtil implements Iterable{
    private LinkedList<SarsaLogElement> lastLogObjects;
    private int maximumSize;
    private SarsaLogElement lastElem;
    private boolean sarsaLambdaMode = false;

    /**
     * You should always use this constructor!
     *
     * @param n_Logs        Maximum number of log entries.
     */
    public AgentLogUtil(int n_Logs){
        lastLogObjects = new LinkedList<>();
        this.maximumSize = n_Logs;
    }

    /**
     * This contructor is for the SarsaLambda logging utility only.
     * DO NOT USE THIS ONE!
     *
     * @param n_Logs        Maximum number of log entries.
     * @param sarsaLambdaMode
     */
    public AgentLogUtil(int n_Logs, boolean sarsaLambdaMode){
        this(n_Logs);
        this.sarsaLambdaMode = sarsaLambdaMode;
    }

    /**
     * Adds a new logging element to the queue.
     * If the queue reaches the maximum amount of entries it'll drop the oldest entry.
     *
     * @param elem      The element to add.
     * @return          True if the add was successful, false if not.
     */
    public boolean add(SarsaLogElement elem){
        if(lastLogObjects.size()+1 >= maximumSize){
            lastLogObjects.poll();
        }
        lastElem = elem;
        return lastLogObjects.add(elem);
    }

    /**
     * Adds a logging element with the given observation and action.
     *
     * @param obs       The observation
     * @param action    The action
     * @return          True if the add was successful, false if not.
     */
    public boolean add(String obs, int action){
        return add(new SarsaLogElement(obs, action));
    }

    /**
     * Returns the log entry with the given observation and action.
     *
     * @param obs       The observation
     * @param action    The action
     * @return          The logging element with the action and observation. Returns null if there is not such element.
     */
    public SarsaLogElement get(String obs, int action){
        int index = lastLogObjects.indexOf(new SarsaLogElement(obs, action));
        return index == -1 ? null : lastLogObjects.get(index);
    }

    /**
     * Returns an iterator
     *
     * @return  The iterator
     */
    @Override
    public Iterator iterator() {
        return lastLogObjects.iterator();
    }

    /**
     * Resets the utility
     */
    public void clear(){
        lastLogObjects = new LinkedList<>();
    }

    /**
     * Returns the last added element.
     *
     * @return      the last added element
     */
    public SarsaLogElement getLastElem(){
        return lastElem;
    }

    /**
     * Prints the information from all the elements
     *
     * @param storage       The observationStorage for this instance.
     */
    public void logLastQValueUodates(HashMap<String, HashMap<Integer, Double>> storage){
        if (sarsaLambdaMode){
            System.out.println("-----------------------------------------------------CHAIN START!-------------------------------------");
        }else {
            System.out.println("-----------------------------------------------------LOG START!---------------------------------------------------------------------");
        }
        for (SarsaLogElement elem : lastLogObjects){
            elem.logElem(storage, sarsaLambdaMode);
            System.out.println("------------------------------------------------------NEXT ENTRY!---------------------------------------------------------------");
        }
        if (sarsaLambdaMode){
            System.out.println("-------------------------------------------------CHAIN END!---------------------------------------------------");
        }else {
            System.out.println("-----------------------------------------LOG END!-------------------------------------------------");
        }
    }
}

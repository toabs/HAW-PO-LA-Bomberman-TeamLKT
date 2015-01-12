package klt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Tobi on 30.11.2014.
 *
 * This utility is used to log the q value changes in the Sarsa Lambda algorithm.
 * The Sarsa Lambda changes multiple entries with one reward, which changes the way to use this utility compared to the agentLogUtil.
 * You use this util the following way:
 * 1. Add a new chain for each time you update your values with a new reward.
 * 2. Add a new element for each q value you change to the chain.
 * 3. Retrieve the element and fill it with information.
 * 4. Log the changes.
 *
 * If you got problems, refer to the usage of this utility in the class AgentSarsaLambda.
 */
public class AgentLogUtilSLambda implements Iterable<AgentLogUtil>{

    private LinkedList<AgentLogUtil> lastUpdateChains;
    private int maxLogNumber;
    private int sarsaQueueLenght;
    private SarsaLogElement lastElem;
    private AgentLogUtil lastChain;

    /**
     * Constructor to create a new SarsaL logging utility.
     *
     * @param nLogs                 Amount of elements you want to log with each change
     * @param sarsaLQueueLength     Amount of changes you want to log
     */
    public AgentLogUtilSLambda(int nLogs, int sarsaLQueueLength) {
        this.maxLogNumber = nLogs;
        this.sarsaQueueLenght = sarsaLQueueLength;
        lastUpdateChains = new LinkedList<>();
    }

    /**
     * Adds a new loggingchain.
     *
     * @return      Retruns the loggingchain
     */
    public AgentLogUtil addNewLogChain(){
        AgentLogUtil elem = new AgentLogUtil(sarsaQueueLenght, true);
        if(lastUpdateChains.size()+1 >= maxLogNumber){
            lastUpdateChains.poll();
        }
        lastChain = elem;
        lastUpdateChains.add(elem);
        return elem;
    }

    /**
     * Adds an element to the last added loggingchain.
     *
     * @param elem      Element to add
     * @return
     */
    public boolean add(SarsaLogElement elem){
        lastElem = elem;
        return lastChain.add(elem);
    }

    /**
     * Adds a logging element with the given observation and action to the last added loggingchain.
     *
     * @param obs       The observation
     * @param action    The action
     * @return          True if the add was successful, false if not.
     */
    public boolean add(String obs, int action){
        return add(new SarsaLogElement(obs, action));
    }

    /**
     * Returns the last added element.
     *
     * @return      the last added element
     */
    public SarsaLogElement getLastElem() {
        return lastElem;
    }

    /**
     * Returns the last added chain.
     *
     * @return
     */
    public AgentLogUtil getLastChain() {
        return lastChain;
    }

    /**
     * Returns and iterator for the list of chains.
     *
     * @return
     */
    @Override
    public Iterator iterator() {
        return lastUpdateChains.iterator();
    }

    /**
     * Logs the changes from all chains with their elements.
     *
     * @param storage       the observationStorage for this instance
     */
    public void logLastQValueUpdates(HashMap<String, HashMap<Integer, Double>> storage) {
        System.out.println("-----------------------------------------LOG START!----------------------------------------");
        for (AgentLogUtil chain : this){
            chain.logLastQValueUodates(storage);
        }
        System.out.println("-----------------------------------------LOG END!------------------------------------------");
    }
}

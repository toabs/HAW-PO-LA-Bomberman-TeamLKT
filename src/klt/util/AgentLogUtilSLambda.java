package klt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Tobi on 30.11.2014.
 */
public class AgentLogUtilSLambda implements Iterable<AgentLogUtil>{

    private LinkedList<AgentLogUtil> lastUpdateChains;
    private int maxLogNumber;
    private int sarsaQueueLenght;
    private SarsaLogElement lastElem;
    private AgentLogUtil lastChain;

    public AgentLogUtilSLambda(int nLogs, int sarsaLQueueLength) {
        this.maxLogNumber = nLogs;
        this.sarsaQueueLenght = sarsaLQueueLength;
        lastUpdateChains = new LinkedList<>();
    }

    public AgentLogUtil addNewLogChain(){
        AgentLogUtil elem = new AgentLogUtil(sarsaQueueLenght, true);
        if(lastUpdateChains.size()+1 >= maxLogNumber){
            lastUpdateChains.poll();
        }
        lastChain = elem;
        lastUpdateChains.add(elem);
        return elem;
    }

    public boolean add(SarsaLogElement elem){
        lastElem = elem;
        return lastChain.add(elem);
    }

    public boolean add(String obs, int action){
        return add(new SarsaLogElement(obs, action));
    }

    public SarsaLogElement getLastElem() {
        return lastElem;
    }

    public AgentLogUtil getLastChain() {
        return lastChain;
    }

    @Override
    public Iterator iterator() {
        return lastUpdateChains.iterator();
    }

    public void logLastQValueUodates(HashMap<String, HashMap<Integer, Double>> storage) {
        System.out.println("-----------------------------------------LOG START!----------------------------------------");
        for (AgentLogUtil chain : this){
            chain.logLastQValueUodates(storage);
        }
        System.out.println("-----------------------------------------LOG END!------------------------------------------");
    }
}

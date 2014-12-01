package klt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Tobi on 23.11.2014.
 */
public class AgentLogUtil implements Iterable{
    private LinkedList<SarsaLogElement> lastLogObjects;
    private int maximumSize;
    private SarsaLogElement lastElem;
    private boolean sarsaLambdaMode = false;

    public AgentLogUtil(int n_Logs){
        lastLogObjects = new LinkedList<>();
        this.maximumSize = n_Logs;
    }

    public AgentLogUtil(int n_Logs, boolean sarsaLambdaMode){
        this(n_Logs);
        this.sarsaLambdaMode = sarsaLambdaMode;
    }

    public boolean add(SarsaLogElement elem){
        if(lastLogObjects.size()+1 >= maximumSize){
            lastLogObjects.poll();
        }
        lastElem = elem;
        return lastLogObjects.add(elem);
    }

    public boolean add(String obs, int action){
        return add(new SarsaLogElement(obs, action));
    }

    public SarsaLogElement get(String obs, int action){
        return lastLogObjects.get(lastLogObjects.indexOf(new SarsaLogElement(obs, action)));
    }

    @Override
    public Iterator iterator() {
        return lastLogObjects.iterator();
    }

    public void clear(){
        lastLogObjects = new LinkedList<>();
    }

    public SarsaLogElement getLastElem(){
        return lastElem;
    }

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

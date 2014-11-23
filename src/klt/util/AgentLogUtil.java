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

    public AgentLogUtil(int n_Logs){
        lastLogObjects = new LinkedList<>();
        this.maximumSize = n_Logs;
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
        System.out.println("-----------------------------------------------------LOG START!---------------------------------------------------------------------");
        for (SarsaLogElement elem : lastLogObjects){
            elem.logElem(storage);
            System.out.println("------------------------------------------------------NEXT ENTRY!---------------------------------------------------------------");
        }
        System.out.println("-----------------------------------------LOG END!-------------------------------------------------");
    }
}

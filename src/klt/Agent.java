/**
 * 
 */
package klt;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
public abstract class Agent implements AgentInterface
{
    //The set of observation, saving a Map of Values indexed by the Action
    //Example 
    protected HashMap<String, HashMap<Integer, Double>> observationStorage; 
    private String saveFilePath;
    private DebugState debugState;
    protected int NUMBEROFACTIONS = 6;
    protected double INITIALQVALUE = 50.0;
    protected Random randGenerator = new Random();

    Agent(String saveFilePath, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath);
        this.debugState = debugState;
    }
    
    @SuppressWarnings("unchecked")
    Agent(String saveFilePath) throws IOException, ClassNotFoundException
    {
        this.debugState = DebugState.NO_DEBUG;
        this.saveFilePath = saveFilePath;
        
        File f = new File(saveFilePath);
        
        if (f.exists() && !f.isDirectory()) 
        {
            FileInputStream fin = new FileInputStream(saveFilePath);
            ObjectInputStream ois = new ObjectInputStream(fin);
            this.observationStorage = (HashMap<String, HashMap<Integer, Double>>) ois.readObject();
            ois.close();
        }
        else
        {
            this.observationStorage = new HashMap<String, HashMap<Integer, Double>>();
        }
    }

    /* ************************************************************** */
    /**
     * getBestAction
     * @param currentObs
     * @return
     */ /************************************************************* */
    protected int getBestAction(Observation currentObs, int actionCount)
    {
        int currentAction = 0;
//        Double bestValue = -1 * Double.MAX_VALUE;
        Double bestValue = -9999999.0;
        ArrayList<Integer> bestActions = new ArrayList<Integer>();
        int bestActionCount = 0;

        if (this.observationStorage.containsKey(currentObs.toString()))
        {
            //determine highest value
            for(int i = 0; i < actionCount; i++)
            {
            	agentLogln("Reward["+i+"]:" + this.observationStorage.get(currentObs.toString()).get(i));
                if (this.observationStorage.get(currentObs.toString()).get(i) > bestValue)
                {                	
                    bestValue = this.observationStorage.get(currentObs.toString()).get(i);
                }
            }
            
            agentLogln("bestValue:" + bestValue);
            
            //get Actions with that value (must be at least one)
            for(int i = 0; i < actionCount; i++)
            {
                if (this.observationStorage.get(currentObs.toString()).get(i).equals(bestValue))
                {
                    bestActions.add(i);
                }
            }
            
            bestActionCount = bestActions.size();
            agentLogln("bestActionCount:" + bestActionCount);

            if (bestActionCount <= 1)
            {
                currentAction = bestActions.get(0);
            }
            else
            {
                //there is more than one best action
                currentAction = bestActions.get(this.randGenerator.nextInt(bestActionCount));
            }
        }
        else
        {
            currentAction = this.randGenerator.nextInt(actionCount);
        }

        agentLogln("Action chosen:" + currentAction);
        return currentAction;
    }

    protected void agentLogln(String output){
        if (debugState.getAgentDebugState()){
            System.out.println(output);
        }
    }

    protected void agentLog(String output){
        if (debugState.getAgentDebugState()){
            System.out.print(output);
        }
    }

    protected void setRewardForActionObservation(double reward, String observation, int action){
        if (observationStorage.containsKey(observation)) {

            observationStorage.get(observation).put(action, reward);

        } else {
            //add the unknown observation
            observationStorage.put(observation, new HashMap<Integer, Double>());

            for (int i = 0; i < NUMBEROFACTIONS; i++) {
                observationStorage.get(observation).put(i, (i == action) ? reward : INITIALQVALUE);
            }
        }
    }

    protected void fillInUnknownObservations(String observation){
        //add the unknown observation
        observationStorage.put(observation, new HashMap<Integer, Double>());

        for (int i = 0; i < NUMBEROFACTIONS; i++) {
            observationStorage.get(observation).put(i, INITIALQVALUE);
        }
    }

    /* ************************************************************** */
    /**
     * agent_cleanup
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_cleanup()
    */ /************************************************************* */
    @Override
    public void agent_cleanup()
    {    	
        this.agentLogln("Cleanup Called");

    }

	public void agent_exit() {
		this.agentLogln("Exit Called");
		//save progress
        try
        {
            FileOutputStream fout = new FileOutputStream(saveFilePath);
            ObjectOutputStream oos = new ObjectOutputStream(fout);        
            oos.writeObject(observationStorage);
            oos.close();
        } catch (IOException e)
        {
            System.out.println("Error saving observationStorage: " + e.getMessage());
        }
	}
}

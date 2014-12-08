/**
 * 
 */
package klt;

import klt.util.Actions_E;
import klt.util.DebugState;
import klt.util.SaveDataUtility;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.*;

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
    protected DebugState debugState;
    protected double INITIALQVALUE = 50.0;
    protected Random randGenerator = new Random();
    protected int maxRandomAction = 5;
    protected int ActionsCount = 6;

    Agent(String saveFilePath, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath);
        this.debugState = debugState;
    }

    Agent(String saveFilePath) throws IOException, ClassNotFoundException
    {
        this.debugState = DebugState.NO_DEBUG;
        this.saveFilePath = saveFilePath;

//        File f = new File(saveFilePath);
//
//        if (f.exists() && !f.isDirectory())
//        {
//            FileInputStream fin = new FileInputStream(saveFilePath);
//            GZIPInputStream gzip = new GZIPInputStream(fin);
//            ObjectInputStream ois = new ObjectInputStream(gzip);
//            this.observationStorage = (HashMap<String, HashMap<Integer, Double>>) ois.readObject();
//            ois.close();
//        }
//        else
//        {
//            this.observationStorage = new HashMap<String, HashMap<Integer, Double>>();
//        }
        this.observationStorage = SaveDataUtility.loadCompressedStorage(saveFilePath);
        System.out.println("Storage:" + this.observationStorage.size());
    }

    /* ************************************************************** */
    /**
     * getBestAction
     * @param currentObs
     * @return
     */ /************************************************************* */
    protected int getBestAction(Observation currentObs, Set<Actions_E> allowedActions)
    {
        Iterator<Actions_E> actions = null;
        int currentAction = 0;
        Double bestValue = -9999999.0;
        ArrayList<Integer> bestActions = new ArrayList<Integer>();
        int bestActionCount = 0;

        if (this.observationStorage.containsKey(currentObs.toString()) && (allowedActions.size() > 0))
        {

            //determine highest value
            for(Actions_E action : allowedActions){
            	agentLogln("Reward["+action.ordinal()+"]:" + this.observationStorage.get(currentObs.toString()).get(action.ordinal()));
                if (this.observationStorage.get(currentObs.toString()).get(action.ordinal()) > bestValue)
                {                	
                    bestValue = this.observationStorage.get(currentObs.toString()).get(action.ordinal());
                }
            }
            
            agentLogln("bestValue:" + bestValue);

            //get Actions with that value (must be at least one)
            for(Actions_E action : allowedActions){
                if (this.observationStorage.get(currentObs.toString()).get(action.ordinal()).equals(bestValue))
                {
                    bestActions.add(action.ordinal());
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
            Actions_E[] actionArray =  allowedActions.toArray(new Actions_E[0]);
            if (actionArray.length <= 0) {
                this.agentLogln("No possible Action! -> Stay");
            } else {
                int randomActionIndex = this.randGenerator.nextInt(actionArray.length);
                currentAction = actionArray[randomActionIndex].ordinal();
            }
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

    protected void setRewardForActionObservation(double reward, String observation, int action, Set<Actions_E> allowedActions){
        if (observationStorage.containsKey(observation)) {
            observationStorage.get(observation).put(action, reward);
        } else {
            //add the unknown observation
            observationStorage.put(observation, new HashMap<Integer, Double>());

            for(int i = 0; i < Actions_E.getActionCount(); i++) {
                observationStorage.get(observation).put(i, (i == action) ? reward : INITIALQVALUE);
            }
        }
    }
    

    protected void fillInUnknownObservations(String observation){
        //add the unknown observation
        observationStorage.put(observation, new HashMap<Integer, Double>());
        
        for(int i = 0; i < Actions_E.getActionCount(); i++) {
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
        System.out.println("Exit Called");
        System.out.println("Storage contains: " + observationStorage.size() + " Observations");
        //save progress
        SaveDataUtility.writeCompressedStorage(observationStorage, saveFilePath);
	}
	
	protected double getMaxRewardForObs(String observation) {
	    double result = Double.MIN_VALUE;
	    
	    HashMap<Integer, Double> qValues = this.observationStorage.get(observation.toString());
	    
	    if (qValues == null) {
	        //return InitialValue if observation has not been stored
	        return INITIALQVALUE;
	    } else {	        
    	    for(Double reward : qValues.values()) {
    	        result = (reward > result) ? reward : result;
    	    }
	    }
	    
	    return result;
	}
}

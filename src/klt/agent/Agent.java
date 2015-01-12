package klt.agent;

import klt.util.Actions_E;
import klt.util.DebugState;
import klt.util.SaveDataUtility;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.*;

/**
 * @author LarsE
 * 13.10.2014
 *
 * This is the abstract agent class all other agents base on. Here we implement the AgentInterface from RLGlue.
 * Also this class provide the basic mathods all our agents are using including a way to handle the observationStorage.
 */
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

    /**
     * Creates an observasionStorage, saves the location where to write it afterwards and saves the debugState for this instance of the agent.
     *
     * @param saveFilePath      The Path where to save the file.
     * @param debugState        The debug state for this instance.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Agent(String saveFilePath, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath);
        this.debugState = debugState;
    }

    /**
     * Creates an observationStorage and saves where to save it to.
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Agent(String saveFilePath) throws IOException, ClassNotFoundException
    {
        this.debugState = DebugState.NO_DEBUG;
        this.saveFilePath = saveFilePath;
        this.observationStorage = SaveDataUtility.loadCompressedStorage(saveFilePath);
    }

    /**
     * Returns the best action for the current observation.
     * If there is more than one observation with the highest value, the action to be returned is random.
     *
     * @param currentObs        The current observation.
     * @param allowedActions    Allowed actions for this observation.
     * @return  One of the allowed actions with the highest value.
     */
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
            Actions_E[] actionArray =  allowedActions.toArray(new Actions_E[allowedActions.size()]);
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

    /**
     * A logging method for the agents. It only prints on stdout when the debugState is turned true for agents!
     *
     * @param output        What to print.
     */
    protected void agentLogln(String output){
        if (debugState.getAgentDebugState()){
            System.out.println(output);
        }
    }

    /**
     * A logging method for the agents. It only prints on stdout when the debugState is turned true for agents!
     *
     * @param output        What to print.
     */
    protected void agentLog(String output){
        if (debugState.getAgentDebugState()){
            System.out.print(output);
        }
    }

    /**
     * Sets the reward of a given action in a given observation.
     * If the observation doesn't exist yet, it'll be created with the INITIALQVALUE.
     *
     * @param reward        The reward.
     * @param observation   The observation.
     * @param action        The action.
     */
    protected void setRewardForActionObservation(double reward, String observation, int action){
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

    /**
     * Adds an observation to the observationStorage.
     *
     * @param observation       The observation to add.
     */
    protected void fillInUnknownObservations(String observation){
        if(!observationStorage.containsKey(observation)) {
            //add the unknown observation
            observationStorage.put(observation, new HashMap<Integer, Double>());

            for (int i = 0; i < Actions_E.getActionCount(); i++) {
                observationStorage.get(observation).put(i, INITIALQVALUE);
            }
        }
    }

    /**
     * This method is not used in our implementation.
     * agent_cleanup
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_cleanup()
    */
    @Override
    public void agent_cleanup()
    {    	
        this.agentLogln("Cleanup Called");

    }

    /**
     * This method is called at the end of the experiment to save all generated data.
     * You need to call this if you want to save your progress!
     */
	public void agent_exit() {
        System.out.println("Exit Called");
        System.out.println("Storage contains: " + observationStorage.size() + " Observations");
        //save progress
        SaveDataUtility.writeCompressedStorage(observationStorage, saveFilePath);
	}

    /**
     * Returns the maximum reward for the given observation.
     *
     * @param observation       The observation to search for.
     * @return          The maximum reward.
     */
	protected double getMaxRewardForObs(String observation) {
	    double result = Double.MIN_VALUE;
	    
	    HashMap<Integer, Double> qValues = this.observationStorage.get(observation);
	    
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

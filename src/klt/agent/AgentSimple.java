package klt.agent;

import klt.ObservationWithActions;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author LarsE
 * 19.10.2014
 *
 * This is a very simple agent which is taking the way with highest reward (greedy).
 */
public class AgentSimple extends Agent
{    
    Integer lastAction;
    Observation lastObs;
    int NUMBEROFACTIONS = 5;
    
    /**
     * Agent_Follower
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
    */
public AgentSimple(String saveFilePath, DebugState debugState) throws IOException,
            ClassNotFoundException
    {
        super(saveFilePath, debugState);
    }

    /**
     * This method is called at the end of a round.
     * It is used to take the reward for the last action into account.
     *
     * @param v         The reward for the last action.
     */
    @Override
    public void agent_end(double v)
    {
        agentLogln("agent_end called!");
        
    }

    /**
     * agent_init is not used in this version.
     * @param arg0
     */
    @Override
    public void agent_init(String arg0)
    {
        agentLogln("agent_init called!");
        
    }

    /**
     * Not used.
     * @param arg0
     * @return
     */
    @Override
    public String agent_message(String arg0)
    {
        agentLogln("agent_message called!");
        return null;
    }

    /**
     * This method is called at the start of a round.
     * It returns an action with the highest reward for the given action.
     *
     * @param arg0       The observation.
     * @return      The action
     */
    @Override
    public Action agent_start(Observation arg0)
    {
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(arg0, ((ObservationWithActions) arg0).getActions());
        
        lastObs = arg0;
        lastAction = returnAction.intArray[0];
        
        return returnAction;
    }


    /**
     * This method is called each step and returns an action with the highest reward for the current observation.
     * Also it calculates a new reward for the action in the last step with the reward.
     *
     * @param arg0             The reward for the last action.
     * @param arg1   The current observation after the last action.
     * @return              The next action.
     */
    @Override
    public Action agent_step(double arg0, Observation arg1)
    {
    	this.agentLogln("LastReward: " + arg0);
    	
        //distribute reward
        if (this.observationStorage.containsKey(lastObs.toString()))
        {
            this.observationStorage.get(lastObs.toString()).put(lastAction, arg0);
        }
        else
        {
            //add the unknown observation
            this.observationStorage.put(lastObs.toString(), new HashMap<Integer, Double>());
            
            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.observationStorage.get(lastObs.toString()).put(i, (i == lastAction) ? arg0 : 0);
            }
        }
        
        //calculate next action
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(arg1, ((ObservationWithActions) arg1).getActions());
        
        lastObs = arg1;
        lastAction = returnAction.intArray[0];
        
        return returnAction;
    }

}

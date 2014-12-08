/**
 * 
 */
package klt.agent;

import klt.ObservationWithActions;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.HashMap;

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class Agent_Simple extends Agent
{    
    Integer lastAction;
    Observation lastObs;
    int NUMBEROFACTIONS = 5;
    
    /* ************************************************************** */
    /**
     * Agent_Follower
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
    */ /************************************************************* */
public Agent_Simple(String saveFilePath, DebugState debugState) throws IOException,
            ClassNotFoundException
    {
        super(saveFilePath, debugState);
    }

    /* ************************************************************** */
    /**
     * agent_end
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_end(double)
    */ /************************************************************* */
    @Override
    public void agent_end(double arg0)
    {
        // TODO Auto-generated method stub
        
    }

    /* ************************************************************** */
    /**
     * agent_init
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_init(java.lang.String)
    */ /************************************************************* */
    @Override
    public void agent_init(String arg0)
    {
        // TODO Auto-generated method stub
        
    }

    /* ************************************************************** */
    /**
     * agent_message
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_message(java.lang.String)
    */ /************************************************************* */
    @Override
    public String agent_message(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* ************************************************************** */
    /**
     * agent_start
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_start(org.rlcommunity.rlglue.codec.types.Observation)
    */ /************************************************************* */
    @Override
    public Action agent_start(Observation arg0)
    {
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(arg0, ((ObservationWithActions) arg0).getActions());
        
        lastObs = arg0;
        lastAction = returnAction.intArray[0];
        
        return returnAction;
    }


    /* ************************************************************** */
    /**
     * agent_step
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_step(double, org.rlcommunity.rlglue.codec.types.Observation)
    */ /************************************************************* */
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

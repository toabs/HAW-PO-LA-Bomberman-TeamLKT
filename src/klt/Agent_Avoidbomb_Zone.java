/**
 * 
 */
package klt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class Agent_Avoidbomb_Zone extends Agent
{    
    Integer lastAction;
    Observation lastObs;
    /* ************************************************************** */
    /**
     * Agent_Follower
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
    */ /************************************************************* */
    Agent_Avoidbomb_Zone(String saveFilePath) throws IOException,
            ClassNotFoundException
    {
        super(saveFilePath);
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
        returnAction.intArray[0] = this.getBestAction(arg0);
        
        lastObs = arg0;
        lastAction = returnAction.intArray[0];
        
        return returnAction;
    }
    
    /* ************************************************************** */
    /**
     * getBestAction
     * @param currentObs
     * @return
    */ /************************************************************* */
    private int getBestAction(Observation currentObs)
    {
        int currentAction = 0;
        int bestValue = -9999999;
        ArrayList<Integer> bestActions = new ArrayList<Integer>();
        int bestActionCount = 0;
        
        if (this.observationStorage.containsKey(currentObs.toString()))
        {
            //determine highest value
            for(int i = 0; i < 5; i++)
            { 
                if (this.observationStorage.get(currentObs.toString()).get(i) > bestValue)
                {
                    bestValue = this.observationStorage.get(currentObs.toString()).get(i);
                }
            }
            
            //get Actions with that value (must be at least one)
            for(int i = 0; i < 5; i++)
            { 
                if (this.observationStorage.get(currentObs.toString()).get(i) == bestValue)
                {
                    bestActions.add(i);
                }
            }            
            
            bestActionCount = bestActions.size();
            
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
            currentAction = this.randGenerator.nextInt(5);
        }
        
        return currentAction;
    }

    /* ************************************************************** */
    /**
     * agent_step
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_step(double, org.rlcommunity.rlglue.codec.types.Observation)
    */ /************************************************************* */
    @Override
    public Action agent_step(double arg0, Observation arg1)
    {
        //distribute reward
        if (this.observationStorage.containsKey(lastObs.toString()))
        {
            this.observationStorage.get(lastObs.toString()).put(lastAction, new Integer((int) arg0));
        }
        else
        {
            //add the unknown observation
            this.observationStorage.put(lastObs.toString(), new HashMap<Integer, Integer>());
            
            for(int i = 0; i < 5; i++)
            {
                this.observationStorage.get(lastObs.toString()).put(i, new Integer((i == lastAction) ? (int) arg0 : 0));
            }
        }
        
        //calculate next action
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(arg1);
        
        lastObs = arg1;
        lastAction = returnAction.intArray[0];
        
        return returnAction;
    }

}

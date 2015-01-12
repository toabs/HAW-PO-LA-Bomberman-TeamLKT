/**
 * 
 */
package klt.agent;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;

/**
 * @author LarsE
 * 13.10.2014
 *
 * A small agent which chooses it's actions randomly.
 */
public class AgentRandom extends Agent
{
    Action lastAction;
    Observation lastObservation;
    
    /* ************************************************************** */
    /**
     * AgentRandom
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
    */ /************************************************************* */
public AgentRandom(String saveFilePath) throws IOException,
            ClassNotFoundException
    {
        super(saveFilePath);
    }

    /**
     * This method is called at the start of a round.
     * It returns an action with the highest reward for the given action.
     *
     * @param observation       The observation.
     * @return      The action
     */
    @Override
    public Action agent_start(Observation observation)
    {
        //Random action
        int randomAction = randGenerator.nextInt(5);
        
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = randomAction;
        
        return returnAction;
    }

    /**
     * This method is called each step and returns an action with the highest reward for the current observation.
     * Also it calculates a new reward for the action in the last step with the reward.
     *
     * @param v             The reward for the last action.
     * @param observation   The current observation after the last action.
     * @return              The next action.
     */
    @Override
    public Action agent_step(double v, Observation observation)
    {
        //Random action
        int randomAction = randGenerator.nextInt(6);
        
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = randomAction;
        
        return returnAction;
    }
    
    /* ************************************************************** */
    /**
     * agent_cleanup
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_cleanup()
    */ /************************************************************* */
    @Override
    public void agent_cleanup()
    {
        System.out.println("agent cleanup");
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
        System.out.println("agent_end");
    }

    /**
     * agent_init is not used in this algorithm.
     * @param arg0
     */
    @Override
    public void agent_init(String arg0)
    {
        System.out.println("agent_init");
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
}

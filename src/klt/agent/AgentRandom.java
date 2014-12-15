/**
 * 
 */
package klt.agent;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
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
    
    /* ************************************************************** */
    /**
     * agent_start
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_start(org.rlcommunity.rlglue.codec.types.Observation)
    */ /************************************************************* */
    @Override
    public Action agent_start(Observation arg0)
    {
        //Random action
        int randomAction = randGenerator.nextInt(5);
        
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = randomAction;
        
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
        // TODO Auto-generated method stub        
    }

    /* ************************************************************** */
    /**
     * agent_end
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_end(double)
    */ /************************************************************* */
    @Override
    public void agent_end(double arg0)
    {
        System.out.println("agent_end");
    }

    /* ************************************************************** */
    /**
     * agent_init
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_init(java.lang.String)
    */ /************************************************************* */
    @Override
    public void agent_init(String arg0)
    {
        System.out.println("agent_init");
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
}

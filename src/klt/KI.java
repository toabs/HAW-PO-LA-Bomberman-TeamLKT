/**
 * 
 */
package klt;

import org.rlcommunity.rlglue.codec.LocalGlue;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.types.Observation_action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;

import Core.Playboard;
import Core.User;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
public class KI extends User
{
    private boolean firstStep = true;
    private Environment currentEnvironment;
    
    /* ************************************************************** */
    /**
     * LerningKI
     * @param id
    */ /************************************************************* */
    public KI(int id, Agent agent, Environment environment)
    {
        super(id);
       
        currentEnvironment = environment;
        
        LocalGlue localGlueImplementation=new LocalGlue(environment,agent);
        RLGlue.setGlue(localGlueImplementation);
    }

    /* ************************************************************** */
    /**
     * getAction
     * @see Core.User#getAction(Core.Playboard)
     */
    /************************************************************* */
    @Override
    public int getAction(Playboard playboard)
    {
        Observation_action firstResponse = null;
        Reward_observation_action_terminal stepResponse = null;
        
        int action = 0;
        
        currentEnvironment.setPlayboard(playboard, this.getId());
        
        if (firstStep) 
        {            
            RLGlue.RL_init();
            firstResponse = RLGlue.RL_start();
            action = firstResponse.a.intArray[0];
            firstStep = false;
        } 
        else
        {
            stepResponse = RLGlue.RL_step();
            action = stepResponse.a.intArray[0];
        }
        
        return action;
    }
    
    /* ************************************************************** */
    /**
     * resetMove
     * @see Core.User#resetMove()
    */ /************************************************************* */
    @Override
    public void resetMove()
    {
        // TODO Auto-generated method stub        
    }

    /* ************************************************************** */
    /**
     * gameOver
     * @see Core.User#gameOver(boolean)
    */ /************************************************************* */
    @Override
    public void gameOver(boolean won)
    {
        //not used by KI
        RLGlue.RL_cleanup();
        firstStep = true;     
    }
}

/**
 * 
 */
package klt;

import Core.Player;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class Environment_Avoidbomb_Zone extends Environment
{
    private final int freeDirections = (int) Math.pow(2, 4); //4 direction, blocked, not blocked
    private final int bombSituations = 125;
    private final int numIntegers = 2;
    private final int numDoubles = 0;
    
    /* ************************************************************** */
    /**
     * Environment_Fighter
     * @param debugState
    */ /************************************************************* */
    Environment_Avoidbomb_Zone(DebugState debugState) {
        super(debugState);
     }
    
    /* ************************************************************** */
    /**
     * env_cleanup
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
    */ /************************************************************* */
    @Override
    public void env_cleanup()
    {
        this.environmentLogln("Env_cleanup called!");        
    }

    /* ************************************************************** */
    /**
     * env_init
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_init()
    */ /************************************************************* */
    @Override
    public String env_init()
    {       
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 4)); //five possible actions (without bomb-planting)
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, freeDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, bombSituations));
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setRewardRange(new DoubleRange(-20, 20));
        
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;
    }

    /* ************************************************************** */
    /**
     * env_message
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_message(java.lang.String)
    */ /************************************************************* */
    @Override
    public String env_message(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* ************************************************************** */
    /**
     * env_start
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_start()
    */ /************************************************************* */
    @Override
    public Observation env_start()
    {
        Player currentPlayer = determineCurrentPlayer();
        
        int freeDirection = this.determinefreeDirections();
        int bombSituation = determineBombSituation();
        
        Observation result = new Observation(numIntegers, numDoubles);
        result.intArray[0] = freeDirection;
        result.intArray[1] = bombSituation;
        
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();
        
        return result;
    }

    /* ************************************************************** */
    /**
     * env_step
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_step(org.rlcommunity.rlglue.codec.types.Action)
    */ /************************************************************* */
    @Override
    public Reward_observation_terminal env_step(Action arg0)
    {
        double theReward=0.0d;
        boolean episodeOver = false;
        Player currentPlayer = determineCurrentPlayer();
        
        int freeDirection = this.determinefreeDirections();
        int bombSituation = determineBombSituation();
        
        Observation currentObs = new Observation(numIntegers, numDoubles);
        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = bombSituation;
        
        //high negative reward if moved against a wall or bomb
        if (lastX == currentPlayer.getX() && lastY == currentPlayer.getY() && arg0.intArray[0] != 0)
        {
            theReward = -500;
        } 
        else
        {
            if (currentDanger < lastDanger)
            {
                theReward = 100;
            }
        }
        /*
        if (currentDanger == 0 && lastDanger == 0) {
            //reward if staying in place
            if (arg0.intArray[0] == 0) {
                theReward = 50;
            }
            else {
                theReward = -50;
            }
        }
        else
        {
            if (arg0.intArray[0] == 0 && lastDanger != 0) {
                theReward = - 100;
            }
            else
            {
                if (currentDanger < lastDanger && lastDanger != 0)
                {
                    theReward = 100;
                }
                else 
                {
                    theReward = 50;
                }
            }
        }
        */
        
       
        
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();
        
        System.out.println("reward:" + theReward);
        System.out.println("cd:" + currentDanger);
        
        return new Reward_observation_terminal(theReward,currentObs,episodeOver);
    }
}

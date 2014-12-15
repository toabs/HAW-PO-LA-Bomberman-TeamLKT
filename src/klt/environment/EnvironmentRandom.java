/**
 * 
 */
package klt.environment;

import Core.Playboard;
import Core.Player;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import java.util.Iterator;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
public class EnvironmentRandom extends Environment
{
    private int playerID;
    private Playboard board;
    private int boardX;
    private int boardY;
    private DebugState debugState;

    public EnvironmentRandom(DebugState debugState) {
        this.debugState = debugState;
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
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 5));
        int fieldcount = 25; //random number, to be replaced
        theTaskSpecObject.addDiscreteObservation(new IntRange(0, fieldcount));
        theTaskSpecObject.addDiscreteObservation(new IntRange(0, 8));
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setRewardRange(new DoubleRange(-1, 1));
        
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;
    }
    
    /* ************************************************************** */
    /**
     * env_start
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_start()
    */ /************************************************************* */
    @Override
    public Observation env_start()
    {
        Observation returnObservation=new Observation(1,0,0);
        returnObservation.intArray[0]=0;
        return returnObservation;
    }

    /* ************************************************************** */
    /**
     * env_step
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_step(org.rlcommunity.rlglue.codec.types.Action)
    */ /************************************************************* */
    @Override
    public Reward_observation_terminal env_step(Action arg0)
    {
        boolean episodeOver=false;
        double theReward=0.0d;
        
        Iterator<Player> it = board.getPlayers().iterator();

        Player thisplayer = null;
        while(it.hasNext())
        {
            thisplayer = it.next();
            if(thisplayer.getId() == playerID)
                break;
        }
       
        episodeOver = !thisplayer.isAlive();

        Observation returnObservation=new Observation(1,0,0);
        returnObservation.intArray[0]=0;

        return new Reward_observation_terminal(theReward,returnObservation,episodeOver);
    }   
    
    /* ************************************************************** */
    /**
     * env_cleanup
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
    */ /************************************************************* */
    @Override
    public void env_cleanup()
    {
        environmentLogln("env_cleanup called!", debugState);
    }

    /* ************************************************************** */
    /**
     * env_message
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_message(java.lang.String)
    */ /************************************************************* */
    @Override
    public String env_message(String arg0)
    {
        environmentLogln("env_message called!", debugState);
        return null;
    }

    @Override
    public void setPlayboard(Playboard playboard, int userID) {
        this.playerID = userID;
        this.board = playboard;
        boardX = board.getBoard().length;
        boardY = board.getBoard()[0].length;
    }
}

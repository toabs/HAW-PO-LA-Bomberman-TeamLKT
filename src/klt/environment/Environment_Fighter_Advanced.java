/**
 * 
 */
package klt.environment;

import klt.ObservationWithActions;
import klt.util.Actions_E;
import Core.Player;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/* ************************************************************** */
/** Advances ist erweitert um Abstand und Richtung zum Rand
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class Environment_Fighter_Advanced extends Environment
{    
    protected int numberOfSpacesToEdge = 0;
    protected final int numIntegers = 5;
    protected int bombSituation = 0;
    
	public Environment_Fighter_Advanced(DebugState debugState) {
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
        maxDistanceToOpponent = Math.sqrt(Math.pow(board.getBoard().length, 2) + Math.pow(board.getBoard()[0].length, 2));
        int tempMax = (board.getBoard().length > board.getBoard()[0].length) ? board.getBoard().length : board.getBoard()[0].length;
        numberOfSpacesToEdge = tempMax / 2;
        this.environmentLogln("maxDistance is : " + maxDistanceToOpponent);
        
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 5)); //five possible actions (without bomb-planting)
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, freeDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, oppenentDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, bombSituations));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, numberOfSpacesToEdge));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(0, maxDistanceToOpponent, board.getBoard().length*board.getBoard()[0].length));
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
        
        int lastBombSituation = bombSituation;
        
        int freeDirection = this.determinefreeDirections();
        int playerOnBomb = this.determinePlayerOnBomb();
        int opponentDirection = this.determineOppenentDirection();
        this.bombSituation = this.determineBombSituation();
        int spacesToEdge = this.determineSpacesToEdge();
        double distanceToOpponent = this.determineDistanceToOpponent();
        
        ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);
        
        if (!this.deadlyCurrent) { result.addAction(Actions_E.STAY); }
        if (this.topfree && !this.deadlyTop) { result.addAction(Actions_E.UP); }
        if (this.botfree && !this.deadlyBot) { result.addAction(Actions_E.DOWN); }
        if (this.leftfree && !this.deadlyLeft) { result.addAction(Actions_E.LEFT); }
        if (this.rightfree && !this.deadlyRight) { result.addAction(Actions_E.RIGHT); }
        if (playerOnBomb == 0 && !this.deadlyCurrent)  { result.addAction(Actions_E.BOMB); }
        
        //result.intArray[] = freeDirection;
        result.intArray[0] = opponentDirection;
        result.intArray[1] = bombSituation;
        result.intArray[2] = playerOnBomb;
        result.intArray[3] = freeDirection;
        result.intArray[4] = spacesToEdge;
        result.doubleArray[0] = distanceToOpponent;
        
        this.lastDistance = distanceToOpponent;
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
        int lastBombSituation = bombSituation;
        
        Player currentPlayer = determineCurrentPlayer();
        Player opponentPlayer = determineOppenentPlayer();
        
        int freeDirection = this.determinefreeDirections();
        int opponentDirection = this.determineOppenentDirection();
        double distanceToOpponent = this.determineDistanceToOpponent();
        this.bombSituation = determineBombSituation();
        int playerOnBomb = this.determinePlayerOnBomb();
        int spacesToEdge = this.determineSpacesToEdge();
        
        ObservationWithActions currentObs = new ObservationWithActions(numIntegers, numDoubles);
        
        if (!this.deadlyCurrent) { currentObs.addAction(Actions_E.STAY); }
        if (this.topfree && !this.deadlyTop) { currentObs.addAction(Actions_E.UP); }
        if (this.botfree && !this.deadlyBot) { currentObs.addAction(Actions_E.DOWN); }
        if (this.leftfree && !this.deadlyLeft) { currentObs.addAction(Actions_E.LEFT); }
        if (this.rightfree && !this.deadlyRight) { currentObs.addAction(Actions_E.RIGHT); }
        if (playerOnBomb == 0)  { currentObs.addAction(Actions_E.BOMB); }        
        
        //currentObs.intArray[0] = freeDirection;
        currentObs.intArray[0] = opponentDirection;
        currentObs.intArray[1] = bombSituation;
        currentObs.intArray[2] = playerOnBomb;
        currentObs.intArray[3] = freeDirection;
        currentObs.intArray[4] = spacesToEdge;
        currentObs.doubleArray[0] = distanceToOpponent; 
        
        //this.environmentLogln("Distance: " + distanceToOpponent);
        if (distanceToOpponent < lastDistance && lastBombSituation == 0)
        {
            theReward = 50 - (distanceToOpponent); 
        }
        /*
        if (currentDanger < lastDanger)
        {
            theReward = 400;
        }
        */
        //win
        if (currentPlayer.isAlive() && !opponentPlayer.isAlive()) {
            //decrement the by 100 for the span the enemy is away
            theReward = (1100 - (distanceToOpponent * 100));
            theReward = (theReward <= 300) ?  300 : theReward - (distanceToOpponent * 100);
        }
        //lose
        if (!currentPlayer.isAlive() && opponentPlayer.isAlive()) {
            theReward = -500;
        }
        //draw
        if (!currentPlayer.isAlive() && !opponentPlayer.isAlive()) {
            theReward = -300;
        }
               
        //reward bomb-plant
        /*
        if (arg0.intArray[0] == 5) {
        	theReward = 10;
        } */
        
        //negative reward for placing bombs without sense
        if (arg0.intArray[0] == 5 && distanceToOpponent > this.board.getExplosionRadius()+2) {
            theReward = -50;
        }
        
        this.lastDistance = distanceToOpponent;
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();
        
        return new Reward_observation_terminal(theReward,currentObs,episodeOver);
    }    
    
    /* ************************************************************** */
    /**
     * determineSpacesToEdge
     * @return
    */ /************************************************************* */
    protected int determineSpacesToEdge() {
        int result = 0;
        Player oP = this.determineOppenentPlayer();
        
        int maxX = board.getBoard().length -1; 
        int maxY = board.getBoard()[0].length -1;
        
        int minDiffX = (Math.abs(oP.getX() - 0) < Math.abs(oP.getX() - maxX) ? Math.abs(oP.getX() - 0) : Math.abs(oP.getX() - maxX));
        int minDiffY = (Math.abs(oP.getY() - 0) < Math.abs(oP.getY() - maxY) ? Math.abs(oP.getY() - 0) : Math.abs(oP.getY() - maxY));
                
        result = (minDiffX < minDiffY) ? minDiffX : minDiffY;
        
        return result;
    }
}

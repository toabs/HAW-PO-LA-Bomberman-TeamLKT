/**
 * 
 */
package klt.environment;

import Core.Playboard;
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

/**
 * @author LarsE
 * 19.10.2014
 *
 * An implementation of the environment which creates the observations in a way that the agent is able to fight an moving enemy.
 */
public class EnvironmentFighter extends Environment
{
    private int playerID;
    private DebugState debugState;
    private Playboard board;
    private DirectionValues dv = new DirectionValues();
    private double lastDistance;
    private int lastX;
    private int lastY;
    private int boardX;
    private int boardY;

    public EnvironmentFighter(DebugState debugState) {
        this.debugState = debugState;
	}

    /**
     *
     * env_cleanup
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
    */
    @Override
    public void env_cleanup()
    {
        this.environmentLogln("Env_cleanup called!", debugState);
    }

    /**
     * env_init
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_init()
    */
    @Override
    public String env_init()
    {
        maxDistanceToOpponent = Math.sqrt(Math.pow(board.getBoard().length, 2) + Math.pow(board.getBoard()[0].length, 2));
        this.environmentLogln("maxDistance is : " + maxDistanceToOpponent, debugState);

        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 5)); //five possible actions (without bomb-planting)
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, freeDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, oppenentDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, bombSituations));
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
        environmentLogln("env_message called!", debugState);
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
        Player currentPlayer = determineCurrentPlayer(board, playerID);
        int currentPlayerX = currentPlayer.getX();
        int currentPlayerY = currentPlayer.getY();

        Player opponentPlayer = determineOppenentPlayer(board, playerID);
        int opponentPlayerX = opponentPlayer.getX();
        int opponentPlayerY = opponentPlayer.getY();

        int[][] dangerA = determineBombZones(boardX, boardY, board);
        int freeDirection = determinefreeDirections(currentPlayerX, currentPlayerY, boardX, boardY, board, dv);
        int bombSituation = determineBombSituation(boardX, boardY, currentPlayerX, currentPlayerY, dangerA, debugState, dv);
        int playerOnBomb = determinePlayerOnBomb(currentPlayerX, currentPlayerY, board);
        int opponentDirection = determineOpponentDirection(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY, debugState);
        double distanceToOpponent = determineDistanceToOpponent(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY);
        
        ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);

        if (!dv.deadlyCurrent) { result.addAction(Actions_E.STAY); }
        if (dv.upFree && !dv.deadlyUp) { result.addAction(Actions_E.UP); }
        if (dv.downFree && !dv.deadlyDown) { result.addAction(Actions_E.DOWN); }
        if (dv.leftFree && !dv.deadlyLeft) { result.addAction(Actions_E.LEFT); }
        if (dv.rightFree && !dv.deadlyRight) { result.addAction(Actions_E.RIGHT); }
        if (playerOnBomb == 0)  { result.addAction(Actions_E.BOMB); }

        result.intArray[0] = opponentDirection;
        result.intArray[1] = bombSituation;
        result.intArray[2] = playerOnBomb;
        result.intArray[3] = freeDirection;
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
        Player currentPlayer = determineCurrentPlayer(board, playerID);
        int currentPlayerX = currentPlayer.getX();
        int currentPlayerY = currentPlayer.getY();

        Player opponentPlayer = determineOppenentPlayer(board, playerID);
        int opponentPlayerX = opponentPlayer.getX();
        int opponentPlayerY = opponentPlayer.getY();

        int[][] dangerA = determineBombZones(boardX, boardY, board);
        int freeDirection = determinefreeDirections(currentPlayerX, currentPlayerY, boardX, boardY, board, dv);
        int bombSituation = determineBombSituation(boardX, boardY, currentPlayerX, currentPlayerY, dangerA, debugState, dv);
        int playerOnBomb = determinePlayerOnBomb(currentPlayerX, currentPlayerY, board);
        int opponentDirection = determineOpponentDirection(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY, debugState);
        double distanceToOpponent = determineDistanceToOpponent(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY);
        
        ObservationWithActions currentObs = new ObservationWithActions(numIntegers, numDoubles);

        if (!dv.deadlyCurrent) { currentObs.addAction(Actions_E.STAY); }
        if (dv.upFree && !dv.deadlyUp) { currentObs.addAction(Actions_E.UP); }
        if (dv.downFree && !dv.deadlyDown) { currentObs.addAction(Actions_E.DOWN); }
        if (dv.leftFree && !dv.deadlyLeft) { currentObs.addAction(Actions_E.LEFT); }
        if (dv.rightFree && !dv.deadlyRight) { currentObs.addAction(Actions_E.RIGHT); }
        if (playerOnBomb == 0)  { currentObs.addAction(Actions_E.BOMB); }
        
        //currentObs.intArray[0] = freeDirection;
        currentObs.intArray[0] = opponentDirection;
        currentObs.intArray[1] = bombSituation;
        currentObs.intArray[2] = playerOnBomb;
        currentObs.intArray[3] = freeDirection;
        currentObs.doubleArray[0] = distanceToOpponent; 
        
        //this.environmentLogln("Distance: " + distanceToOpponent);
        if (distanceToOpponent < lastDistance)
        {
            theReward = 1; 
        } 
        /*
        if (distanceToOpponent > lastDistance)
        {
            theReward = -1;
        }
        */
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
        
        //negative reward if not moved, if move was not "stay" or "bomb"
        if (lastX == currentPlayer.getX() && lastY == currentPlayer.getY() && arg0.intArray[0] != 0 && arg0.intArray[0] != 5)
        {
            this.environmentLogln("--", debugState);
            theReward = -100;
        }      
        
        this.lastDistance = distanceToOpponent;
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();
        
        return new Reward_observation_terminal(theReward,currentObs,episodeOver);
    }

    @Override
    public void setPlayboard(Playboard playboard, int userID) {
        this.playerID = userID;
        this.board = playboard;
        boardX = board.getBoard().length;
        boardY = board.getBoard()[0].length;
    }
}

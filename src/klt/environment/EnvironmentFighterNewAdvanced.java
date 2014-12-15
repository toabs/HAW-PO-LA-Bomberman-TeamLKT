/**
 * 
 */
package klt.environment;

import Core.Playboard;
import Core.Player;
import klt.ObservationWithActions;
import klt.util.Actions_E;
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
public class EnvironmentFighterNewAdvanced extends Environment
{
    private final DebugState debugState;
    protected int numberOfSpacesToEdge = 0;
    protected final int numIntegers = 6;
    protected int bombSituation = 0;
    private int playerID;
    private Playboard board;
    private int boardX;
    private int boardY;
    private DirectionValues dv = new DirectionValues();
    private double lastDistance;
    private int lastX;
    private int lastY;
    private int distanceRadiusOffset;

    public EnvironmentFighterNewAdvanced(DebugState debugState) {
        this.debugState = debugState;
	}

    /* ************************************************************** */
    /**
     * env_cleanup
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
    */ /************************************************************* */
    @Override
    public void env_cleanup()
    {
        this.environmentLogln("Env_cleanup called!", debugState);
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
        this.environmentLogln("maxDistance is : " + maxDistanceToOpponent, debugState);

        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 5)); //five possible actions (without bomb-planting)
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, freeDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, oppenentDirections));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, bombSituations));
        theTaskSpecObject.addDiscreteObservation(new IntRange(1, numberOfSpacesToEdge));
        theTaskSpecObject.addDiscreteObservation(new IntRange(0, escapePaths));
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
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_message(String)
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
        int currentX = currentPlayer.getX();
        int currentY = currentPlayer.getY();

        Player opponentPlayer = determineOppenentPlayer(board, playerID);
        int opponentPlayerX = opponentPlayer.getX();
        int opponentPlayerY = opponentPlayer.getY();

        int freeDirection = determinefreeDirections(currentX, currentY, boardX, boardY, board, dv);
        int[][] dangerA = determineBombZones(boardX, boardY, board);
        this.bombSituation = determineBombSituation(boardX, boardY, currentX, currentY, dangerA, debugState, dv);
        int playerOnBomb = determinePlayerOnBomb(currentX, currentY, board);
        int opponentDirection = determineOpponentDirection(currentX, currentY, opponentPlayerX, opponentPlayerY, debugState);
        int spacesToEdge = determineSpacesToEdge(opponentPlayerX, opponentPlayerY, boardX, boardY);
        double distanceToOpponent = determineDistanceToOpponent(currentX, currentY, opponentPlayerX, opponentPlayerY);
        int freeEscapePath = determineEscapeRoute(currentX, currentY, dangerA, boardX, boardY);

        ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);

        if (!dv.deadlyCurrent) { result.addAction(Actions_E.STAY); }
        if (dv.upFree && !dv.deadlyUp) { result.addAction(Actions_E.UP); }
        if (dv.downFree && !dv.deadlyDown) { result.addAction(Actions_E.DOWN); }
        if (dv.leftFree && !dv.deadlyLeft) { result.addAction(Actions_E.LEFT); }
        if (dv.rightFree && !dv.deadlyRight) { result.addAction(Actions_E.RIGHT); }
        if (playerOnBomb == 0)  { result.addAction(Actions_E.BOMB); }
        
        //result.intArray[] = freeDirection;
        result.intArray[0] = opponentDirection;
        result.intArray[1] = bombSituation;
        result.intArray[2] = playerOnBomb;
        result.intArray[3] = freeDirection;
        result.intArray[4] = spacesToEdge;
        result.intArray[5] = freeEscapePath;
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

        Player currentPlayer = determineCurrentPlayer(board, playerID);
        int currentX = currentPlayer.getX();
        int currentY = currentPlayer.getY();

        Player opponentPlayer = determineOppenentPlayer(board, playerID);
        int opponentPlayerX = opponentPlayer.getX();
        int opponentPlayerY = opponentPlayer.getY();

        int freeDirection = determinefreeDirections(currentX, currentY, boardX, boardY, board, dv);
        int[][] dangerA = determineBombZones(boardX, boardY, board);
        this.bombSituation = determineBombSituation(boardX, boardY, currentX, currentY, dangerA, debugState, dv);
        int playerOnBomb = determinePlayerOnBomb(currentX, currentY, board);
        int opponentDirection = determineOpponentDirection(currentX, currentY, opponentPlayerX, opponentPlayerY, debugState);
        int spacesToEdge = determineSpacesToEdge(opponentPlayerX, opponentPlayerY, boardX, boardY);
        double distanceToOpponent = determineDistanceToOpponent(currentX, currentY, opponentPlayerX, opponentPlayerY);
        int freeEscapePath = determineEscapeRoute(currentX, currentY, dangerA, boardX, boardY);

        ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);

        if (!dv.deadlyCurrent) { result.addAction(Actions_E.STAY); }
        if (dv.upFree && !dv.deadlyUp) { result.addAction(Actions_E.UP); }
        if (dv.downFree && !dv.deadlyDown) { result.addAction(Actions_E.DOWN); }
        if (dv.leftFree && !dv.deadlyLeft) { result.addAction(Actions_E.LEFT); }
        if (dv.rightFree && !dv.deadlyRight) { result.addAction(Actions_E.RIGHT); }
        if (playerOnBomb == 0)  { result.addAction(Actions_E.BOMB); }

        //result.intArray[] = freeDirection;
        result.intArray[0] = opponentDirection;
        result.intArray[1] = bombSituation;
        result.intArray[2] = playerOnBomb;
        result.intArray[3] = freeDirection;
        result.intArray[4] = spacesToEdge;
        result.intArray[5] = freeEscapePath;
        result.doubleArray[0] = distanceToOpponent;
        
        //this.environmentLogln("Distance: " + distanceToOpponent);
        if (distanceToOpponent <= lastDistance && lastBombSituation == 0 && lastDistance != 0.0)
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
        if (arg0.intArray[0] == 5 && distanceToOpponent >= this.board.getExplosionRadius()+this.distanceRadiusOffset) {
            theReward = -50;
        }
        
        this.lastDistance = distanceToOpponent;
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();
        
        return new Reward_observation_terminal(theReward,result,episodeOver);
    }    


    @Override
    public void setPlayboard(Playboard playboard, int userID) {
        this.playerID = userID;
        this.board = playboard;
        boardX = board.getBoard().length;
        boardY = board.getBoard()[0].length;
    }
}

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

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class EnvironmentAvoidbombZone extends Environment
{
    private final int freeDirections = (int) Math.pow(2, 4); //4 direction, blocked, not blocked
    private final int bombSituations = 125;
    private final int numIntegers = 3;
    private final int numDoubles = 0;
    private DebugState debugState;
    private DirectionValues dv = new DirectionValues();
    private Playboard board;
    private int playerID = 0;
    private int boardX;
    private int boardY;
    private int lastX;
    private int lastY;
    
    /* ************************************************************** */
    /**
     * EnvironmentFighter
     * @param debugState
    */ /************************************************************* */
    public EnvironmentAvoidbombZone(DebugState debugState) {
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

        int freeDirection = determinefreeDirections(currentX, currentY, boardX, boardY, board, dv);
        int[][] dangerA = determineBombZones(boardX, boardY, board);
        int bombSituation = determineBombSituation(boardX, boardY, currentX, currentY, dangerA, debugState, dv);
        int playerOnBomb = determinePlayerOnBomb(currentX, currentY, board);
        
        ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);
        if (!dv.deadlyCurrent) { result.addAction(Actions_E.STAY); }
        if (dv.upFree && !dv.deadlyUp) { result.addAction(Actions_E.UP); }
        if (dv.downFree && !dv.deadlyDown) { result.addAction(Actions_E.DOWN); }
        if (dv.leftFree && !dv.deadlyLeft) { result.addAction(Actions_E.LEFT); }
        if (dv.rightFree && !dv.deadlyRight) { result.addAction(Actions_E.RIGHT); }
        
        result.intArray[0] = freeDirection;
        result.intArray[1] = bombSituation;
        result.intArray[2] = playerOnBomb;
        
        lastX = currentPlayer.getX();
        lastY = currentPlayer.getY();
        
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
        int currentX = currentPlayer.getX();
        int currentY = currentPlayer.getY();

        int freeDirection = determinefreeDirections(currentX, currentY, boardX, boardY, board, dv);
        int[][] dangerA = determineBombZones(boardX, boardY, board);
        int bombSituation = determineBombSituation(boardX, boardY, currentX, currentY, dangerA, debugState, dv);
        int playerOnBomb = determinePlayerOnBomb(currentX, currentY, board);

        ObservationWithActions currentObs = new ObservationWithActions(numIntegers, numDoubles);
        if (!dv.deadlyCurrent) { currentObs.addAction(Actions_E.STAY); }
        if (dv.upFree && !dv.deadlyUp) { currentObs.addAction(Actions_E.UP); }
        if (dv.downFree && !dv.deadlyDown) { currentObs.addAction(Actions_E.DOWN); }
        if (dv.leftFree && !dv.deadlyLeft) { currentObs.addAction(Actions_E.LEFT); }
        if (dv.rightFree && !dv.deadlyRight) { currentObs.addAction(Actions_E.RIGHT); }
        
        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = bombSituation;
        currentObs.intArray[2] = playerOnBomb;

        //TODO find a new way for him to recognize a rise or a fall in currentDanger compared to the last iteration!
        
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

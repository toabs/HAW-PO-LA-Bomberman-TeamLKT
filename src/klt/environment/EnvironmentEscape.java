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

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class EnvironmentEscape extends Environment
{
    private Playboard board;
    private int playerID;
    private DirectionValues dv = new DirectionValues();
    private DebugState debugState;
    private double lastDistance;
    private int lastX;
    private int lastY;
    private int boardX;
    private int boardY;

    public EnvironmentEscape(DebugState debugState) {
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
	    environmentLogln("Env_cleanup called!", debugState);
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
	    System.out.println("maxDistance is : " + maxDistanceToOpponent);
	
	    TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
	    theTaskSpecObject.addDiscreteAction(new IntRange(0, 4)); //five possible actions (without bomb-planting)
	    theTaskSpecObject.addDiscreteObservation(new IntRange(1, freeDirections));
	    theTaskSpecObject.addDiscreteObservation(new IntRange(1, oppenentDirections));
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
	    environmentLogln("env_meassage called!", debugState);
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

        Player opponent = determineOppenentPlayer(board, playerID);
        int opponentX = opponent.getX();
        int opponentY = opponent.getY();

        int freeDirection = determinefreeDirections(currentX, currentY, boardX, boardY, board, dv);
        int opponentDirection = determineOpponentDirection(currentX, currentY, opponentX, opponentY, debugState);
        double distanceToOpponent = determineDistanceToOpponent(currentX, currentY, opponentX, opponentY);
	
	    Observation result = new Observation(numIntegers, numDoubles);
	    result.intArray[0] = freeDirection;
	    result.intArray[1] = opponentDirection;
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
        int currentX = currentPlayer.getX();
        int currentY = currentPlayer.getY();

        Player opponent = determineOppenentPlayer(board, playerID);
        int opponentX = opponent.getX();
        int opponentY = opponent.getY();

        int freeDirection = determinefreeDirections(currentX, currentY, boardX, boardY, board, dv);
        int opponentDirection = determineOpponentDirection(currentX, currentY, opponentX, opponentY, debugState);
        double distanceToOpponent = determineDistanceToOpponent(currentX, currentY, opponentX, opponentY);

        Observation currentObs = new Observation(numIntegers, numDoubles);
        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = opponentDirection;
        currentObs.doubleArray[0] = distanceToOpponent;

        System.out.println("Distance: " + distanceToOpponent);

        if (lastDistance > distanceToOpponent)
        {
            theReward = -1;
            System.out.println("11+");
        }

        if (lastDistance < distanceToOpponent)
        {
            theReward = 1;
            System.out.println("11-");
        }

        //negative reward if not moved
        if (lastX == currentPlayer.getX() && lastY == currentPlayer.getY() && lastDistance != 0)
        {
            System.out.println("--");
            theReward = -2;
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

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
 * @author LarsE 19.10.2014
 */
/* *********************************************************** */
public class EnvironmentFollower extends Environment {
	private int playerID;
	private Playboard board;
	private int boardX;
	private int boardY;
	private DebugState debugState;
	private DirectionValues dv = new DirectionValues();
	private double lastDistance;
	private int lastX;
	private int lastY;

	public EnvironmentFollower(DebugState debugState) {
		this.debugState = debugState;
	}

	/* ************************************************************** */
	/**
	 * env_cleanup
	 * 
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
	 */
	/************************************************************* */
	@Override
	public void env_cleanup() {
		this.environmentLogln("Env_cleanup called!", debugState);
	}

	/* ************************************************************** */
	/**
	 * env_init
	 * 
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_init()
	 */
	/************************************************************* */
	@Override
	public String env_init() {
		maxDistanceToOpponent = Math.sqrt(Math.pow(board.getBoard().length, 2)
				+ Math.pow(board.getBoard()[0].length, 2));
		this.environmentLogln("maxDistance is : " + maxDistanceToOpponent, debugState);

		TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
		theTaskSpecObject.addDiscreteAction(new IntRange(0, 4)); // five
																	// possible
																	// actions
																	// (without
																	// bomb-planting)
		theTaskSpecObject
				.addDiscreteObservation(new IntRange(1, freeDirections));
		theTaskSpecObject.addDiscreteObservation(new IntRange(1,
				oppenentDirections));
		theTaskSpecObject.addContinuousObservation(new DoubleRange(0,
				maxDistanceToOpponent, board.getBoard().length
						* board.getBoard()[0].length));
		theTaskSpecObject.setEpisodic();
		theTaskSpecObject.setRewardRange(new DoubleRange(-20, 20));

		String taskSpecString = theTaskSpecObject.toTaskSpec();
		TaskSpec.checkTaskSpec(taskSpecString);
		return taskSpecString;
	}

	/* ************************************************************** */
	/**
	 * env_message
	 * 
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_message(java.lang.String)
	 */
	/************************************************************* */
	@Override
	public String env_message(String arg0) {
		return null;
	}

	/* ************************************************************** */
	/**
	 * env_start
	 * 
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_start()
	 */
	/************************************************************* */
	@Override
	public Observation env_start() {
		Player currentPlayer = determineCurrentPlayer(board, playerID);
		int currentPlayerX = currentPlayer.getX();
		int currentPlayerY = currentPlayer.getY();

		Player opponentPlayer = determineOppenentPlayer(board, playerID);
		int opponentPlayerX = opponentPlayer.getX();
		int opponentPlayerY = opponentPlayer.getY();

		int freeDirection = determinefreeDirections(currentPlayerX, currentPlayerY, boardX, boardY, board, dv);
		int opponentDirection = determineOpponentDirection(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY, debugState);
		double distanceToOpponent = determineDistanceToOpponent(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY);

		ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);
		result.intArray[0] = freeDirection;
		result.intArray[1] = opponentDirection;
		result.doubleArray[0] = distanceToOpponent;

		if (!dv.deadlyCurrent) { result.addAction(Actions_E.STAY); }
		if (dv.upFree && !dv.deadlyUp) { result.addAction(Actions_E.UP); }
		if (dv.downFree && !dv.deadlyDown) { result.addAction(Actions_E.DOWN); }
		if (dv.leftFree && !dv.deadlyLeft) { result.addAction(Actions_E.LEFT); }
		if (dv.rightFree && !dv.deadlyRight) { result.addAction(Actions_E.RIGHT); }

		this.lastDistance = distanceToOpponent;
		this.lastX = currentPlayer.getX();
		this.lastY = currentPlayer.getY();

		return result;
	}

	/* ************************************************************** */
	/**
	 * env_step
	 * 
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_step(org.rlcommunity.rlglue.codec.types.Action)
	 */
	/************************************************************* */
	@Override
	public Reward_observation_terminal env_step(Action arg0) {
		double theReward = 0.0d;
		boolean episodeOver = false;

		Player currentPlayer = determineCurrentPlayer(board, playerID);
		int currentPlayerX = currentPlayer.getX();
		int currentPlayerY = currentPlayer.getY();

		Player opponentPlayer = determineOppenentPlayer(board, playerID);
		int opponentPlayerX = opponentPlayer.getX();
		int opponentPlayerY = opponentPlayer.getY();

		int freeDirection = determinefreeDirections(currentPlayerX, currentPlayerY, boardX, boardY, board, dv);
		int opponentDirection = determineOpponentDirection(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY, debugState);
		double distanceToOpponent = determineDistanceToOpponent(currentPlayerX, currentPlayerY, opponentPlayerX, opponentPlayerY);

		ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);
		result.intArray[0] = freeDirection;
		result.intArray[1] = opponentDirection;
		result.doubleArray[0] = distanceToOpponent;

		if (!dv.deadlyCurrent) { result.addAction(Actions_E.STAY); }
		if (dv.upFree && !dv.deadlyUp) { result.addAction(Actions_E.UP); }
		if (dv.downFree && !dv.deadlyDown) { result.addAction(Actions_E.DOWN); }
		if (dv.leftFree && !dv.deadlyLeft) { result.addAction(Actions_E.LEFT); }
		if (dv.rightFree && !dv.deadlyRight) { result.addAction(Actions_E.RIGHT); }


		// this.environmentLogln("Distance: " + distanceToOpponent);

		if (distanceToOpponent < lastDistance) {
			theReward = 10;
			// this.environmentLogln("+");
		} else {
		    theReward = -10;
		}

		// great reward for reached enemy
		if (distanceToOpponent == 0.0) {
			theReward = 10;
		}

		System.out.println("this.lastX:" + this.lastX);
		System.out.println("this.lastY:" + this.lastY);
		System.out.println("this.lastDistance:" + this.lastDistance);

		this.lastDistance = distanceToOpponent;
		this.lastX = currentPlayer.getX();
		this.lastY = currentPlayer.getY();

		return new Reward_observation_terminal(theReward, result, episodeOver);
	}

	@Override
	public void setPlayboard(Playboard playboard, int userID) {
		this.playerID = userID;
		this.board = playboard;
		boardX = board.getBoard().length;
		boardY = board.getBoard()[0].length;
	}
}

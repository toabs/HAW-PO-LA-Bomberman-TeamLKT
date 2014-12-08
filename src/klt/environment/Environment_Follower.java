/**
 * 
 */
package klt.environment;

import klt.ObservationWithActions;
import klt.util.Actions_E;
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
 * @author LarsE 19.10.2014
 */
/* *********************************************************** */
public class Environment_Follower extends Environment {
	/* ************************************************************** */
	/**
	 * env_cleanup
	 * 
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
	 */
	/************************************************************* */
	@Override
	public void env_cleanup() {
		this.environmentLogln("Env_cleanup called!");
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
		this.environmentLogln("maxDistance is : " + maxDistanceToOpponent);

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
		// TODO Auto-generated method stub
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
		Player currentPlayer = determineCurrentPlayer();

		int freeDirection = this.determinefreeDirections();
		int opponentDirection = this.determineOppenentDirection();
		double distanceToOpponent = this.determineDistanceToOpponent();

		ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);
		result.intArray[0] = freeDirection;
		result.intArray[1] = opponentDirection;
		result.doubleArray[0] = distanceToOpponent;
		
        result.addAction(Actions_E.STAY);
        if (this.topfree) { result.addAction(Actions_E.UP); }
        if (this.botfree) { result.addAction(Actions_E.DOWN); }
        if (this.leftfree) { result.addAction(Actions_E.LEFT); }
        if (this.rightfree) { result.addAction(Actions_E.RIGHT); } 

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
		Player currentPlayer = determineCurrentPlayer();

		int freeDirection = this.determinefreeDirections();
		int opponentDirection = this.determineOppenentDirection();
		double distanceToOpponent = this.determineDistanceToOpponent();

		ObservationWithActions currentObs = new ObservationWithActions(numIntegers, numDoubles);
		currentObs.intArray[0] = freeDirection;
		currentObs.intArray[1] = opponentDirection;
		currentObs.doubleArray[0] = distanceToOpponent;
		
		currentObs.addAction(Actions_E.STAY);
        if (this.topfree) { currentObs.addAction(Actions_E.UP); }
        if (this.botfree) { currentObs.addAction(Actions_E.DOWN); }
        if (this.leftfree) { currentObs.addAction(Actions_E.LEFT); }
        if (this.rightfree) { currentObs.addAction(Actions_E.RIGHT); } 


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

		return new Reward_observation_terminal(theReward, currentObs,
				episodeOver);
	}
}

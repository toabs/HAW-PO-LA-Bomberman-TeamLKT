package klt;

import Core.Player;
import klt.util.Actions_E;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/**
 * Created by Tobi on 24.11.2014.
 */
public class Environment_GoTo extends Environment {
    private int destinationX = 0;
    private int destinationY = 0;
    private int maxX = 0;
    private int maxY = 0;
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
        destinationX++;
        if (destinationX >= maxX ){
            destinationX=0;
            destinationY++;
            if(destinationY >= maxY){
                destinationY=0;
            }
        }
        System.out.println("Destination now is x: " + destinationX + " y: " + destinationY);
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
        maxDistanceToOpponent = Math.sqrt(Math.pow(board.getBoard().length, 2) + Math.pow(board.getBoard()[0].length, 2));
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
        maxX = board.getBoard().length;
        maxY = board.getBoard()[0].length;

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

        int freeDirection = determinefreeDirections();
        int destinationDirection = determineDestinationDirection();
        double distanceToDestination = determineDistanceToDestination();

        ObservationWithActions result = new ObservationWithActions(numIntegers, numDoubles);
        result.addAction(Actions_E.STAY);
        if (this.topfree) { result.addAction(Actions_E.UP); }
        if (this.botfree) { result.addAction(Actions_E.DOWN); }
        if (this.leftfree) { result.addAction(Actions_E.LEFT); }
        if (this.rightfree) { result.addAction(Actions_E.RIGHT); }

        result.intArray[0] = freeDirection;
        result.intArray[1] = destinationDirection;
        result.doubleArray[0] = distanceToDestination;

        this.lastDistance = distanceToDestination;
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();

        return result;
    }

    private int determineDestinationDirection() {
        Player cP = determineCurrentPlayer();

        if (cP.getX() == destinationX && cP.getY() == destinationY) return 1; //equal

        //determine
        boolean top = (destinationY > cP.getY());
        boolean bot = (destinationY < cP.getY());
        boolean left = (destinationX < cP.getX());
        boolean right = (destinationX > cP.getX());

        if (top && !left && !right)     return 2; //top
        if (top && left && !right)      return 3; //topleft
        if (top && !left && right)      return 4; //topright
        if (right && !top && !bot)      return 5; //right
        if (left && !top && !bot)       return 6; //left
        if (bot && !left && !right)     return 7; //bot
        if (bot && left && !right)      return 8; //botleft
        if (bot && !left && right)      return 9; //botright

        this.environmentLogln("OpponentDirection error.");
        return 0;
    }

    private double determineDistanceToDestination() {

        Player cP = determineCurrentPlayer();

        int diffx = cP.getX() - destinationX;
        int diffy = cP.getY() - destinationY;

        return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));

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
        currentObs.addAction(Actions_E.STAY);
        if (this.topfree) { currentObs.addAction(Actions_E.UP); }
        if (this.botfree) { currentObs.addAction(Actions_E.DOWN); }
        if (this.leftfree) { currentObs.addAction(Actions_E.LEFT); }
        if (this.rightfree) { currentObs.addAction(Actions_E.RIGHT); }

        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = opponentDirection;
        currentObs.doubleArray[0] = distanceToOpponent;

        // this.environmentLogln("Distance: " + distanceToOpponent);

        if (distanceToOpponent < lastDistance) {
            theReward = 1;
            // this.environmentLogln("+");
        } else {

            if (distanceToOpponent > lastDistance) {
                theReward = -1;
                // this.environmentLogln("-");
            } else {

                // negative reward if not moved
                if (lastX == currentPlayer.getX()
                        && lastY == currentPlayer.getY()
                        && !lastDistance.equals(0.0)) {
                    // this.environmentLogln("--");
                    theReward = -2;
                }
            }
        }

        // great reward for reached enemy
        if (distanceToOpponent < 1) {
            theReward = 200;
        }


        this.lastDistance = distanceToOpponent;
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();

        return new Reward_observation_terminal(theReward, currentObs, episodeOver);
    }
}

package klt;

import Core.Player;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/**
 * Created by Tobi on 20.10.2014.
 */
public class Environment_Escape extends Environment_Follower {

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
        int opponentDirection = this.determineOppenentDirection();
        double distanceToOpponent = this.determineDistanceToOpponent();

        Observation currentObs = new Observation(numIntegers, numDoubles);
        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = opponentDirection;
        currentObs.doubleArray[0] = distanceToOpponent;

        System.out.println("Distance: " + distanceToOpponent);

        if (distanceToOpponent > 5 && distanceToOpponent < 10)
        {
            theReward = 1;
            System.out.println("+");
        }
        else
        {
            theReward = -1;
            System.out.println("-");
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
}

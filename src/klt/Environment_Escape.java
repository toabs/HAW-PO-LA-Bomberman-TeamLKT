/**
 *
 */
package klt;

import java.util.Iterator;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import Core.Player;

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class Environment_Escape extends Environment
{
    private final int freeDirections = (int) Math.pow(2, 4); //4 direction, blocked, not blocked
    private final int oppenentDirections = 9; //equal, top, left, topleft, ...
    private double maxDistanceToOpponent = 0; //to be calculated
    private final int numIntegers = 2;
    private final int numDoubles = 1;

    private double lastDistance = 0;
    private int lastX = 0;
    private int lastY = 0;

    /* ************************************************************** */
    /**
     * env_cleanup
     * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_cleanup()
     */ /************************************************************* */
@Override
public void env_cleanup()
{
    System.out.println("Env_cleanup called!");
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

    int freeDirection = this.determinefreeDirections();
    int opponentDirection = this.determineOppenentDirection();
    double distanceToOpponent = this.determineDistanceToOpponent();

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
     * determineCurrentPlayer
     * @return
     */ /************************************************************* */
Player determineCurrentPlayer() {
    Iterator<Player> it = board.getPlayers().iterator();

    Player thisplayer = null;
    while(it.hasNext())
    {
        thisplayer = it.next();
        if(thisplayer.getId() == this.userID)
            break;
    }

    return thisplayer;
}

    /* ************************************************************** */
    /**
     * determineOppenentPlayer
     * @param currentPlayer
     * @return
     */ /************************************************************* */
private Player determineOppenentPlayer() {
    Iterator<Player> it = board.getPlayers().iterator();

    Player player = null;
    while(it.hasNext())
    {
        player = it.next();
        if(player.getId() != this.userID)
            break;
    }

    return player;
}

    /* ************************************************************** */
    /**
     * determinefreeDirections
     * @return
     */ /************************************************************* */
int determinefreeDirections() {
    Player currentPlayer = determineCurrentPlayer();

    int result = 1;

    boolean topfree =   (currentPlayer.getY()+1 >= board.getBoard()[0].length) ? false : !board.getBoard()[currentPlayer.getX()][currentPlayer.getY()+1].isWall();
    boolean rightfree = (currentPlayer.getX()+1 >= board.getBoard().length) ? false : !board.getBoard()[currentPlayer.getX()+1][currentPlayer.getY()].isWall();
    boolean botfree =   (currentPlayer.getY()-1 < 0 ) ? false : !board.getBoard()[currentPlayer.getX()][currentPlayer.getY()-1].isWall();
    boolean leftfree =  (currentPlayer.getX()-1 < 0 ) ? false : !board.getBoard()[currentPlayer.getX()-1][currentPlayer.getY()].isWall();

    result += (topfree) ? Math.pow(2, 0) : 0;
    result += (rightfree) ? Math.pow(2, 1) : 0;
    result += (botfree) ? Math.pow(2, 2) : 0;
    result += (leftfree) ? Math.pow(2, 3) : 0;

    return result;
}

    /* ************************************************************** */
    /**
     * determineOppenentDirection
     * @return
     */ /************************************************************* */
int determineOppenentDirection() {
    Player cP = determineCurrentPlayer();
    Player oP = determineOppenentPlayer();

    if (cP.getX() == oP.getX() && cP.getY() == oP.getY()) return 1; //equal

    //determine
    boolean top = (oP.getY() > cP.getY());
    boolean bot = (oP.getY() < cP.getY());
    boolean left = (oP.getX() < cP.getX());
    boolean right = (oP.getX() > cP.getX());

    if (top && !left && !right)     return 2; //top
    if (top && left && !right)      return 3; //topleft
    if (top && !left && right)      return 4; //topright
    if (right && !top && !bot)      return 5; //right
    if (left && !top && !bot)       return 6; //left
    if (bot && !left && !right)     return 7; //bot
    if (bot && left && !right)      return 8; //botleft
    if (bot && !left && right)      return 9; //botright

    System.out.println("OpponentDirection error.");
    return 0;
}

    /* ************************************************************** */
    /**
     * determineDistanceToOpponent
     * @return
     */ /************************************************************* */
double determineDistanceToOpponent()
{
    Player cP = determineCurrentPlayer();
    Player oP = determineOppenentPlayer();

    int diffx = cP.getX() - oP.getX();
    int diffy = cP.getY() - oP.getY();

    return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));
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
        Player currentPlayer = determineCurrentPlayer();

        int freeDirection = this.determinefreeDirections();
        int opponentDirection = this.determineOppenentDirection();
        double distanceToOpponent = this.determineDistanceToOpponent();

        Observation currentObs = new Observation(numIntegers, numDoubles);
        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = opponentDirection;
        currentObs.doubleArray[0] = distanceToOpponent;

        System.out.println("Distance: " + distanceToOpponent);

        if (distanceToOpponent >= 5 && distanceToOpponent <= 10)
        {
            theReward = 2;
            System.out.println("+!");
        }
        if (lastDistance > distanceToOpponent && distanceToOpponent > 10)
        {
            theReward = 1;
            System.out.println("+");
        }
        if (lastDistance < distanceToOpponent && distanceToOpponent > 10){
            theReward = -1;
            System.out.println("-");
        }

        if (5 > distanceToOpponent)
        {
            theReward = -1;
            System.out.println("-");
            if(lastDistance < distanceToOpponent){
                theReward = 1;
                System.out.println("-+");
            }
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

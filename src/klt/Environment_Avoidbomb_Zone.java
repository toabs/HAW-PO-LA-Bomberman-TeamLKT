/**
 * 
 */
package klt;

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
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class Environment_Avoidbomb_Zone extends Environment
{
    private final int freeDirections = (int) Math.pow(2, 4); //4 direction, blocked, not blocked
    private final int oppenentDirections = 9; //equal, top, left, topleft, ...
    private final int bombSituations = 125;
    private double maxDistanceToOpponent = 0; //to be calculated
    private final int numIntegers = 3;
    private final int numDoubles = 1;
    
    private double lastDistance = 0;
    private int lastDanger = 0;
    private int currentDanger = 0;
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
        this.environmentLogln("maxDistance is : " + maxDistanceToOpponent);
        
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 4)); //five possible actions (without bomb-planting)
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
        int bombSituation = determineBombSituation();
        
        Observation currentObs = new Observation(numIntegers, numDoubles);
        currentObs.intArray[0] = freeDirection;
        currentObs.intArray[1] = opponentDirection;
        currentObs.intArray[2] = bombSituation;
        currentObs.doubleArray[0] = distanceToOpponent; 
        
        this.environmentLogln("Distance: " + distanceToOpponent);
        
        if (lastDistance > distanceToOpponent)
        {
            theReward = 100; 
            this.environmentLogln("+");
        } 
        
        if (lastDistance < distanceToOpponent)
        {
            theReward = -100;
            this.environmentLogln("-");
        }
        
        if (currentDanger < lastDanger)
        {
            theReward = 200;
        }
        
        if (lastDanger != 0 && (currentDanger > lastDanger))
        {
            theReward = -200;
        }
        
        //negative reward if not moved
        if (lastX == currentPlayer.getX() && lastY == currentPlayer.getY() && lastDistance != 0)
        {
            this.environmentLogln("--");
            theReward = -500;
        }        
        
        this.lastDistance = distanceToOpponent;
        this.lastX = currentPlayer.getX();
        this.lastY = currentPlayer.getY();
        
        return new Reward_observation_terminal(theReward,currentObs,episodeOver);
    }
    
    /* ************************************************************** */
    /**
     * determineBombSituation
     * @return
    */ /************************************************************* */
    private int determineBombSituation()
    {
        Player cP = this.determineCurrentPlayer();
        int[][] dangerAnalysis = new int[board.getBoard().length][board.getBoard()[0].length];
        int currentBombCounter = 0;
        
        int countZoneStatus = 3; // count of possible danger
        
        int dangerCurrent   = 0;
        int dangerTop       = 0;
        int dangerBot       = 0;
        int dangerLeft      = 0;
        int dangerRight     = 0;
        
        int result = 0;
        
        determineBombZones(dangerAnalysis);
        
        //print out dangerAnalysis
        /*
        for(int i = 0; i < board.getBoard().length; i++)
        {
            for (int n = 0; n < board.getBoard()[0].length; n++)
            {
                this.environmentLog("[" + dangerAnalysis[n][i] + 1 + "]");
            }
            this.environmentLogln("");
        } */
        
        //current Position
        currentBombCounter = dangerAnalysis[cP.getX()][cP.getY()];
        dangerCurrent = evaluateBombCounter(currentBombCounter);
        this.lastDanger = this.currentDanger;
        this.currentDanger = dangerCurrent;
        
        //watch top
        currentBombCounter = getMinBombCounter(cP.getX() -1, cP.getY() + 3, 3, 3, dangerAnalysis);
        dangerTop = evaluateBombCounter(currentBombCounter);
        
        //watch bot
        currentBombCounter = getMinBombCounter(cP.getX() - 1, cP.getY() + 1, 3, 3, dangerAnalysis);
        dangerBot = evaluateBombCounter(currentBombCounter);
        
        //watch left
        currentBombCounter = getMinBombCounter(cP.getX() -3, cP.getY() - 1, 3, 3, dangerAnalysis);
        dangerLeft = evaluateBombCounter(currentBombCounter);
        
        //watch right
        currentBombCounter = getMinBombCounter(cP.getX() +1, cP.getY() -1, 3, 3, dangerAnalysis);
        dangerRight = evaluateBombCounter(currentBombCounter);

        
        result += (dangerCurrent + Math.pow(countZoneStatus, 0));
        result += (dangerTop     + Math.pow(countZoneStatus, 1));
        result += (dangerBot     + Math.pow(countZoneStatus, 2));
        result += (dangerLeft    + Math.pow(countZoneStatus, 3));
        result += (dangerRight   + Math.pow(countZoneStatus, 4));
        
        this.environmentLogln("DangerCurrent=" + dangerCurrent);
        this.environmentLogln("DangerTop=" + dangerTop);
        this.environmentLogln("DangerBot=" + dangerBot);
        this.environmentLogln("DangerLeft=" + dangerLeft);
        this.environmentLogln("DangerRight=" + dangerRight);
        
        return result;
    }
    
    /* ************************************************************** */
    /**
     * getMinBombCounter
     * @param startX
     * @param startY
     * @param spanX
     * @param spanY
     * @param dangerAnalysis
     * @return
    */ /************************************************************* */
    private int getMinBombCounter(int startX, int startY, int spanX, int spanY, int[][] dangerAnalysis) {
        int initCounter = 99; //just a high value to start evaluation
        int result = initCounter;
        
        for(int x = startX; x < startX+spanX; x++)
        {
            for (int y = startY; y < startY + spanY; y++)
            {
                if (validX(x) && validY(y)) {
                    result = ((dangerAnalysis[x][y] < result) ? dangerAnalysis[x][y] : result);
                } else {
                    result = initCounter;
                }
            }
        }
        
        return result;
    }
    
    /* ************************************************************** */
    /** Returns a value from 0-2, depening on how dangerous the counter is
     * evaluateBombCounter
     * @param currentBombCounter
     * @return
    */ /************************************************************* */
    private int evaluateBombCounter(int currentBombCounter) {
        int noDanger    = 0;
        int maybeDanger = 1;
        int highDanger  = 2;
        
        int counterMaybeDanger = 3;
        int counterhighDanger  = 1;
        
        int result = noDanger;
        
        if ((currentBombCounter == 99) || (currentBombCounter >= counterMaybeDanger)) {
            result = noDanger;
        }
        else {
            if (currentBombCounter > counterhighDanger) {
                result = maybeDanger;
            } else {
                result = highDanger;
            }
        }
        
        return result;
    }
}

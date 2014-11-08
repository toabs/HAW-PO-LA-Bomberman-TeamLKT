/**
 * 
 */
package klt;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import Core.Bomb;
import Core.Player;

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
     * determineCurrentPlayer
     * @return
    */ /************************************************************* */
    private Player determineCurrentPlayer() {
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
    private int determinefreeDirections() {
        Player currentPlayer = determineCurrentPlayer();
        boolean[][] bombPosition = new boolean[board.getBoard().length][board.getBoard()[0].length];
        Bomb currentBomb = null;
        
        //initilize with false
        //initilize with initValue
        for(int i = 0; i < board.getBoard().length; i++)
        {
            for (int n = 0; n < board.getBoard()[0].length; n++)
            {
                bombPosition[i][n] = false;
            }
        }
        
        //fill bomb-position array
        Iterator<Bomb> bIt = this.board.getBombs().iterator();
        while(bIt.hasNext())
        {
            currentBomb = bIt.next();
            bombPosition[currentBomb.getX()][currentBomb.getY()] = true;
        }
        
        int result = 1;
        
        boolean topfree =   (validY(currentPlayer.getY()+1) ? !board.getBoard()[currentPlayer.getX()][currentPlayer.getY()+1].isWall()
                                                            && !bombPosition[currentPlayer.getX()][currentPlayer.getY()+1] : false);
        boolean rightfree = (validX(currentPlayer.getX()+1) ? !board.getBoard()[currentPlayer.getX()+1][currentPlayer.getY()].isWall()
                                                            && !bombPosition[currentPlayer.getX()+1][currentPlayer.getY()]: false);
        boolean botfree =   (validY(currentPlayer.getY()-1) ? !board.getBoard()[currentPlayer.getX()][currentPlayer.getY()-1].isWall()
                                                            && !bombPosition[currentPlayer.getX()][currentPlayer.getY()-1]: false);
        boolean leftfree =  (validX(currentPlayer.getX()-1) ? !board.getBoard()[currentPlayer.getX()-1][currentPlayer.getY()].isWall()
                                                            && !bombPosition[currentPlayer.getX()-1][currentPlayer.getY()]: false);
        
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
    private int determineOppenentDirection() {
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
        
        this.environmentLogln("OpponentDirection error.");        
        return 0;
    }
    
    /* ************************************************************** */
    /**
     * validX
     * @param x
     * @return
    */ /************************************************************* */
    boolean validX(int x) {
       return ((x < board.getBoard().length) && (x >= 0));
    }
    
    /* ************************************************************** */
    /**
     * validY
     * @param y
     * @return
    */ /************************************************************* */
    boolean validY(int y) {
        return ((y < board.getBoard()[0].length) && (y >= 0 ));
    }
    
    /* ************************************************************** */
    /**
     * determineDistanceToOpponent
     * @return
    */ /************************************************************* */
    private double determineDistanceToOpponent()
    {     
        Player cP = determineCurrentPlayer();
        Player oP = determineOppenentPlayer();
        
        int diffx = cP.getX() - oP.getX();
        int diffy = cP.getY() - oP.getY();
        
        return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));
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
        currentBombCounter = dangerAnalysis[cP.getY()][cP.getY()];
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

    /* ************************************************************** */
    /**
     * determineBombZones
     * @param dangerAnalysis
    */ /************************************************************* */
    private void determineBombZones(int[][] dangerAnalysis)
    {
        Set<Bomb> bomblist = this.board.getBombs();
        TreeMap<Integer, Bomb> bombByTimeleft = new TreeMap<Integer, Bomb>();
        Bomb currentBomb = null;
        int tempBombCounter = 0;
        int initValue = 99;
        
        boolean topwall = false;
        boolean botwall = false;
        boolean leftwall = false;
        boolean rightwall = false;        
        
        //initilize with initValue
        for(int i = 0; i < board.getBoard().length; i++)
        {
            for (int n = 0; n < board.getBoard()[0].length; n++)
            {
                dangerAnalysis[i][n] = initValue;
            }
        }
        
        //sort bombs by timeleft
        Iterator<Bomb> bIt = bomblist.iterator();
        while(bIt.hasNext())
        {
            currentBomb = bIt.next();
            bombByTimeleft.put(currentBomb.getCounter(), currentBomb);
        }
        
        //iterate again over the sorted treemap
        bIt = bombByTimeleft.values().iterator();
        while(bIt.hasNext())
        {
            topwall = false;
            botwall = false;
            leftwall = false;
            rightwall = false;
            
            //for each bomb, itereate once
            currentBomb = bIt.next();
            
            //Determine shortest bombcounter
            tempBombCounter = dangerAnalysis[currentBomb.getX()][currentBomb.getY()];
            
            if (tempBombCounter < 0 || tempBombCounter > currentBomb.getCounter()) {
                tempBombCounter = currentBomb.getCounter();
            }
            
            dangerAnalysis[currentBomb.getX()][currentBomb.getY()] = tempBombCounter;
            
            //radius
            //todo: one var for each way, to check for walls and stop
            for (int i = 1; i < currentBomb.getExplosionRadius(); i++) {
                //top
                if (validY(currentBomb.getY() + i) && !topwall) {
                    if (board.getBoard()[currentBomb.getX()][currentBomb.getY() + i].isWall()) {
                       topwall = true; 
                    }
                    else {
                        if (dangerAnalysis[currentBomb.getX()][currentBomb.getY() + i] > tempBombCounter
                            || dangerAnalysis[currentBomb.getX()][currentBomb.getY() + i] == initValue) {
                            dangerAnalysis[currentBomb.getX()][currentBomb.getY() + i] = tempBombCounter;
                        }
                    }
                }
                //bot
                if (validY(currentBomb.getY() - i) && !botwall) {
                    if (board.getBoard()[currentBomb.getX()][currentBomb.getY() - i].isWall()) {
                        botwall = true; 
                     }
                     else {
                        if (dangerAnalysis[currentBomb.getX()][currentBomb.getY() - i] > tempBombCounter
                            || dangerAnalysis[currentBomb.getX()][currentBomb.getY() - i] == initValue) {
                            dangerAnalysis[currentBomb.getX()][currentBomb.getY() - i] = tempBombCounter;
                        }   
                    }
                }
                //left
                if (validX(currentBomb.getX() - i) && !leftwall) {
                    if (board.getBoard()[currentBomb.getX() - i][currentBomb.getY()].isWall()) {
                        leftwall = true; 
                    }
                    else {
                        if (dangerAnalysis[currentBomb.getX() - i][currentBomb.getY()] > tempBombCounter
                            || dangerAnalysis[currentBomb.getX() - i][currentBomb.getY()] == initValue) {
                            dangerAnalysis[currentBomb.getX() - i][currentBomb.getY()] = tempBombCounter;
                        } 
                    }
                }
                //right
                if (validX(currentBomb.getX() + i) && !rightwall) {
                    if (board.getBoard()[currentBomb.getX() + i][currentBomb.getY()].isWall()) {
                        rightwall = true; 
                    }
                    else {
                        if (dangerAnalysis[currentBomb.getX() + i][currentBomb.getY()] > tempBombCounter
                            || dangerAnalysis[currentBomb.getX() + i][currentBomb.getY()] == initValue) {
                            dangerAnalysis[currentBomb.getX() + i][currentBomb.getY()] = tempBombCounter;
                        } 
                    }
                }
            }
        }
    }   
}

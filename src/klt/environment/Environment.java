/**
 * 
 */
package klt.environment;

import Core.Bomb;
import Core.Playboard;
import Core.Player;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
public abstract class Environment implements EnvironmentInterface
{
    protected Playboard board;
    protected int userID = 0;
    protected DebugState debugState;
    
    //Variables used for state-count-determination
    protected final int freeDirections = (int) Math.pow(2, 4); //4 direction, blocked, not blocked
    protected final int playerOnBombValues = 1;
    protected final int oppenentDirections = 9; //equal, top, left, topleft, ...
    protected final int bombSituations = 125;
    protected double maxDistanceToOpponent = 0; //to be calculated
    protected final int numIntegers = 4;
    protected final int numDoubles = 1;
    
    //helper variables filled by the functions
    protected boolean topfree = false;
    protected boolean botfree = false;
    protected boolean leftfree = false;
    protected boolean rightfree = false;
    
    protected int dangerCurrent   = 0;
    protected int dangerTop       = 0;
    protected int dangerBot       = 0;
    protected int dangerLeft      = 0;
    protected int dangerRight     = 0;
    
    protected boolean deadlyCurrent   = false;
    protected boolean deadlyTop       = false;
    protected boolean deadlyBot       = false;
    protected boolean deadlyLeft      = false;
    protected boolean deadlyRight     = false;
    
    //Variables used for reward-determination
    protected Double lastDistance = 0.0;
    protected int lastDanger = 0;
    protected int currentDanger = 0;
    protected int lastX = 0;
    protected int lastY = 0;

    /**
     * @param debugState
     */
    Environment(DebugState debugState){
        super();
        this.debugState = debugState;
    }

    /**
     * 
     */
    Environment(){
        super();
    }

    /* ************************************************************** */
    /**
     * setPlayboard
     * @param playboard
    */ /************************************************************* */
public void setPlayboard(Playboard playboard, int userID)
    {
        this.userID = userID;
        board = playboard;
    }

    protected void environmentLogln(String output){
        if (debugState != null) {
            if (debugState.getEnvironmentDebugState()){
                System.out.println(output);
            }
        }
    }

    protected void environmentLog(String output){
        if (debugState != null) {
            if (debugState.getEnvironmentDebugState()){
                System.out.print(output);
            }
        }
    }
    
    /* ************************************************************** */
    /**
     * determineCurrentPlayer
     * @return
     */ /************************************************************* */
    protected Player determineCurrentPlayer() {
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
    protected Player determineOppenentPlayer() {
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
     * determineOppenentDirection
     * @return
    */ /************************************************************* */
    protected int determineOppenentDirection() {
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
     * determineDistanceToOpponent
     * @return
     */ /************************************************************* */
    protected double determineDistanceToOpponent()
    {
        Player cP = determineCurrentPlayer();
        Player oP = determineOppenentPlayer();
    
        int diffx = cP.getX() - oP.getX();
        int diffy = cP.getY() - oP.getY();
    
        return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));
    }
    
    /* ************************************************************** */
    /**
     * validX
     * @param x
     * @return
    */ /************************************************************* */
    protected boolean validX(int x) {
       return ((x < board.getBoard().length) && (x >= 0));
    }
      
    /* ************************************************************** */
    /**
     * validY
     * @param y
     * @return
    */ /************************************************************* */
    protected boolean validY(int y) {
        return ((y < board.getBoard()[0].length) && (y >= 0 ));
    }
    
    /* ************************************************************** */
    /**
     * determinefreeDirections
     * @return
    */ /************************************************************* */
    protected int determinefreeDirections() {
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
        for (Bomb bomb : this.board.getBombs()) {
            currentBomb = bomb;
            bombPosition[currentBomb.getX()][currentBomb.getY()] = true;
        }
        
        /*
        int dimsize = 15;
        Set<Double> test = new HashSet<Double>();
        for(int x1 = 0; x1 < dimsize; x1++) {
            for(int y1 = 0; y1 < dimsize; y1++) {
                for(int x2 = 0; x2 < dimsize; x2++) {
                    for(int y2 = 0; y2 < dimsize; y2++) {
                        int diffx = x1 - x2;
                        int diffy = y1 - y2;
                        if (!board.getBoard()[x1][y1].isWall() && !board.getBoard()[x2][y2].isWall())  
                        {
                            test.add(Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2)));
                        }
                    }
                }             
            }
        } 
        
        System.out.println("Max Abst�nde: " + test.size());
        */
        
        int result = 0;
        
        this.topfree =   (validY(currentPlayer.getY() - 1) && !board.getBoard()[currentPlayer.getX()][currentPlayer.getY() - 1].isWall()
                && !bombPosition[currentPlayer.getX()][currentPlayer.getY() - 1]);
        this.rightfree = (validX(currentPlayer.getX() + 1) && !board.getBoard()[currentPlayer.getX() + 1][currentPlayer.getY()].isWall()
                && !bombPosition[currentPlayer.getX() + 1][currentPlayer.getY()]);
        this.botfree =   (validY(currentPlayer.getY() + 1) && !board.getBoard()[currentPlayer.getX()][currentPlayer.getY() + 1].isWall()
                && !bombPosition[currentPlayer.getX()][currentPlayer.getY() + 1]);
        this.leftfree =  (validX(currentPlayer.getX() - 1) && !board.getBoard()[currentPlayer.getX() - 1][currentPlayer.getY()].isWall()
                && !bombPosition[currentPlayer.getX() - 1][currentPlayer.getY()]);
        
        result += (this.topfree) ? Math.pow(2, 0) : 0;
        result += (this.rightfree) ? Math.pow(2, 1) : 0;
        result += (this.botfree) ? Math.pow(2, 2) : 0;
        result += (this.leftfree) ? Math.pow(2, 3) : 0;
        
        return result;
    }
    
    /* ************************************************************** */
    /**
     * determineBombZones
     * @param dangerAnalysis
    */ /************************************************************* */
    protected void determineBombZones(int[][] dangerAnalysis)
    {
        Set<Bomb> bomblist = this.board.getBombs();
        TreeMap<Integer, Bomb> bombByTimeleft = new TreeMap<Integer, Bomb>();
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
        for(Bomb currentBomb : bomblist)
        {
            bombByTimeleft.put(currentBomb.getCounter(), currentBomb);
        }
        
        //iterate again over the sorted treemap
        Iterator<Bomb> bIt = bombByTimeleft.values().iterator();
        Bomb currentBomb;
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
    
    protected int determineBombSituation()
    {
        Player cP = this.determineCurrentPlayer();
        int[][] dangerAnalysis = new int[board.getBoard().length][board.getBoard()[0].length];
        int currentBombCounter = 0;
        
        int countZoneStatus = 3; // count of possible danger
        
        dangerCurrent   = 0;
        dangerTop       = 0;
        dangerBot       = 0;
        dangerLeft      = 0;
        dangerRight     = 0;
        
        int result = 0;
        
        determineBombZones(dangerAnalysis);
        
        //print out dangerAnalysis
        for(int i = 0; i < board.getBoard().length; i++)
        {
            for (int n = 0; n < board.getBoard()[0].length; n++)
            {
                if (n == cP.getX() && i == cP.getY()) {
                    this.environmentLog("[XX]");
                } else {
                    this.environmentLog("[" + dangerAnalysis[n][i] + "]");
                }
            }
            this.environmentLogln("");
        } 
        
        //current Position             
        currentBombCounter = dangerAnalysis[cP.getX()][cP.getY()];
        deadlyCurrent =  (currentBombCounter <= 1);
        dangerCurrent = evaluateBombCounter(currentBombCounter);
        
        //TODO: Remove, its still used in bomb-avoider
        this.lastDanger = this.currentDanger;
        this.currentDanger = dangerCurrent;
        
        //watch top
        deadlyTop = (getBombCounter(cP.getX(), cP.getY() - 1, 0, dangerAnalysis) <= 1);
        currentBombCounter = getMinBombCounter(cP.getX() -1, cP.getY() - 2, 3, 2, dangerAnalysis);
        dangerTop = evaluateBombCounter(currentBombCounter);
        
        //watch bot
        deadlyBot = (getBombCounter(cP.getX(), cP.getY() + 1, 0, dangerAnalysis) <= 1);                
        currentBombCounter = getMinBombCounter(cP.getX() - 1, cP.getY() + 1, 3, 2, dangerAnalysis);
        dangerBot = evaluateBombCounter(currentBombCounter);
        
        //watch left
        deadlyLeft = (getBombCounter(cP.getX() - 1, cP.getY(), 0, dangerAnalysis) <= 1);        
        currentBombCounter = getMinBombCounter(cP.getX() -2, cP.getY() - 1, 2, 3, dangerAnalysis);
        dangerLeft = evaluateBombCounter(currentBombCounter);
        
        //watch right
        deadlyRight =  (getBombCounter(cP.getX() + 1, cP.getY(), 0, dangerAnalysis) <= 1);
        currentBombCounter = getMinBombCounter(cP.getX() +1, cP.getY() -1, 2, 3, dangerAnalysis);
        dangerRight = evaluateBombCounter(currentBombCounter);

        
        result += (dangerCurrent * Math.pow(countZoneStatus, 0));
        result += (dangerTop     * Math.pow(countZoneStatus, 1));
        result += (dangerBot     * Math.pow(countZoneStatus, 2));
        result += (dangerLeft    * Math.pow(countZoneStatus, 3));
        result += (dangerRight   * Math.pow(countZoneStatus, 4));
        
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
    protected int getMinBombCounter(int startX, int startY, int spanX, int spanY, int[][] dangerAnalysis) {
        int initCounter = 99; //just a high value to start evaluation
        int result = initCounter;
        
        for(int x = startX; x < startX+spanX; x++)
        {
            for (int y = startY; y < startY + spanY; y++)
            {
                if (validX(x) && validY(y)) {
                    result = ((dangerAnalysis[x][y] < result) ? dangerAnalysis[x][y] : result);
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
    protected int evaluateBombCounter(int currentBombCounter) {
        int noDanger    = 0;
        int maybeDanger = 1;
        int highDanger  = 2;
        
        int counterHighDanger = 2;
        int counterMaybeDanger = 5;
        
        int result = noDanger;
        
        if ((currentBombCounter != 99) && (currentBombCounter <= counterHighDanger)) {
            result = highDanger;
        }
        else {
            if ((currentBombCounter == 99) || (currentBombCounter > counterMaybeDanger)) {
                result = noDanger;
            } else {
                result = maybeDanger; 
            }            
        }
        
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
    protected int getBombCounter(int x, int y, int stepsaway, int[][] dangerAnalysis) {
        int initCounter = 99; //just a high value to start evaluation
        int result = initCounter;
        
        if (validX(x) && validY(y)) {
            result = ((dangerAnalysis[x][y]) == initCounter ? initCounter : dangerAnalysis[x][y] - stepsaway);
        }             
        
        return result;
    }
    
    /* ************************************************************** */
    /**
     * determinePlayerOnBomb
     * @return
    */ /************************************************************* */
    protected int determinePlayerOnBomb() {
        Player cP = this.determineCurrentPlayer();
        Iterator<Bomb> bombs = board.getBombs().iterator();
        Bomb currentBomb = null;
        
        while (bombs.hasNext()) {
            currentBomb = bombs.next();
            if (currentBomb.getX() == cP.getX() && currentBomb.getY() == cP.getY()) {
                return 1;
            }
        }            
            
        return 0;
    }

    /**
     * This method returns in which direction and how far the closest hideout is.
     *
     * Far stands for reachable in 3 moves and close for reachable in 2 moves.
     * @return An integer version of all booleans combined
     */
    protected int determineEscapeRoute(){
        Player cp = this.determineCurrentPlayer();
        int cpXPos = cp.getX();
        int cpYPos = cp.getY();

        int LOWERBORDER = 3;

        int[][] dangerAnalysis = new int[board.getBoard().length][board.getBoard()[0].length];

        determineBombZones(dangerAnalysis);

        boolean upFar = false;
        boolean upClose = false;

        if (validX(cpXPos-3)){
            upFar = dangerAnalysis[cpXPos-3][cpYPos] > LOWERBORDER + 1;
        }
        if(validX(cpXPos-2)){
            upClose = dangerAnalysis[cpXPos-2][cpYPos] > LOWERBORDER;
            if (!upFar && (validY(cpYPos-1) || validY(cpYPos+1))){
                upFar = dangerAnalysis[cpXPos-2][cpYPos -1] > LOWERBORDER + 1 || dangerAnalysis[cpXPos - 2][cpYPos + 1] > LOWERBORDER + 1;
            }
        }
        if (!upClose && validX(cpXPos-1) && (validY(cpYPos-1) || validY(cpYPos+1))){
            upClose = dangerAnalysis[cpXPos-1][cpYPos-1] > 3 || dangerAnalysis[cpXPos-1][cpYPos+1] > LOWERBORDER;
        }

        boolean rightFar = false;
        boolean rightClose = false;

        if (validY(cpYPos + 3)){
            rightFar = dangerAnalysis[cpXPos][cpYPos + 3] > LOWERBORDER + 1;
        }
        if(validY(cpYPos + 2)){
            rightClose = dangerAnalysis[cpXPos][cpYPos + 2] > LOWERBORDER;
            if (!rightFar && (validX(cpXPos + 1) || validX(cpXPos - 1))){
                rightFar = dangerAnalysis[cpXPos - 1][cpYPos + 2] > LOWERBORDER + 1 || dangerAnalysis[cpXPos + 1][cpYPos + 2] > LOWERBORDER + 1;
            }
        }
        if (!rightClose && validY(cpYPos + 1) && (validX(cpXPos - 1) || validX(cpXPos + 1))){
            rightClose = dangerAnalysis[cpXPos+1][cpYPos+1] > 3 || dangerAnalysis[cpXPos-1][cpYPos+1] > LOWERBORDER;
        }

        boolean downFar = false;
        boolean downClose = false;

        if (validX(cpXPos+3)){
            downFar = dangerAnalysis[cpXPos+3][cpYPos] > LOWERBORDER + 1;
        }
        if(validX(cpXPos+2)){
            downClose = dangerAnalysis[cpXPos+2][cpYPos] > LOWERBORDER;
            if (!downFar && (validY(cpYPos-1) || validY(cpYPos+1))){
                downFar = dangerAnalysis[cpXPos+2][cpYPos -1] > LOWERBORDER + 1 || dangerAnalysis[cpXPos + 2][cpYPos + 1] > LOWERBORDER + 1;
            }
        }
        if (!downClose && validX(cpXPos+1) && (validY(cpYPos-1) || validY(cpYPos+1))){
            downClose = dangerAnalysis[cpXPos+1][cpYPos-1] > 3 || dangerAnalysis[cpXPos+1][cpYPos+1] > LOWERBORDER;
        }

        boolean leftFar = false;
        boolean leftClose = false;

        if (validY(cpYPos - 3)){
            leftFar = dangerAnalysis[cpXPos][cpYPos - 3] > LOWERBORDER + 1;
        }
        if(validY(cpYPos - 2)){
            leftClose = dangerAnalysis[cpXPos][cpYPos - 2] > LOWERBORDER;
            if (!leftFar && (validX(cpXPos + 1) || validX(cpXPos - 1))){
                leftFar = dangerAnalysis[cpXPos - 1][cpYPos - 2] > LOWERBORDER + 1 || dangerAnalysis[cpXPos + 1][cpYPos - 2] > LOWERBORDER + 1;
            }
        }
        if (!leftClose && validY(cpYPos - 1) && (validX(cpXPos - 1) || validX(cpXPos + 1))){
            leftClose = dangerAnalysis[cpXPos+1][cpYPos-1] > 3 || dangerAnalysis[cpXPos-1][cpYPos-1] > LOWERBORDER;
        }

        int result = 0;

        result += upFar ? 1 : 0;
        result += upClose ? 2 : 0;
        result += rightFar ? 4 : 0;
        result += rightClose ? 8 : 0;
        result += downFar ? 16 : 0;
        result += downClose ? 32 : 0;
        result += leftFar ? 64 : 0;
        result += leftClose ? 128 : 0;

        return result;
    }
}
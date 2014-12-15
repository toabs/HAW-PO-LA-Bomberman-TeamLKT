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
    
    //Variables used for state-count-determination
    protected final int freeDirections = (int) Math.pow(2, 4); //4 direction, blocked, not blocked
    protected final int playerOnBombValues = 1;
    protected final int oppenentDirections = 9; //equal, top, left, topleft, ...
    protected final int bombSituations = 125;
    protected double maxDistanceToOpponent = 0; //to be calculated
    protected final int numIntegers = 4;
    protected final int numDoubles = 1;
    protected final int escapePaths = 256;

    public abstract void setPlayboard(Playboard playboard, int userID);

    protected static void environmentLogln(String output, DebugState debugState){
        if (debugState != null) {
            if (debugState.getEnvironmentDebugState()){
                System.out.println(output);
            }
        }
    }

    protected static void environmentLog(String output, DebugState debugState){
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
    protected static Player determineCurrentPlayer(Playboard board, int userID) {
        Iterator<Player> it = board.getPlayers().iterator();
    
        Player thisplayer = null;
        while(it.hasNext())
        {
            thisplayer = it.next();
            if(thisplayer.getId() == userID)
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
    protected static Player determineOppenentPlayer(Playboard board, int userID) {
        Iterator<Player> it = board.getPlayers().iterator();
    
        Player player = null;
        while(it.hasNext())
        {
            player = it.next();
            if(player.getId() != userID)
                break;
        }
    
        return player;
    }
    
    /* ************************************************************** */
    /**
     * determineOpponentDirection
     * @return
    */ /************************************************************* */
    protected static int determineOpponentDirection(int currentPlayerX, int currentPlayerY, int opponentX, int opponentY, DebugState debugState) {
        
        if (currentPlayerX == opponentX && currentPlayerY == opponentY) return 1; //equal
        
        //determine
        boolean top = (opponentY > currentPlayerY);
        boolean bot = (opponentY < currentPlayerY);
        boolean left = (opponentX < currentPlayerX);
        boolean right = (opponentX > currentPlayerX);
        
        if (top && !left && !right)     return 2; //top
        if (top && left && !right)      return 3; //topleft
        if (top && !left && right)      return 4; //topright
        if (right && !top && !bot)      return 5; //right
        if (left && !top && !bot)       return 6; //left
        if (bot && !left && !right)     return 7; //bot
        if (bot && left && !right)      return 8; //botleft
        if (bot && !left && right)      return 9; //botright
        
        environmentLogln("OpponentDirection error.", debugState);
        return 0;
    }    

    /* ************************************************************** */
    /**
     * determineDistanceToOpponent
     * @return
     */ /************************************************************* */
    protected static double determineDistanceToOpponent(int currentPlayerX, int currentPlayerY, int opponentX, int opponentY)
    {
    
        int diffx = currentPlayerX - opponentX;
        int diffy = currentPlayerY - opponentY;
    
        return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));

        //double result = Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));
        /*
        if (result < (this.board.getExplosionRadius() + this.distanceRadiusOffset)) {
            return result;
        } else {
            return (this.board.getExplosionRadius() + this.distanceRadiusOffset);
        } */
    }
    
    /* ************************************************************** */
    /**
     * validX
     * @param x
     * @return
    */ /************************************************************* */
    protected static boolean validX(int x, int boardLengthX) {
       return ((x < boardLengthX) && (x >= 0));
    }
      
    /* ************************************************************** */
    /**
     * validY
     * @param y
     * @return
    */ /************************************************************* */
    protected static boolean validY(int y, int boardLengthY) {
        return ((y < boardLengthY) && (y >= 0 ));
    }
    

    protected static int determinefreeDirections(int currentPlayerX, int currentPlayerY, int boardXLength, int boardYLength, Playboard board, DirectionValues directionValues) {
        boolean[][] bombPosition = new boolean[boardXLength][boardYLength];
        Bomb currentBomb = null;
        
        //initilize with false
        //initilize with initValue
        for(int i = 0; i < boardXLength; i++)
        {
            for (int n = 0; n < boardYLength; n++)
            {
                bombPosition[i][n] = false;
            }
        }
        
        //fill bomb-position array
        for (Bomb bomb : board.getBombs()) {
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

        directionValues.upFree =   (validY(currentPlayerY - 1, boardYLength) && !board.getBoard()[currentPlayerX][currentPlayerY - 1].isWall()
                && !bombPosition[currentPlayerX][currentPlayerY - 1]);
        directionValues.rightFree = (validX(currentPlayerX + 1, boardXLength) && !board.getBoard()[currentPlayerX + 1][currentPlayerY].isWall()
                && !bombPosition[currentPlayerX + 1][currentPlayerY]);
        directionValues.downFree =   (validY(currentPlayerY + 1, boardYLength) && !board.getBoard()[currentPlayerX][currentPlayerY + 1].isWall()
                && !bombPosition[currentPlayerX][currentPlayerY + 1]);
        directionValues.leftFree =  (validX(currentPlayerX - 1, boardXLength) && !board.getBoard()[currentPlayerX - 1][currentPlayerY].isWall()
                && !bombPosition[currentPlayerX - 1][currentPlayerY]);

        result += (directionValues.upFree) ? Math.pow(2, 0) : 0;
        result += (directionValues.rightFree) ? Math.pow(2, 1) : 0;
        result += (directionValues.downFree) ? Math.pow(2, 2) : 0;
        result += (directionValues.leftFree) ? Math.pow(2, 3) : 0;

        return result;
    }

    /* ************************************************************** */
    /**
     * determineBombZones
     * @param dangerAnalysis
    */ /************************************************************* */
    protected static int[][] determineBombZones(int boardXLength, int boardYLength, Playboard board)
    {
        int[][] dangerAnalysis = new int[boardXLength][boardYLength];

        Set<Bomb> bomblist = board.getBombs();
        TreeMap<Integer, Bomb> bombByTimeleft = new TreeMap<>();
        int tempBombCounter = 0;
        int initValue = 99;

        boolean topwall;
        boolean botwall;
        boolean leftwall;
        boolean rightwall;

        //initilize with initValue
        for(int i = 0; i < boardXLength; i++)
        {
            for (int n = 0; n < boardYLength; n++)
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
            int currentBombX = currentBomb.getX();
            int currentBombY = currentBomb.getY();
            int currentBombExplosionRadius = currentBomb.getExplosionRadius();

            //Determine shortest bombcounter
            tempBombCounter = dangerAnalysis[currentBombX][currentBombY];

            if (tempBombCounter < 0 || tempBombCounter > currentBomb.getCounter()) {
                tempBombCounter = currentBomb.getCounter();
            }

            dangerAnalysis[currentBombX][currentBombY] = tempBombCounter;

            //radius
            //todo: one var for each way, to check for walls and stop
            for (int i = 1; i < currentBombExplosionRadius; i++) {
                //top
                if (validY(currentBombY + i, boardYLength) && !topwall) {
                    if (board.getBoard()[currentBombX][currentBombY + i].isWall()) {
                       topwall = true;
                    }
                    else {
                        if (dangerAnalysis[currentBombX][currentBombY + i] > tempBombCounter
                            || dangerAnalysis[currentBombX][currentBombY + i] == initValue) {
                            dangerAnalysis[currentBombX][currentBombY + i] = tempBombCounter;
                        }
                    }
                }
                //bot
                if (validY(currentBombY - i, boardYLength) && !botwall) {
                    if (board.getBoard()[currentBombX][currentBombY - i].isWall()) {
                        botwall = true;
                     }
                     else {
                        if (dangerAnalysis[currentBombX][currentBombY - i] > tempBombCounter
                            || dangerAnalysis[currentBombX][currentBombY - i] == initValue) {
                            dangerAnalysis[currentBombX][currentBombY - i] = tempBombCounter;
                        }
                    }
                }
                //left
                if (validX(currentBombX - i, boardXLength) && !leftwall) {
                    if (board.getBoard()[currentBombX - i][currentBombY].isWall()) {
                        leftwall = true;
                    }
                    else {
                        if (dangerAnalysis[currentBombX - i][currentBombY] > tempBombCounter
                            || dangerAnalysis[currentBombX - i][currentBombY] == initValue) {
                            dangerAnalysis[currentBombX - i][currentBombY] = tempBombCounter;
                        }
                    }
                }
                //right
                if (validX(currentBombX + i, boardXLength) && !rightwall) {
                    if (board.getBoard()[currentBombX + i][currentBombY].isWall()) {
                        rightwall = true;
                    }
                    else {
                        if (dangerAnalysis[currentBombX + i][currentBombY] > tempBombCounter
                            || dangerAnalysis[currentBombX + i][currentBombY] == initValue) {
                            dangerAnalysis[currentBombX + i][currentBombY] = tempBombCounter;
                        }
                    }
                }
            }
        }
        return dangerAnalysis;
    }

    protected static int determineBombSituation(int boardXLength, int boardYLength, int currentPlayerX, int currentPlayerY, int[][] dangerAnalysis, DebugState debugState, DirectionValues directionValues)
    {
        int currentBombCounter = 0;

        int countZoneStatus = 3; // count of possible danger

        int dangerCurrent = 0;
        int dangerTop = 0;
        int dangerBot = 0;
        int dangerLeft = 0;
        int dangerRight = 0;

        int result = 0;

        //print out dangerAnalysis
        for(int i = 0; i < boardXLength; i++)
        {
            for (int n = 0; n < boardYLength; n++)
            {
                if (n == currentPlayerX && i == currentPlayerY) {
                    environmentLog("[XX]", debugState);
                } else {
                    environmentLog("[" + dangerAnalysis[n][i] + "]", debugState);
                }
            }
            environmentLogln("", debugState);
        }

        //TODO: Figure out a good way to relocate the deadly-(direction) function. We still need it!
        //current Position
        currentBombCounter = dangerAnalysis[currentPlayerX][currentPlayerY];
        directionValues.deadlyCurrent =  (currentBombCounter <= 1);
        dangerCurrent = evaluateBombCounter(currentBombCounter);

        //watch top
        directionValues.deadlyUp = (getBombCounter(currentPlayerX, currentPlayerY - 1, 0, dangerAnalysis) <= 1);
        currentBombCounter = getMinBombCounter(currentPlayerX -1, currentPlayerY - 2, 3, 2, dangerAnalysis);
        dangerTop = evaluateBombCounter(currentBombCounter);

        //watch bot
        directionValues.deadlyDown = (getBombCounter(currentPlayerX, currentPlayerY + 1, 0, dangerAnalysis) <= 1);
        currentBombCounter = getMinBombCounter(currentPlayerX - 1, currentPlayerY + 1, 3, 2, dangerAnalysis);
        dangerBot = evaluateBombCounter(currentBombCounter);

        //watch left
        directionValues.deadlyLeft = (getBombCounter(currentPlayerX - 1, currentPlayerY, 0, dangerAnalysis) <= 1);
        currentBombCounter = getMinBombCounter(currentPlayerX -2, currentPlayerY - 1, 2, 3, dangerAnalysis);
        dangerLeft = evaluateBombCounter(currentBombCounter);

        //watch right
        directionValues.deadlyRight =  (getBombCounter(currentPlayerX + 1, currentPlayerY, 0, dangerAnalysis) <= 1);
        currentBombCounter = getMinBombCounter(currentPlayerX +1, currentPlayerY -1, 2, 3, dangerAnalysis);
        dangerRight = evaluateBombCounter(currentBombCounter);


        result += (dangerCurrent * Math.pow(countZoneStatus, 0));
        result += (dangerTop     * Math.pow(countZoneStatus, 1));
        result += (dangerBot     * Math.pow(countZoneStatus, 2));
        result += (dangerLeft    * Math.pow(countZoneStatus, 3));
        result += (dangerRight   * Math.pow(countZoneStatus, 4));

        environmentLogln("DangerCurrent=" + dangerCurrent, debugState);
        environmentLogln("DangerTop=" + dangerTop, debugState);
        environmentLogln("DangerBot=" + dangerBot, debugState);
        environmentLogln("DangerLeft=" + dangerLeft, debugState);
        environmentLogln("DangerRight=" + dangerRight, debugState);

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
    protected static int getMinBombCounter(int startX, int startY, int spanX, int spanY, int[][] dangerAnalysis) {
        int initCounter = 99; //just a high value to start evaluation
        int result = initCounter;

        for(int x = startX; x < startX+spanX; x++)
        {
            for (int y = startY; y < startY + spanY; y++)
            {
                if (validX(x, dangerAnalysis.length) && validY(y, dangerAnalysis[0].length)) {
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
    protected static int evaluateBombCounter(int currentBombCounter) {
        int noDanger    = 0;
        int maybeDanger = 1;
        int highDanger  = 2;

        int counterHighDanger = 2;
        int counterMaybeDanger = 5;

        int result;

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
    protected static int getBombCounter(int x, int y, int stepsaway, int[][] dangerAnalysis) {
        int initCounter = 99; //just a high value to start evaluation
        int result = initCounter;

        if (validX(x, dangerAnalysis.length) && validY(y, dangerAnalysis[0].length)) {
            result = ((dangerAnalysis[x][y]) == initCounter ? initCounter : dangerAnalysis[x][y] - stepsaway);
        }

        return result;
    }

    /* ************************************************************** */
    /**
     * determinePlayerOnBomb
     * @return
    */ /************************************************************* */
    protected static int determinePlayerOnBomb(int currentPlayerX, int currentPlayerY, Playboard board) {
        Iterator<Bomb> bombs = board.getBombs().iterator();
        Bomb currentBomb = null;

        while (bombs.hasNext()) {
            currentBomb = bombs.next();
            if (currentBomb.getX() == currentPlayerX && currentBomb.getY() == currentPlayerY) {
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
    protected static int determineEscapeRoute(int currentPlayerX, int currentPlayerY, int[][] dangerAnalysis, int boardXLength, int boardYLength){
        int cpXPos = currentPlayerX;
        int cpYPos = currentPlayerY;

        int LOWERBORDER = 3;

        boolean upFar = false;
        boolean upClose = false;

        if (validX(cpXPos-3, boardXLength)){
            upFar = dangerAnalysis[cpXPos-3][cpYPos] > LOWERBORDER + 1;
        }
        if(validX(cpXPos-2, boardXLength)){
            upClose = dangerAnalysis[cpXPos-2][cpYPos] > LOWERBORDER;
            if (!upFar){
                if(validY(cpYPos - 1, boardYLength)){
                    upFar = dangerAnalysis[cpXPos-2][cpYPos -1] > LOWERBORDER + 1;
                }
                if(!upFar && validY(cpYPos + 1, boardYLength)){
                    upFar = dangerAnalysis[cpXPos - 2][cpYPos + 1] > LOWERBORDER + 1;
                }
            }
        }
        if (!upClose && validX(cpXPos - 1, boardXLength)){
            if(validY(cpYPos - 1, boardYLength)){
                upClose = dangerAnalysis[cpXPos - 1][cpYPos -1] > LOWERBORDER + 1;
            }
            if(!upClose && validY(cpYPos + 1, boardYLength)){
                upClose = dangerAnalysis[cpXPos - 1][cpYPos + 1] > LOWERBORDER + 1;
            }
        }

        boolean rightFar = false;
        boolean rightClose = false;

        if (validY(cpYPos + 3, boardYLength)){
            rightFar = dangerAnalysis[cpXPos][cpYPos + 3] > LOWERBORDER + 1;
        }
        if(validY(cpYPos + 2, boardYLength)){
            rightClose = dangerAnalysis[cpXPos][cpYPos + 2] > LOWERBORDER;
            if (!rightFar){
                if(validX(cpXPos + 1, boardXLength)){
                    rightFar = dangerAnalysis[cpXPos + 1][cpYPos + 2] > LOWERBORDER + 1;
                }
                if(!rightFar && validX(cpXPos - 1, boardXLength)){
                    rightFar = dangerAnalysis[cpXPos - 1][cpYPos + 2] > LOWERBORDER + 1;
                }
            }
        }
        if (!rightClose && validY(cpYPos + 1, boardYLength)){
            if(validX(cpXPos - 1, boardXLength)){
                rightClose = dangerAnalysis[cpXPos - 1][cpYPos + 1] > LOWERBORDER + 1;
            }
            if(!rightClose && validX(cpXPos + 1, boardXLength)){
                rightClose = dangerAnalysis[cpXPos + 1][cpYPos + 1] > LOWERBORDER + 1;
            }
        }

        boolean downFar = false;
        boolean downClose = false;

        if (validX(cpXPos+3, boardXLength)){
            downFar = dangerAnalysis[cpXPos+3][cpYPos] > LOWERBORDER + 1;
        }
        if(validX(cpXPos+2, boardXLength)){
            downClose = dangerAnalysis[cpXPos+2][cpYPos] > LOWERBORDER;
            if (!downFar){
                if(validY(cpYPos - 1, boardYLength)){
                    downFar = dangerAnalysis[cpXPos + 2][cpYPos -1] > LOWERBORDER + 1;
                }
                if(!upFar && validY(cpYPos + 1, boardYLength)){
                    downFar = dangerAnalysis[cpXPos + 2][cpYPos + 1] > LOWERBORDER + 1;
                }
            }
        }
        if (!downClose && validX(cpXPos + 1, boardXLength)){
            if(validY(cpYPos - 1, boardYLength)){
                downClose = dangerAnalysis[cpXPos + 1][cpYPos -1] > LOWERBORDER + 1;
            }
            if(!downClose && validY(cpYPos + 1, boardYLength)){
                downClose = dangerAnalysis[cpXPos + 1][cpYPos + 1] > LOWERBORDER + 1;
            }
        }

        boolean leftFar = false;
        boolean leftClose = false;

        if (validY(cpYPos - 3, boardYLength)){
            leftFar = dangerAnalysis[cpXPos][cpYPos - 3] > LOWERBORDER + 1;
        }
        if(validY(cpYPos - 2, boardYLength)){
            leftClose = dangerAnalysis[cpXPos][cpYPos - 2] > LOWERBORDER;
            if (!leftFar){
                if(validX(cpXPos + 1, boardXLength)){
                    leftFar = dangerAnalysis[cpXPos + 1][cpYPos - 2] > LOWERBORDER + 1;
                }
                if(!leftFar && validX(cpXPos - 1, boardXLength)){
                    leftFar = dangerAnalysis[cpXPos - 1][cpYPos - 2] > LOWERBORDER + 1;
                }
            }
        }
        if (!leftClose && validY(cpYPos - 1, boardYLength)){
            if(validX(cpXPos - 1, boardXLength)){
                leftClose = dangerAnalysis[cpXPos - 1][cpYPos - 1] > LOWERBORDER + 1;
            }
            if(!leftClose && validX(cpXPos + 1, boardXLength)){
                leftClose = dangerAnalysis[cpXPos + 1][cpYPos - 1] > LOWERBORDER + 1;
            }
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

    protected static int determineSpacesToEdge(int opponentPlayerX, int opponentPlayerY, int boardX, int boardY) {
        int result = 0;

        int maxX = boardX;
        int maxY = boardY;

        int minDiffX = (Math.abs(opponentPlayerX - 0) < Math.abs(opponentPlayerX - maxX) ? Math.abs(opponentPlayerX - 0) : Math.abs(opponentPlayerX - maxX));
        int minDiffY = (Math.abs(opponentPlayerY - 0) < Math.abs(opponentPlayerY - maxY) ? Math.abs(opponentPlayerY - 0) : Math.abs(opponentPlayerY - maxY));

        result = (minDiffX < minDiffY) ? minDiffX : minDiffY;

        return result;
    }

    protected class DirectionValues{
        public boolean upFree = false;
        public boolean rightFree = false;
        public boolean downFree = false;
        public boolean leftFree = false;

        public boolean deadlyUp = false;
        public boolean deadlyRight = false;
        public boolean deadlyDown = false;
        public boolean deadlyLeft = false;
        public boolean deadlyCurrent = false;
    }
}
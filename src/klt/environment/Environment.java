package klt.environment;

import Core.Bomb;
import Core.Playboard;
import Core.Player;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author LarsE
 * 13.10.2014
 *
 * This is the abstract class for all environments.
 * Here we collected all methods we use more than once and frequent in all extending environments.
 * The methods are static so they are more easy to test with JUnit tests.
 */
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

    /**
     * These informations are crucial for almost all methods in this class. That's why all extending classes need those informations too.
     *
     * @param playboard     Is the playboard provided by the KI after the creation.
     * @param userID        Is the userID provided by the KI after the creation.
     */
    public abstract void setPlayboard(Playboard playboard, int userID);

    /**
     * Prints the output over the standart output if logging is enabled for environments in the DebugState for this KI.
     *
     * @param output        The output as a string.
     * @param debugState    The debugState.
     */
    protected static void environmentLogln(String output, DebugState debugState){
        if (debugState != null) {
            if (debugState.getEnvironmentDebugState()){
                System.out.println(output);
            }
        }
    }

    /**
     * Prints the output over the standart output if logging is enabled for environments in the DebugState for this KI.
     *
     * @param output        The output as a string.
     * @param debugState    The debugState.
     */
    protected static void environmentLog(String output, DebugState debugState){
        if (debugState != null) {
            if (debugState.getEnvironmentDebugState()){
                System.out.print(output);
            }
        }
    }

    /**
     * Finds and returns the player with the provided userID.
     *
     * @param board     The playboard instance you work with at the moment.
     * @param userID    The userID you search for.
     * @return          The Player with the provided userID. Returns null if the user is not found.
     */
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

    /**
     * Finds and returns the player who does not use the provided userID.
     *
     * @param board     The playboard instance you work with at the moment.
     * @param userID    The userID you search for.
     * @return          The Player who does not use the provided userID. Returns null if the user is not found.
     */
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

    /**
     * Determines in which direction the Opponent is compared to the player.
     * The directions are like those on a compass. 2 = N, 3 = NW, 4 = NE, 5 = E, 6 = W, 7 = S, 8 = SW, 9 = SE and 1 for the case that they're both on the same spot.
     *
     * @param currentPlayerX    The current players X coordinate.
     * @param currentPlayerY    The current players Y coordinate.
     * @param opponentX         The opponents X coordinate.
     * @param opponentY         The opponents Y coordinate.
     * @param debugState        The debugState.
     * @return      An integer for 1 to 9 (including) with the values as stated above.
     */
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

    /**
     * Calcutlates the distance to the opponent.
     *
     * @param currentPlayerX    The current players X coordinate.
     * @param currentPlayerY    The current players Y coordinate.
     * @param opponentX         The opponents X coordinate.
     * @param opponentY         The opponents Y coordinate.
     * @return          The distance to the opponent.
     */
    protected static double determineDistanceToOpponent(int currentPlayerX, int currentPlayerY, int opponentX, int opponentY)
    {
    
        int diffx = currentPlayerX - opponentX;
        int diffy = currentPlayerY - opponentY;
    
        return Math.sqrt(Math.pow(diffx, 2) + Math.pow(diffy, 2));
    }
    
    /**
     * Returns the distance capped to a certain value.
     *
     * @param distance      The distance to cap.
     * @param cap           The cap value.
     * @return      The capped distance.
    */
    protected static double returnCappedDistance(double distance, double cap) {
          if (distance < (cap)) {
            return distance;
        } else {
            return (cap);
        }  
    }

    /**
     * Checks if the provided X value is still an index found on the board.
     *
     * @param x                 The value to check.
     * @param boardLengthX      The length of the board in X dimension.
     * @return      True for 0 =< x < boardLengthX.
     */
    protected static boolean validX(int x, int boardLengthX) {
       return ((x < boardLengthX) && (x >= 0));
    }

    /**
     * Checks if the provided Y value is still an index found on the board.
     *
     * @param y                 The value to check.
     * @param boardLengthY      The length of the board in Y dimension.
     * @return      True for 0 =< y < boardLengthY
     */
    protected static boolean validY(int y, int boardLengthY) {
        return ((y < boardLengthY) && (y >= 0 ));
    }

    /**
     * Determines which of the four directions are free.
     * The binary version of the int number looks like this: 0b(leftFree)(downFree)(rightFree)(upFree)
     *
     *
     * @param currentPlayerX    The current players X coordinate.
     * @param currentPlayerY    The current players Y coordinate.
     * @param boardXLength      The length of the board in X dimension.
     * @param boardYLength      The length of the board in Y dimension.
     * @param board             The current playboard.
     * @param directionValues   The current instance of DirectionValues to store the values in.
     * @return      An integer which tells directly which direction is free and which is not.
     */
    protected static int determinefreeDirections(int currentPlayerX, int currentPlayerY, int boardXLength, int boardYLength, Playboard board, DirectionValues directionValues)
    {
        boolean[][] bombPosition = new boolean[boardXLength][boardYLength];
        Bomb currentBomb;
        
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

    /**
     * Determines how many steps are left on every field of the board before an explosion hits the field.
     *
     * @param boardXLength  The length of the board in X dimension.
     * @param boardYLength  The length of the board in Y dimension.
     * @param board         The current playboard.
     * @return      Returns an two dimensional array which contains the steps left before an explosion hits a field.
     */
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

    /**
     * Determines how dangerous it is to go in a certian direction according to the distribution and the status of the bombs on the board.
     *
     * @param boardXLength      The length of the board in X dimension.
     * @param boardYLength      The length of the board in Y dimension.
     * @param currentPlayerX    The current players X coordinate.
     * @param currentPlayerY    The current players Y coordinate.
     * @param dangerAnalysis    The current status of how much steps are left on each field till it explodes.
     * @param debugState        The debugState.
     * @param directionValues   The current instance of DirectionValues to store the values in.
     * @return      Returns an int which encodes for each direction (including the current position) how dangerous it is.
     */
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

    /**
     * Returns the smallest value in a defined rectangle in the two dimensional array.
     * It's used in this case to find the shortest remaining time till an explosion happens in a predefined extract of the board.
     *
     * @param startX    X startpoint for the evaluation.
     * @param startY    Y startpoint for the evaluation.
     * @param spanX     Delta to check in in the X dimension.
     * @param spanY     Delta to check in in the Y dimension.
     * @param dangerAnalysis    The array to search in.
     * @return      The smallest number in the defined region in the delivered array.
     */
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

    /**
     * Returns a value from 0-2, depending on how dangerous the counter is.
     *
     * @param currentBombCounter    The counter to evaluate.
     * @return      The value the counter was assigned to.
    */
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

    /**
     * Returns the steps left till explosion on a certain field regarding the steps it took to get there.
     *
     * @param x                 The x coordinate to check.
     * @param y                 The y coordinate to check.
     * @param stepsaway         The steps it takes to get there.
     * @param dangerAnalysis    The steps left till explosion on all fields on the board.
     * @return      The steps left on that field.
     */
    protected static int getBombCounter(int x, int y, int stepsaway, int[][] dangerAnalysis) {
        int initCounter = 99; //just a high value to start evaluation
        int result = initCounter;

        if (validX(x, dangerAnalysis.length) && validY(y, dangerAnalysis[0].length)) {
            result = ((dangerAnalysis[x][y]) == initCounter ? initCounter : dangerAnalysis[x][y] - stepsaway);
        }

        return result;
    }

    /**
     * Checks if a player is currently on a bomb.
     *
     * @param currentPlayerX    X coordinate to check.
     * @param currentPlayerY    Y coordinate to check.
     * @param board             The current playboard.
     * @return      Is there a bomb on the specified field?
     */
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
     *
     * @param currentPlayerX    The current players X coordinate.
     * @param currentPlayerY    The current players Y coordinate.
     * @param dangerAnalysis    The steps left till explosion on all fields on the board.
     * @param boardXLength      The length of the board in X dimension.
     * @param boardYLength      The length of the board in Y dimension.
     * @return      Returns an integer which shows in which direction a possible escape route is.
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

    /**
     * Checks how much fields are left to the closest playboard border.
     *
     * @param opponentPlayerX         The opponents X coordinate.
     * @param opponentPlayerY         The opponents Y coordinate.
     * @param boardXLength      The length of the board in X dimension.
     * @param boardYLength      The length of the board in Y dimension.
     * @return      Fields to the closest playboard border.
     */
    protected static int determineSpacesToEdge(int opponentPlayerX, int opponentPlayerY, int boardXLength, int boardYLength) {
        int result = 0;

        int maxX = boardXLength;
        int maxY = boardYLength;

        int minDiffX = (Math.abs(opponentPlayerX - 0) < Math.abs(opponentPlayerX - maxX) ? Math.abs(opponentPlayerX - 0) : Math.abs(opponentPlayerX - maxX));
        int minDiffY = (Math.abs(opponentPlayerY - 0) < Math.abs(opponentPlayerY - maxY) ? Math.abs(opponentPlayerY - 0) : Math.abs(opponentPlayerY - maxY));

        result = (minDiffX < minDiffY) ? minDiffX : minDiffY;

        return result;
    }

    /**
     * A small inner class to store the free and unsafe directions to move to in.
     * It is used to allow the agent to only choose from 'useful' actions. (Running against a wall is NOT useful.)
     */
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
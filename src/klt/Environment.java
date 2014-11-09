/**
 * 
 */
package klt;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;

import Core.Bomb;
import Core.Playboard;
import Core.Player;

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
    protected final int oppenentDirections = 9; //equal, top, left, topleft, ...
    protected final int bombSituations = 125;
    protected double maxDistanceToOpponent = 0; //to be calculated
    protected final int numIntegers = 3;
    protected final int numDoubles = 1;
    
    //helper variables filled by the functions
    protected boolean topfree = false;
    protected boolean botfree = false;
    protected boolean leftfree = false;
    protected boolean rightfree = false;
    
    //Variables used for reward-determination
    protected double lastDistance = 0;
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
    void setPlayboard(Playboard playboard, int userID)
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
        Iterator<Bomb> bIt = this.board.getBombs().iterator();
        while(bIt.hasNext())
        {
            currentBomb = bIt.next();
            bombPosition[currentBomb.getX()][currentBomb.getY()] = true;
        }
        
        int result = 1;
        
        this.topfree =   (validY(currentPlayer.getY()+1) ? !board.getBoard()[currentPlayer.getX()][currentPlayer.getY()+1].isWall()
                                                            && !bombPosition[currentPlayer.getX()][currentPlayer.getY()+1] : false);
        this.rightfree = (validX(currentPlayer.getX()+1) ? !board.getBoard()[currentPlayer.getX()+1][currentPlayer.getY()].isWall()
                                                            && !bombPosition[currentPlayer.getX()+1][currentPlayer.getY()]: false);
        this.botfree =   (validY(currentPlayer.getY()-1) ? !board.getBoard()[currentPlayer.getX()][currentPlayer.getY()-1].isWall()
                                                            && !bombPosition[currentPlayer.getX()][currentPlayer.getY()-1]: false);
        this.leftfree =  (validX(currentPlayer.getX()-1) ? !board.getBoard()[currentPlayer.getX()-1][currentPlayer.getY()].isWall()
                                                            && !bombPosition[currentPlayer.getX()-1][currentPlayer.getY()]: false);
        
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

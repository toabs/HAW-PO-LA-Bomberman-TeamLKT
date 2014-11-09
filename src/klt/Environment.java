/**
 * 
 */
package klt;

import java.util.Iterator;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;

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
    private DebugState debugState;

    Environment(DebugState debugState){
        super();
        this.debugState = debugState;
    }

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
        if (debugState.getEnvironmentDebugState()){
            System.out.println(output);
        }
    }

    protected void environmentLog(String output){
        if (debugState.getEnvironmentDebugState()){
            System.out.print(output);
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
}

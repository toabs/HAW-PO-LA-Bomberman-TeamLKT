/**
 * 
 */
package klt;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;

import Core.Playboard;

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
}

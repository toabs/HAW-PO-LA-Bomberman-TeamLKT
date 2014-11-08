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
}

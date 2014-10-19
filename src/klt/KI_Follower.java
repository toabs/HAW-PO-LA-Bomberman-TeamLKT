/**
 * 
 */
package klt;

/* ************************************************************** */
/**
 * @author LarsE
 * 19.10.2014
 */
/* *********************************************************** */
public class KI_Follower extends KI
{
    /* ************************************************************** */
    /**
     * KI_Follower
     * @param id
     * @param agent
     * @param environment
    */ /************************************************************* */
    public KI_Follower(int id, Agent agent, Environment environment)
    {
        super(id, new Agent_Follower(), new Environment_Follower());
    }
}

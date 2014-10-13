/**
 * 
 */
package klt;

/* ************************************************************** */
/**
 * @author ToRoSaR
 * 13.10.2014
 */
/* *********************************************************** */
public class KI_Random extends KI
{

    /* ************************************************************** */
    /**
     * KI_Random
     * @param id
     * @param agent
     * @param environment
    */ /************************************************************* */
    public KI_Random(int id)
    {
        super(id, new Agent_Random(), new Environment_Random());
    }

}

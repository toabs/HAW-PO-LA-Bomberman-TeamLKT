/**
 * 
 */
package klt;

/* ************************************************************** */
/**
 * @author Lars
 * 19.10.2014
 */
/* *********************************************************** */
public class KI_Factory
{
    /* ************************************************************** */
    /**
     * getKI_Random
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getKI_Random(int id)
    {
        try
        {
            return new KI(id, new Agent_Random(null), new Environment_Random());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            return null;
        }
    }
    
    /* ************************************************************** */
    /**
     * getKI_Random
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getKI_Follower(int id)
    {
        try
        {
            return new KI(id, new Agent_Follower("KI_Follower.rgo"), new Environment_Follower());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            return null;
        }
    }
}

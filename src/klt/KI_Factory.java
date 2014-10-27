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

    public static KI getKI_Escape(int id){
        try{
            return new KI(id, new Agent_Follower("KI_Escape.rgo"), new Environment_Escape());
        } catch (Exception e){
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }
    
    /* ************************************************************** */
    /**
     * getKI_Random
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getKI_Avoidbomb_Zone(int id)
    {
        try
        {
            return new KI(id, new Agent_Avoidbomb_Zone("KI_Avoidbomb_Zone.rgo"), new Environment_Avoidbomb_Zone());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /* ************************************************************** */
    /**
     * getKI_SARSA_Follower
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getKI_SARSA_Follower(int id)
    {
        try
        {
            return new KI(id, new Agent_SARSA("KI_FollowerSARSA.rgo"), new Environment_Follower());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

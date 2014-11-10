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
     * getKI_Escape
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getKI_Escape(int id){
        try{
            return new KI(id, new Agent_Simple("KI_Escape.rgo"), new Environment_Escape());
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
            return new KI(id, new Agent_Simple("KI_Follower.rgo"), new Environment_Follower());
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
            return new KI(id, new Agent_SARSA("KI_AvoidBombSARSA.rgo"), new Environment_Avoidbomb_Zone());
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

    /* ************************************************************** */
    /**
     * getKI_SARSA_Lambda_Follower
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getKI_SARSA_Lambda_Follower(int id)
    {
        try
        {
            return new KI(id, new Agent_SARSALambda("KI_FollowerSARSAL.rgo"), new Environment_Follower());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /* ************************************************************** */
    /**
     * getFighterA
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getFighterA(int id) {
        try
        {
            return new KI(id, new Agent_SARSA("KI_FighterA.rgo", 0.95, false, DebugState.NO_DEBUG), new Environment_Fighter(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }        
    }
    
    /* ************************************************************** */
    /**
     * getFighterA
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getFighterB(int id) {
        try
        {
            return new KI(id, new Agent_SARSA("KI_FighterB.rgo", 0.95, true, DebugState.NO_DEBUG), new Environment_Fighter(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }        
    }
}

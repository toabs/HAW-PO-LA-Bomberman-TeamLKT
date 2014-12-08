/**
 * 
 */
package klt;

/* ************************************************************** */

import klt.util.DebugState;

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

    public static KI getGoToAgent(int id){
        try
        {
            return new KI(id, new Agent_SARSA("KI_GoToSARSA.rgo"), new Environment_GoTo());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
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
            return new KI(id, new Agent_Simple("KI_Escape.rgo", DebugState.ALL_DEBUG), new Environment_Escape());
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
            return new KI(id, new Agent_Simple("KI_Follower.rgo", DebugState.ALL_DEBUG), new Environment_Follower());
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
            return new KI(id, new Agent_Simple("KI_AvoidBombSimple.rgo", DebugState.NO_DEBUG), new Environment_Avoidbomb_Zone(DebugState.NO_DEBUG));
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
            //return new KI(id, new Agent_SARSALambda("KI_FollowerSARSAL.rgo"), new Environment_Follower());
            return null;
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static KI getFighterAdvSLA(int id, double explorationRate, boolean trainMode, double lambda, int queueLength){
        try
        {
            return new KI(id, new Agent_SARSALambda("KI_FighterAdvSLA" + id + ".rgo", explorationRate, lambda, trainMode, DebugState.NO_DEBUG, queueLength), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static KI getFighterAdvSLB(int id, double explorationRate, boolean trainMode, double lambda, int queueLength){
        try
        {
            return new KI(id, new Agent_SARSALambda("KI_FighterAdvSLB" + id + ".rgo", explorationRate, lambda, trainMode, DebugState.NO_DEBUG, queueLength), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));
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
            //return new KI(id, new Agent_SARSALambda("KI_FighterLambdaA.rgo", 0.95, 0.4, true, DebugState.NO_DEBUG, 10), new Environment_Fighter(DebugState.NO_DEBUG));
            
            return new KI(id, new Agent_SARSA(20, "KI_FighterA.rgo", 0, true, DebugState.NO_DEBUG), new Environment_Fighter(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }        
    }
    
    /* ************************************************************** */
    /**
     * getFighterB
     * @param id
     * @return
    */ /************************************************************* */
    public static KI getFighterB(int id) {
        try
        {
            //return new KI(id, new Agent_SARSALambda("KI_FighterLambdaB.rgo", 0.95, 0.4, true, DebugState.NO_DEBUG, 10), new Environment_Fighter(DebugState.NO_DEBUG));
            
            //return new KI(id, new Agent_SARSALambda("KI_FighterBL.rgo", 0, 0.8, false, DebugState.NO_DEBUG, 20), new Environment_Fighter(DebugState.NO_DEBUG));
            return new KI(id, new Agent_SARSA(20, "KI_FighterB.rgo",  0.95, true, DebugState.NO_DEBUG), new Environment_Fighter(DebugState.NO_DEBUG));

        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }        
    }
    
    public static KI getFighterAdvA(int id, double explorationRate, boolean trainMode) {
        try
        {   
            //return new KI(id, new Agent_SARSALambda("KI_FighterAdvA_Lambda.rgo", 0.95, 0.8, true, DebugState.NO_DEBUG, 15), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));

            DebugState.NO_DEBUG.setqLoggingEnabled(false);
            return new KI(id, new Agent_SARSA("KI_FighterAdvAN2.rgo", explorationRate, trainMode, DebugState.NO_DEBUG), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }        
    }
    
    public static KI getFighterAdvB(int id, double explorationRate, boolean trainMode) {
        try
        {   
            //return new KI(id, new Agent_SARSALambda("KI_FighterAdvB_Lambda.rgo", 0.95, 0.8, true, DebugState.NO_DEBUG, 15), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));

            return new KI(id, new Agent_SARSA("KI_FighterAdvBN2.rgo", explorationRate, trainMode, DebugState.NO_DEBUG), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }        
    }
    

    /* ************************************************************** */
    /**
     * getKI_Q_Follower
     * @param id
     * @param explorationRate
     * @param trainMode
     * @return
    */ /************************************************************* */
    public static KI getKI_Q_Follower(int id, double explorationRate, boolean trainMode)
    {
        try
        {
            return new KI(id, new Agent_Qlearning("KI_Q_Follower.rgo", 0.3, 0.8, explorationRate, trainMode), new Environment_Follower());
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    

    /* ************************************************************** */
    /**
     * getKI_Q_Fighter
     * @param id
     * @param explorationRate
     * @param trainMode
     * @return
    */ /************************************************************* */
    public static KI getKI_Q_Fighter(int id, double explorationRate, boolean trainMode)
    {
        try
        {
            return new KI(id, new Agent_Qlearning("KI_Q_Fighter.rgo", 0.3, 0.8, explorationRate, trainMode), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static KI getKI_Q_FighterB(int id, double explorationRate, boolean trainMode)
    {
        try
        {
            return new KI(id, new Agent_Qlearning("KI_Q_FighterB.rgo", 0.3, 0.8, explorationRate, trainMode), new Environment_Fighter_Advanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

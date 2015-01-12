/**
 * 
 */
package klt;

/* ************************************************************** */

import klt.agent.*;
import klt.environment.*;
import klt.util.DebugState;

import java.io.IOException;

/**
 * @author Lars
 * 19.10.2014
 */
/* *********************************************************** */
public class KIFactory
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
            return new KI(id, new AgentRandom(null), new EnvironmentRandom(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentSimple("KI_Escape.rgo", DebugState.ALL_DEBUG), new EnvironmentEscape(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentSimple("KI_Follower.rgo", DebugState.ALL_DEBUG), new EnvironmentFollower(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentSimple("KI_AvoidBombSimple.rgo", DebugState.NO_DEBUG), new EnvironmentAvoidbombZone(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentSarsa("KI_FollowerSARSA.rgo"), new EnvironmentFollower(DebugState.NO_DEBUG));
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
            //return new KI(id, new AgentSarsaLambda("KI_FollowerSARSAL.rgo"), new EnvironmentFollower());
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
            return new KI(id, new AgentSarsaLambda("KI_FighterAdvSLA.rgo", explorationRate, lambda, trainMode, DebugState.NO_DEBUG, queueLength), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentSarsaLambda("KI_FighterAdvSLB" + id + ".rgo", explorationRate, lambda, trainMode, DebugState.NO_DEBUG, queueLength), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));
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
            //return new KI(id, new AgentSarsaLambda("KI_FighterLambdaA.rgo", 0.95, 0.4, true, DebugState.NO_DEBUG, 10), new EnvironmentFighter(DebugState.NO_DEBUG));
            
            return new KI(id, new AgentSarsa(20, "KI_FighterA.rgo", 0, true, DebugState.NO_DEBUG), new EnvironmentFighter(DebugState.NO_DEBUG));
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
            //return new KI(id, new AgentSarsaLambda("KI_FighterLambdaB.rgo", 0.95, 0.4, true, DebugState.NO_DEBUG, 10), new EnvironmentFighter(DebugState.NO_DEBUG));
            
            //return new KI(id, new AgentSarsaLambda("KI_FighterBL.rgo", 0, 0.8, false, DebugState.NO_DEBUG, 20), new EnvironmentFighter(DebugState.NO_DEBUG));
            return new KI(id, new AgentSarsa(20, "KI_FighterB.rgo",  0.95, true, DebugState.NO_DEBUG), new EnvironmentFighter(DebugState.NO_DEBUG));

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
            //return new KI(id, new AgentSarsaLambda("KI_FighterAdvA_Lambda.rgo", 0.95, 0.8, true, DebugState.NO_DEBUG, 15), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));

            DebugState.NO_DEBUG.setqLoggingEnabled(false);
            return new KI(id, new AgentSarsa("KI_FighterAdvA.rgo", explorationRate, trainMode, DebugState.NO_DEBUG), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));
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
            //return new KI(id, new AgentSarsaLambda("KI_FighterAdvB_Lambda.rgo", 0.95, 0.8, true, DebugState.NO_DEBUG, 15), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));

            return new KI(id, new AgentSarsa("KI_FighterAdvB2.rgo", explorationRate, trainMode, DebugState.NO_DEBUG), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentQLearning("KI_Q_Follower.rgo", 0.3, 0.8, explorationRate, trainMode), new EnvironmentFollower(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentQLearning("KI_Q_FighterA.rgo", 0.3, 0.8, explorationRate, trainMode), new EnvironmentFighterAdvanced(DebugState.NO_DEBUG));
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
            return new KI(id, new AgentQLearning("KI_Q_FighterB" + id + ".rgo", 0.3, 0.8, explorationRate, trainMode), new EnvironmentFighterNewAdvanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static KI getBomberman(int id, double explorationRate, boolean trainMode){
        try{
            return new KI(id, new AgentSarsaLambda("KI_Bomberman" + id + ".rgo", explorationRate, 0.8, trainMode, DebugState.NO_DEBUG, 30), new EnvironmentFighterNewAdvanced(DebugState.NO_DEBUG));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static KI getKI_SarsaAdv2A(int id, double explorationRate, boolean trainMode){
        try
        {
            return new KI(id, new AgentSarsa("KI_FighterAdv2A.rgo", explorationRate, trainMode, DebugState.NO_DEBUG), new EnvironmentFighterNewAdvanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static KI getKI_SarsaAdv2B(int id, double explorationRate, boolean trainMode){
        try
        {
            return new KI(id, new AgentSarsa("KI_FighterAdv2B.rgo", explorationRate, trainMode, DebugState.NO_DEBUG), new EnvironmentFighterNewAdvanced(DebugState.NO_DEBUG));
        } catch (Exception e)
        {
            System.out.println("Exception at KI-Creation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

/**
 * 
 */
package klt;

import Core.Playboard;
import Core.User;
import klt.agent.Agent;
import klt.environment.Environment;
import klt.util.DebugState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
public class KI extends User
{
    private boolean firstStep = true;
    private Environment currentEnvironment;
    private DebugState debugState;
    private Agent currentAgent;
    Action lastAction = null;
    
    /* ************************************************************** */
    /**
     * LerningKI
     * @param id
    */ /************************************************************* */
    public KI(int id, Agent agent, Environment environment)
    {
        super(id);
        this.debugState = DebugState.NO_DEBUG;
       
        currentAgent = agent;
        currentEnvironment = environment;
        
        //LocalGlue localGlueImplementation=new LocalGlue(environment,agent);
        //RLGlue.setGlue(localGlueImplementation);
    }

    public KI(int id, Agent agent, Environment environment, DebugState debugState){
        this(id, agent, environment);
        this.debugState = debugState;
    }

    /* ************************************************************** */
    /**
     * getAction
     * @see Core.User#getAction(Core.Playboard)
     */
    /************************************************************* */
    @Override
    public int getAction(Playboard playboard)
    {
        //Observation_action firstResponse = null;
        //Reward_observation_action_terminal stepResponse = null;
        String taskSpec = null;
        Observation obsStart = null;
        Action actionStart = null;
        Reward_observation_terminal obsStep = null;
        Action actionStep = null;
        
        int action = 0;
        
        currentEnvironment.setPlayboard(playboard, this.getId());
        
        if (firstStep) 
        {            
            //RLGlue.RL_init();
            taskSpec = this.currentEnvironment.env_init();
            this.currentAgent.agent_init(taskSpec);
            
            obsStart = this.currentEnvironment.env_start();
            actionStart = this.currentAgent.agent_start(obsStart);
            
            //firstResponse = RLGlue.RL_start();
            //action = firstResponse.a.intArray[0];
            
            lastAction = actionStart;
            action = actionStart.intArray[0];
            firstStep = false;
        } 
        else
        {            
            //stepResponse = RLGlue.RL_step();
            //action = stepResponse.a.intArray[0];
            obsStep = this.currentEnvironment.env_step(lastAction);
            
            if (obsStep.isTerminal())
            {
                this.currentAgent.agent_end(obsStep.r);
            }
            else
            {
                actionStep = this.currentAgent.agent_step(obsStep.r, obsStep.o);
            }
            
            lastAction = actionStep;
            action = actionStep.intArray[0];
        }
        
        return action;
    }
    
    /* ************************************************************** */
    /**
     * resetMove
     * @see Core.User#resetMove()
    */ /************************************************************* */
    @Override
    public void resetMove()
    {
        //not needed here! the KI will NEVER reset and regret it's decision. It's a true brave warrior!
    }

    /* ************************************************************** */
    /**
     * gameOver
     * @see Core.User#gameOver(boolean)
    */ /************************************************************* */
    @Override
    public void gameOver(boolean won, Playboard playboard)
    {
        currentEnvironment.setPlayboard(playboard, this.getId());
        
        Reward_observation_terminal envStepResult = null;
        //not used by KI
        //RLGlue.RL_cleanup();
        envStepResult = this.currentEnvironment.env_step(this.lastAction);
        this.currentEnvironment.env_cleanup();
        this.currentAgent.agent_end(envStepResult.r);
        this.currentAgent.agent_cleanup();
        firstStep = true;     
    }

    protected void kILogln(String output){
        if (debugState.getKIDebugState()){
            System.out.println(output);
        }
    }

    protected void kILog(String output){
        if (debugState.getKIDebugState()){
            System.out.print(output);
        }
    }

    @Override
    public void gameExit() {
    	this.currentAgent.agent_exit();
    }
}

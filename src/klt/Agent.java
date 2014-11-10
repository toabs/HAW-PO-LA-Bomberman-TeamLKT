/**
 * 
 */
package klt;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/* ************************************************************** */
/**
 * @author LarsE
 * 13.10.2014
 */
/* *********************************************************** */
public abstract class Agent implements AgentInterface
{
    //The set of observation, saving a Map of Values indexed by the Action
    //Example 
    protected HashMap<String, HashMap<Integer, Double>> observationStorage; 
    private String saveFilePath;
    private DebugState debugState;
    protected Random randGenerator = new Random();

    Agent(String saveFilePath, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath);
        this.debugState = debugState;
    }
    
    @SuppressWarnings("unchecked")
    Agent(String saveFilePath) throws IOException, ClassNotFoundException
    {
        this.debugState = DebugState.NO_DEBUG;
        this.saveFilePath = saveFilePath;
        
        File f = new File(saveFilePath);
        
        if (f.exists() && !f.isDirectory()) 
        {
            FileInputStream fin = new FileInputStream(saveFilePath);
            ObjectInputStream ois = new ObjectInputStream(fin);
            this.observationStorage = (HashMap<String, HashMap<Integer, Double>>) ois.readObject();
            ois.close();
        }
        else
        {
            this.observationStorage = new HashMap<String, HashMap<Integer, Double>>();
        }
    }

    /* ************************************************************** */
    /**
     * getBestAction
     * @param currentObs
     * @return
     */ /************************************************************* */
    protected int getBestAction(Observation currentObs)
    {
        int currentAction = 0;
        Double bestValue = -9999999.9;
        ArrayList<Integer> bestActions = new ArrayList<Integer>();
        int bestActionCount = 0;

        if (this.observationStorage.containsKey(currentObs.toString()))
        {
            //determine highest value
            for(int i = 0; i < 5; i++)
            {
                if (this.observationStorage.get(currentObs.toString()).get(i) > bestValue)
                {
                    bestValue = this.observationStorage.get(currentObs.toString()).get(i);
                }
            }

            //get Actions with that value (must be at least one)
            for(int i = 0; i < 5; i++)
            {
                if (this.observationStorage.get(currentObs.toString()).get(i) == bestValue)
                {
                    bestActions.add(i);
                }
            }

            bestActionCount = bestActions.size();

            if (bestActionCount <= 1)
            {
                currentAction = bestActions.get(0);
            }
            else
            {
                //there is more than one best action
                currentAction = bestActions.get(this.randGenerator.nextInt(bestActionCount));
            }
        }
        else
        {
            currentAction = this.randGenerator.nextInt(5);
        }

        return currentAction;
    }

    protected void agentLogln(String output){
        if (debugState.getAgentDebugState()){
            System.out.println(output);
        }
    }

    protected void agentLog(String output){
        if (debugState.getAgentDebugState()){
            System.out.print(output);
        }
    }

    /* ************************************************************** */
    /**
     * agent_cleanup
     * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_cleanup()
    */ /************************************************************* */
    @Override
    public void agent_cleanup()
    {    	
        this.agentLogln("Cleanup Called");

    }

	public void agent_exit() {
		this.agentLogln("Exit Called");
		//save progress
        try
        {
            FileOutputStream fout = new FileOutputStream(saveFilePath);
            ObjectOutputStream oos = new ObjectOutputStream(fout);        
            oos.writeObject(observationStorage);
            oos.close();
        } catch (IOException e)
        {
            System.out.println("Error saving observationStorage: " + e.getMessage());
        }
	}
}

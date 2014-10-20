/**
 * 
 */
package klt;

import org.rlcommunity.rlglue.codec.AgentInterface;

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
    protected HashMap<String, HashMap<Integer, Integer>> observationStorage; 
    private String saveFilePath;
    protected Random randGenerator = new Random();
    
    @SuppressWarnings("unchecked")
    Agent(String saveFilePath) throws IOException, ClassNotFoundException
    {
        this.saveFilePath = saveFilePath;
        
        File f = new File(saveFilePath);
        
        if (f.exists() && !f.isDirectory()) 
        {
            FileInputStream fin = new FileInputStream(saveFilePath);
            ObjectInputStream ois = new ObjectInputStream(fin);
            this.observationStorage = (HashMap<String, HashMap<Integer, Integer>>) ois.readObject();
            ois.close();
        }
        else
        {
            this.observationStorage = new HashMap<String, HashMap<Integer, Integer>>();
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
        System.out.println("Cleanup Called");
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

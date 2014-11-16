package klt;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;

/**
 * Created by Tobi on 27.10.2014.
 */
public class Agent_Qlearning extends Agent {

    //private double alpha = 0.2; //Learningrate
    //private double epsilon = 0.4; //exploration rate
    //private double gamma = 0.625; //discount factor
    //private int initialQValue = 10; //a higher initial value encourages exploration

    public Agent_Qlearning(String saveFilePath) throws IOException, ClassNotFoundException {
        super(saveFilePath);
    }

    @Override
    public void agent_init(String s) {

    }

    @Override
    public Action agent_start(Observation observation) {
        return null;
    }

    @Override
    public Action agent_step(double v, Observation observation) {
        return null;
    }

    @Override
    public void agent_end(double v) {

    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

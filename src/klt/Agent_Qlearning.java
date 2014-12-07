package klt;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import klt.util.Actions_E;

/**
 * Created by Tobi on 27.10.2014.
 */
public class Agent_Qlearning extends Agent {

    private double alpha = 0.1; //Learningrate
    private double gamma = 0.8; //discount factor
    private double epsilon = 0.625; //Start-exploration rate
    private final double EPSILONMINIMUM = 0.005;
    private final double EPSILONDECREASE = 0.0003;
    private boolean trainingMode = true;
    
    private ObservationWithActions lastObservation;
    private Integer lastAction;

    public Agent_Qlearning(String saveFilePath, double alpha, double gamma, double epsilon, boolean trainingMode) throws IOException, ClassNotFoundException {
        super(saveFilePath);
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.trainingMode = trainingMode;
    }

    @Override
    public void agent_init(String s) {

    }

    @Override
    public Action agent_start(Observation observation) {
        Action returnAction = new Action(1, 0, 0);        
        returnAction.intArray[0] = this.chooseAction((ObservationWithActions) observation);
        return returnAction;
    }

    @Override
    public Action agent_step(double v, Observation observation) {
        doRating(v, observation);        
        Action returnAction = new Action(1, 0, 0);        
        returnAction.intArray[0] = this.chooseAction((ObservationWithActions) observation);        
        return returnAction;
    }

    @Override
    public void agent_end(double v) {
        //no reward calculation needed, as there are no future steps, just update
        this.setRewardForActionObservation(v, lastObservation.toString(), this.lastAction, lastObservation.getActions());
        
        //decrease exploration rate
        epsilon -= EPSILONDECREASE;
        if (epsilon < EPSILONMINIMUM) {
            epsilon = EPSILONMINIMUM;
        }
    }
    
    private void doRating(double v, Observation currentObservation)
    {
        double oldReward = this.INITIALQVALUE;
        //the the maximum reward for the current observation
        double maxReward = getMaxRewardForObs(currentObservation.toString());
        //get the reward of the last step
        if (this.observationStorage.get(lastObservation.toString()) != null) {
            //first step, there may be no stored lastObservations
            oldReward = this.observationStorage.get(lastObservation.toString()).get(this.lastAction);
        }
        
        //calculate new reward
        double newReward = oldReward + alpha * (v + (gamma * maxReward) - oldReward);
        //update
        this.setRewardForActionObservation(newReward, lastObservation.toString(), this.lastAction, lastObservation.getActions());
    }

    @Override
    public String agent_message(String s) {
        return null;
    }
    
    private Integer chooseAction(ObservationWithActions currentObservation) {
        this.lastObservation = currentObservation;
        Integer result = 0;
        result = this.getBestAction(currentObservation, (currentObservation).getActions());

        if (trainingMode) {  //if the trainingmode is enabled the agent will sometimes randomly choose a random action
            if (this.randGenerator.nextDouble() < epsilon) {
                Actions_E[] actionArray = (currentObservation).getActions().toArray(new Actions_E[0]);
                
                if (actionArray.length <= 0) {
                    this.agentLogln("No possible Action! -> Stay");
                    result = 0;
                } else {
                    int randomActionIndex = this.randGenerator.nextInt(actionArray.length);
                    result = actionArray[randomActionIndex].ordinal();
                }
            }
        }
        
        this.lastAction = result;
        return result;
    }
}

package klt.agent;

import klt.ObservationWithActions;
import klt.util.Actions_E;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;

/**
 * Created by Tobi on 27.10.2014.
 */
public class AgentQLearning extends Agent {

    private double alpha = 0.1; //Learningrate
    private double gamma = 0.8; //discount factor
    private double epsilon = 0.625; //Start-exploration rate
    private final double EPSILONMINIMUM = 0.005;
    private final double EPSILONDECREASE = 0.0003;
    private boolean trainingMode = true;
    
    private ObservationWithActions lastObservation;
    private Integer lastAction;

    /**
     * Creates an agent which uses the QLearning algorithm.
     *
     * @param saveFilePath      The path to save the training data to.
     * @param alpha             The alpha used for the algorithm.
     * @param gamma             The gamma used for the algorithm.
     * @param epsilon           The epsilon used for the algorithm.
     * @param trainingMode      Is this instance supposed to train or not.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public AgentQLearning(String saveFilePath, double alpha, double gamma, double epsilon, boolean trainingMode) throws IOException, ClassNotFoundException {
        super(saveFilePath);
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.trainingMode = trainingMode;
    }

    /**
     * agent_init is not used in this algorithm.
     * @param s
     */
    @Override
    public void agent_init(String s) {

    }

    /**
     * This method is called at the start of a round.
     * It returns an action with the highest reward for the given action.
     *
     * @param observation       The observation.
     * @return      The action
     */
    @Override
    public Action agent_start(Observation observation) {
        Action returnAction = new Action(1, 0, 0);        
        returnAction.intArray[0] = this.chooseAction((ObservationWithActions) observation);
        return returnAction;
    }

    /**
     * This method is called each step and returns an action with the highest reward for the current observation.
     * Also it calculates a new reward for the action in the last step with the reward.
     *
     * @param v             The reward for the last action.
     * @param observation   The current observation after the last action.
     * @return              The next action.
     */
    @Override
    public Action agent_step(double v, Observation observation) {
        doRating(v, observation);        
        Action returnAction = new Action(1, 0, 0);        
        returnAction.intArray[0] = this.chooseAction((ObservationWithActions) observation);        
        return returnAction;
    }

    /**
     * This method is called at the end of a round.
     * It is used to take the reward for the last action into account.
     *
     * @param v         The reward for the last action.
     */
    @Override
    public void agent_end(double v) {
        //no reward calculation needed, as there are no future steps, just update
        this.setRewardForActionObservation(v, lastObservation.toString(), this.lastAction);
        
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
        this.setRewardForActionObservation(newReward, lastObservation.toString(), this.lastAction);
    }

    /**
     * Not used.
     * @param s
     * @return
     */
    @Override
    public String agent_message(String s) {
        return null;
    }

    /**
     * Chooses an action for the current observation.
     *
     * @param currentObservation        Current observation
     * @return      The action with the highest reward for this observation.
     */
    private Integer chooseAction(ObservationWithActions currentObservation) {
        this.lastObservation = currentObservation;
        Integer result = 0;
        result = this.getBestAction(currentObservation, currentObservation.getActions());

        if (trainingMode) {  //if the trainingmode is enabled the agent will sometimes randomly choose a random action
            if (this.randGenerator.nextDouble() < epsilon) {
                Actions_E[] actionArray = currentObservation.getActions().toArray(new Actions_E[currentObservation.getActions().size()]);
                
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

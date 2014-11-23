package klt;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.HashMap;

import klt.util.Actions_E;

/**
 * Created by Tobi on 26.10.2014.
 * Credit goes to "https://github.com/HAW-AI/PO-LA-2012-WS-Cliff" the general construction bases on their version
 */
public class Agent_SARSA extends Agent{

    //private List<Pair<Pair<String,Integer>,Double>> episode;
    private Observation lastObservation;
    private Observation beforeLastObservation;
    private Integer lastAction;
    private Integer beforeLastAction;
    private double alpha = 0.4; //Lernrate
    private double gamma = 0.8; //Discountrate
    private double epsilon = 0.95; //exploration rate
    private boolean trainingMode = true;
    private final double INITIALQVALUE = 50.0; //initial q value
    private final double EPSILONMINIMUM = 0.005;

    /**
     * With this contructor it will just run what the agent learned. The agent will not train or log anything.
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Agent_SARSA(String saveFilePath) throws IOException, ClassNotFoundException {
        this(saveFilePath, 0.0, false, DebugState.NO_DEBUG);
    }

    public Agent_SARSA(String saveFilePath, double explorationRate, boolean trainingMode, DebugState debugState) throws IOException, ClassNotFoundException {
        super(saveFilePath, debugState);
        this.epsilon = explorationRate;
        this.trainingMode = trainingMode;
    }

    @Override
    public void agent_init(String s) {
        //episode = new ArrayList<Pair<Pair<String, Integer>,Double>>();
    }

    @Override
    public Action agent_start(Observation observation) {
        lastObservation = observation;;
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, ((ObservationWithActions) observation).getActions());

        lastAction = returnAction.intArray[0];
        return returnAction;
    }

    @Override
    public Action agent_step(double v, Observation observation) {
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;
        lastObservation = observation;

        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, ((ObservationWithActions) observation).getActions());

        if (trainingMode) {         //if the trainingmode is enabled the agent will sometimes randomly choose a random action
            if (this.randGenerator.nextDouble() < epsilon) {
                Actions_E[] actionArray = ((ObservationWithActions) observation).getActions().toArray(new Actions_E[0]);
                
                if (actionArray.length <= 0) {
                    this.agentLogln("No possible Action! -> Stay");
                    returnAction.intArray[0] = 0;
                } else {

                    int randomActionIndex = this.randGenerator.nextInt(actionArray.length);
                    returnAction.intArray[0] = actionArray[randomActionIndex].ordinal();
                }
            }
        }

        lastAction = returnAction.intArray[0];
        if (trainingMode) {
            updateValues(v);
        }
        return returnAction;
    }

    @Override
    public void agent_end(double v) {
        if(trainingMode) {
            updateValues(v);

            double reward = v;
            //distribute reward
            if (this.observationStorage.containsKey(lastObservation)) {
                double currentQ = observationStorage.get(lastObservation).get(lastAction);
                reward = currentQ + alpha * (v - currentQ);
            }
            setRewardForActionObservation(reward, lastObservation.toString(), lastAction, ((ObservationWithActions) lastObservation).getActions());

            {        //lower the explorationrate
                epsilon -= 0.003;
                if (epsilon < EPSILONMINIMUM) {
                    epsilon = EPSILONMINIMUM;
                }
            }
        }
    }

    private void updateValues(double r){

        double reward = r;
        //distribute reward
        if (this.observationStorage.containsKey(beforeLastObservation))
        {
            double qThis = observationStorage.get(beforeLastObservation).get(beforeLastAction);
            double qNext = INITIALQVALUE;
            if (observationStorage.containsKey(lastObservation)) {
                qNext = observationStorage.get(lastObservation).get(lastAction);
            }
            reward = qThis + alpha * (r + gamma * qNext - qThis);
        }
        setRewardForActionObservation(reward, beforeLastObservation.toString(), beforeLastAction, ((ObservationWithActions) beforeLastObservation).getActions());
    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

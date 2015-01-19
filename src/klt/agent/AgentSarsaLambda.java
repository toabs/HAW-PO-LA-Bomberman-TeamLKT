package klt.agent;

import klt.ObservationWithActions;
import klt.util.*;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tobi on 29.10.2014.
 * Influenced by "http://artint.info/demos/rl/SarsaController.java" (1.11.2014)
 * and the theoretical implementation from "http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node77.html" (1.11.2014)
 */
public class AgentSarsaLambda extends Agent {

    private String lastObservation;
    private String beforeLastObservation;
    private Integer lastAction;
    private Integer beforeLastAction;
    private HashMap<String, HashMap<Integer, Double>> traceStorage;
    private double lambda;
    private double alpha = 0.2; //Lernrate
    private double gamma = 0.8; //Discountrate
    private double epsilon; //exploration rate
    private boolean trainingMode; //is it allowed to explore?
    private final double INITIALQVALUE = 5; //initial q values
    private final int NUMBEROFACTIONS = 6; //total numbers of actions to choose from
    private final double EPSILON = 0.00001; //the epsilon for the close to zero comparisions
    private final double EPSILONMINIMUM = 0.1;
    private RingBuffer<SarsaLambdaQueueElement> queue;
    private AgentLogUtilSLambda logUtil = null;


    public AgentSarsaLambda(String saveFilePath, double explorationRate, double lambda, boolean trainingMode, DebugState debugState, int queueLenght) throws IOException, ClassNotFoundException {
        super(saveFilePath, debugState);
        this.epsilon = explorationRate;
        this.lambda = lambda;
        this.trainingMode = trainingMode;
        queue = new RingBuffer<>(queueLenght);
    }

    public AgentSarsaLambda(String saveFilePath, double explorationRate, double lambda, boolean trainingMode, DebugState debugState, int queueLenght, int logLastNChanges) throws IOException, ClassNotFoundException {
        this(saveFilePath, explorationRate, lambda, trainingMode, debugState, queueLenght);
        if (debugState.isqLoggingEnabled()) {
            logUtil = new AgentLogUtilSLambda(logLastNChanges, queueLenght);
        }
    }

    @Override
    public void agent_init(String s) {
        queue.clear();
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
        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, ((ObservationWithActions) observation).getActions());

        lastAction = returnAction.intArray[0];
        return returnAction;
    }

    private void updateValues(double r, boolean lastUpdate){
        if(debugState.isqLoggingEnabled()) {
            logUtil.addNewLogChain();
        }

        double lastQ = INITIALQVALUE;
        double currentQ = INITIALQVALUE;

        if (observationStorage.containsKey(beforeLastObservation)){
            lastQ = observationStorage.get(beforeLastObservation).get(beforeLastAction);
        }
        else{
            fillInUnknownObservations(beforeLastObservation.toString());
        }

        if(!lastUpdate) {
            if (observationStorage.containsKey(lastObservation)) {
                currentQ = observationStorage.get(lastObservation).get(lastAction);
            } else {
                fillInUnknownObservations(lastObservation.toString());
            }
        }else{
            currentQ = 0;
        }

        double delta = r + gamma * currentQ - lastQ;

        if (queue.contains(beforeLastObservation, beforeLastAction)){
            SarsaLambdaQueueElement element = queue.getElement(beforeLastObservation, beforeLastAction);
            element.setValue(1);
            queue.add(element);
        }
        else{
            queue.add(new SarsaLambdaQueueElement(beforeLastObservation, beforeLastAction, 1));
        }

        for (SarsaLambdaQueueElement stepElement : queue){
            SarsaLogElement logElement = null;
            if (debugState.isqLoggingEnabled()) {
                logUtil.add(stepElement.getObservation(), stepElement.getAction());
                logElement = logUtil.getLastElem();
            }

            double oldValE = stepElement.getValue();
            double oldValQ = observationStorage.get(stepElement.getObservation()).get(stepElement.getAction());
            double newValQ = oldValQ + alpha * delta * oldValE;

            if (debugState.isqLoggingEnabled()) {
                logElement.setEpsilon(oldValE);
                logElement.setReward(r);
                logElement.setValueBefore(oldValQ);
                logElement.setValueAfter(newValQ);
                logElement.setValueNextAction(currentQ);
            }
            observationStorage.get(stepElement.getObservation()).put(stepElement.getAction(), newValQ);
            stepElement.setValue(gamma * lambda * oldValE);
        }
        /*for(String keyObservation : observationStorage.keySet()){
            for (Integer keyAction : observationStorage.get(keyObservation).keySet()){
                double oldValE = traceStorage.get(keyObservation).get(keyAction);
                if (oldValE >= EPSILON) {
                    double oldValQ = observationStorage.get(keyObservation).get(keyAction);
                    observationStorage.get(keyObservation).put(keyAction, (oldValQ + alpha * delta * oldValE));
                    traceStorage.get(keyObservation).put(keyAction, gamma * lambda * oldValE);
                }
            }
        }*/
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
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;

        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, ((ObservationWithActions) observation).getActions());

        lastAction = returnAction.intArray[0];

        if (trainingMode) {         //if the trainingmode is enabled the agent will sometimes randomly choose a random action
            if (this.randGenerator.nextDouble() < epsilon) {
                returnAction.intArray[0] = this.randGenerator.nextInt(NUMBEROFACTIONS);
            }
        updateValues(v, false);
        }

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
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;

        if (trainingMode) {            //lower the exploration rate
            epsilon -= 0.002;
            if (epsilon < EPSILONMINIMUM) {
                epsilon = EPSILONMINIMUM;
            }
        updateValues(v, true);
        }

        if(debugState.isqLoggingEnabled()) {
            logUtil.logLastQValueUpdates(observationStorage);
        }
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
}

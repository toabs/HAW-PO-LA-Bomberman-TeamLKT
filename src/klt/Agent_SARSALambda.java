package klt;

import klt.util.Pair;
import klt.util.RingBuffer;
import klt.util.SarsaLambdaQueueElement;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Tobi on 29.10.2014.
 * Influenced by "http://artint.info/demos/rl/SarsaController.java" (1.11.2014)
 * and the theoretical implementation from "http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node77.html" (1.11.2014)
 */
public class Agent_SARSALambda extends Agent {

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

    public Agent_SARSALambda(String saveFilePath) throws IOException, ClassNotFoundException {
        this(saveFilePath, DebugState.NO_DEBUG);
    }

    public Agent_SARSALambda(String saveFilePath, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath, true, debugState);
    }

    public Agent_SARSALambda(String saveFilePath, boolean trainingMode, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath, 0.9,  0.4, trainingMode, debugState);
    }

    public Agent_SARSALambda(String saveFilePath, double explorationRate, double lambda, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath, explorationRate, lambda, true, debugState);
    }

    public Agent_SARSALambda(String saveFilePath, double explorationRate, double lambda, boolean trainingMode, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath, explorationRate, lambda, trainingMode, debugState, 50);
    }

    public Agent_SARSALambda(String saveFilePath, double explorationRate, double lambda, boolean trainingMode, DebugState debugState, int queueLenght) throws IOException, ClassNotFoundException {
        super(saveFilePath, debugState);
        this.epsilon = explorationRate;
        this.lambda = lambda;
        this.trainingMode = trainingMode;
        queue = new RingBuffer<>(queueLenght);
    }

    @Override
    public void agent_init(String s) {
        queue.clear();
    }

    @Override
    public Action agent_start(Observation observation) {
        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, NUMBEROFACTIONS);

        lastAction = returnAction.intArray[0];
        return returnAction;
    }

    private void updateValues(double r){
        double delta = r + gamma * INITIALQVALUE - INITIALQVALUE;
        if (observationStorage.containsKey(lastObservation)){

            if(observationStorage.containsKey(beforeLastObservation)) {

            double lastQ = observationStorage.get(beforeLastObservation).get(beforeLastAction);
            double currentQ = observationStorage.get(lastObservation).get(lastAction);
            delta = r + gamma * currentQ - lastQ;

            }
            else{
                fillInUnknownObservations(beforeLastObservation);
            }
        }
        else{
            fillInUnknownObservations(lastObservation);
        }

        if (queue.contains(beforeLastObservation, beforeLastAction)){
            SarsaLambdaQueueElement element = queue.getElement(beforeLastObservation, beforeLastAction);
            element.setValue(element.getValue() + 1);
            queue.add(element);
        }
        else{
            queue.add(new SarsaLambdaQueueElement(beforeLastObservation, beforeLastAction, 1));
        }

        for (SarsaLambdaQueueElement stepElement : queue){
            double oldValE = stepElement.getValue();
            if (oldValE >= EPSILON){
                double oldValQ = observationStorage.get(stepElement.getObservation()).get(stepElement.getAction());
                observationStorage.get(stepElement.getObservation()).put(stepElement.getAction(), (oldValQ + alpha * delta * oldValE));
                stepElement.setValue(gamma * lambda * oldValE);
            }
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

    @Override
    public Action agent_step(double v, Observation observation) {
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;

        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, NUMBEROFACTIONS);

        lastAction = returnAction.intArray[0];

        if (trainingMode) {         //if the trainingmode is enabled the agent will sometimes randomly choose a random action
            if (this.randGenerator.nextDouble() < epsilon) {
                returnAction.intArray[0] = this.randGenerator.nextInt(NUMBEROFACTIONS);
            }
        }

        updateValues(v);
        return returnAction;
    }

    @Override
    public void agent_end(double v) {
        if (trainingMode) {            //lower the exploration rate
            epsilon -= 0.002;
            if (epsilon < EPSILONMINIMUM) {
                epsilon = EPSILONMINIMUM;
            }
        }
        updateValues(v);
    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

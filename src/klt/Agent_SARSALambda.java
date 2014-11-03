package klt;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    private double lambda = 0.9;
    private double alpha = 0.2; //Lernrate
    private double gamma = 0.8; //Discountrate
    private double epsilon = 0.95; //exploration rate
    private final double INITIALQVALUE = 5; //initial q values
    private final int NUMBEROFACTIONS = 5;

    public Agent_SARSALambda(String saveFilePath) throws IOException, ClassNotFoundException {
        super(saveFilePath);

            traceStorage = new HashMap<String, HashMap<Integer, Double>>();
    }

    @Override
    public void agent_init(String s) {
        //set 0 in the trace map e(s, a) for all known s, a
        for(String keyObs : observationStorage.keySet()){
            //add the unknown observation
            this.traceStorage.put(keyObs, new HashMap<Integer, Double>());

            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.traceStorage.get(keyObs).put(i, 0.0);
            }
        }
    }

    @Override
    public Action agent_start(Observation observation) {
        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation);

        lastAction = returnAction.intArray[0];
        return returnAction;
    }

    private void updateValues(double r){
        double delta = r + gamma * INITIALQVALUE - INITIALQVALUE;
        if (observationStorage.containsKey(lastObservation)){

            if(observationStorage.containsKey(beforeLastObservation)) {

            double lastQ = observationStorage.get(beforeLastObservation).get(beforeLastAction).doubleValue();
            double currentQ = observationStorage.get(lastObservation).get(lastAction).doubleValue();
            delta = r + gamma * currentQ - lastQ;

            }else
            {
                //add the unknown observation
                this.observationStorage.put(beforeLastObservation, new HashMap<Integer, Double>());

                for(int i = 0; i < NUMBEROFACTIONS; i++)
                {
                    this.observationStorage.get(beforeLastObservation).put(i, (i == lastAction) ? r : INITIALQVALUE);
                }
            }
        }  else
        {
            //add the unknown observation
            this.observationStorage.put(lastObservation, new HashMap<Integer, Double>());

            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.observationStorage.get(lastObservation).put(i, (i == lastAction) ? r : INITIALQVALUE);
            }
        }

        if (traceStorage.containsKey(beforeLastObservation)){
            double now = traceStorage.get(beforeLastObservation).get(beforeLastAction).doubleValue();
            traceStorage.get(beforeLastObservation).put(beforeLastAction, now + 1);
        } else
        {
            //add the unknown observation
            this.traceStorage.put(beforeLastObservation, new HashMap<Integer, Double>());

            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.traceStorage.get(beforeLastObservation).put(i, (double) ((i == beforeLastAction) ? 1 : 0));
            }
        }

        if (!traceStorage.containsKey(lastObservation)){
            //add the unknown observation
            this.traceStorage.put(lastObservation, new HashMap<Integer, Double>());

            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.traceStorage.get(lastObservation).put(i, (double) ((i == beforeLastAction) ? 1 : 0));
            }
        }

        for(String keyObservation : observationStorage.keySet()){
            for (Integer keyAction : observationStorage.get(keyObservation).keySet()){
                double oldValQ = observationStorage.get(keyObservation).get(keyAction).doubleValue();
                double oldValE = traceStorage.get(keyObservation).get(keyAction).doubleValue();
                observationStorage.get(keyObservation).put(keyAction, (oldValQ + alpha * delta * oldValE));
                traceStorage.get(keyObservation).put(keyAction, gamma * lambda * oldValE);
            }
        }
    }

    @Override
    public Action agent_step(double v, Observation observation) {
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;

        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation);

        lastAction = returnAction.intArray[0];
        updateValues(v);
        return returnAction;
    }

    @Override
    public void agent_end(double v) {
        updateValues(v);
    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

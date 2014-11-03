package klt;

import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;
import java.util.*;

/**
 * Created by Tobi on 26.10.2014.
 * Credit goes to "https://github.com/HAW-AI/PO-LA-2012-WS-Cliff" the general construction bases on their version
 */
public class Agent_SARSA extends Agent{

    private List<Pair<Pair<String,Integer>,Double>> episode;
    private String lastObservation;
    private String beforeLastObservation;
    private Integer lastAction;
    private Integer beforeLastAction;
    private double alpha = 0.2; //Lernrate
    private double gamma = 0.8; //Discountrate
    private double epsilon = 0.95; //exploration rate
    private final int INITIALQVALUE = 5; //initial q values
    private final int NUMBEROFACTIONS = 5;

    public Agent_SARSA(String saveFilePath) throws IOException, ClassNotFoundException {
        super(saveFilePath);
    }

    @Override
    public void agent_init(String s) {
        episode = new ArrayList<Pair<Pair<String, Integer>,Double>>();
    }

    @Override
    public Action agent_start(Observation observation) {
        lastObservation = observation.toString();
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation);

        lastAction = returnAction.intArray[0];
        return returnAction;
    }

    @Override
    public Action agent_step(double v, Observation observation) {
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;
        lastObservation = observation.toString();

        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation);

        if (this.randGenerator.nextInt(100) < (epsilon * 100)){
            returnAction.intArray[0] = this.randGenerator.nextInt(NUMBEROFACTIONS);
        }

        lastAction = returnAction.intArray[0];
        updateValues(v);
        return returnAction;
    }

    @Override
    public void agent_end(double v) {
        updateValues(v);

        //distribute reward
        if (this.observationStorage.containsKey(lastObservation))
        {
            double currentQ = observationStorage.get(lastObservation).get(lastAction).doubleValue();
            double res = (currentQ + alpha * (v - currentQ));
            observationStorage.get(lastObservation).put(beforeLastAction, res);
        }
        else
        {
            //add the unknown observation
            this.observationStorage.put(lastObservation, new HashMap<Integer, Double>());

            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.observationStorage.get(lastObservation).put(i, (i == lastAction) ? v : INITIALQVALUE);
            }
        }
        epsilon *= epsilon;
    }

    private void updateValues(double r){


        //distribute reward
        if (this.observationStorage.containsKey(beforeLastObservation))
        {
            double qThis = observationStorage.get(beforeLastObservation).get(beforeLastAction).doubleValue();
            double qNext = INITIALQVALUE;
            if (observationStorage.containsKey(lastObservation)) {
                qNext = observationStorage.get(lastObservation).get(lastAction).doubleValue();
            }
            double res = (qThis + alpha * (r + gamma * qNext - qThis));
            observationStorage.get(beforeLastObservation).put(beforeLastAction, res);
        }
        else
        {
            //add the unknown observation
            this.observationStorage.put(beforeLastObservation, new HashMap<Integer, Double>());

            for(int i = 0; i < NUMBEROFACTIONS; i++)
            {
                this.observationStorage.get(beforeLastObservation).put(i, (i == lastAction) ? r : INITIALQVALUE);
            }
        }
    }

//    @Override
//    public Action agent_step(double v, Observation observation) {
//        episode.add(new Pair<Pair<String, Integer>, Double>(new Pair<String, Integer>(lastObservation, lastAction),v));
//        lastObservation = observation.toString();
//
//        Action returnAction = new Action(1, 0, 0);
//        returnAction.intArray[0] = this.getBestAction(observation);
//
//        if (this.randGenerator.nextInt(100) < epsilon){
//            returnAction.intArray[0] = this.randGenerator.nextInt(NUMBEROFACTIONS);
//        }
//
//        lastAction = returnAction.intArray[0];
//        return returnAction;
//    }

//    @Override
//    public void agent_end(double v) {
//        episode.add(new Pair<Pair<String, Integer>, Double>(new Pair<String, Integer>(lastObservation, lastAction),v));
//        for(int i = 0; i < episode.size()-1; i++){
//           String key = episode.get(i).first().first();
//            if(observationStorage.containsKey(episode.get(i).first().first())){
//
//                HashMap<Integer, Integer> states = observationStorage.get(episode.get(i).first().first());
//                int valueBrain = states.get(episode.get(i).first().second());
//                double valueEpisode = episode.get(i).second();
//                states.put(episode.get(i).first().second(), (int) (valueBrain + alpha * (valueEpisode + gamma * episode.get(i+1).second() - valueBrain)));
//
//            }else{
//                this.observationStorage.put(key, new HashMap<Integer, Integer>());
//
//                for(int j = 0; j < 5; j++)
//                {
//                    this.observationStorage.get(key).put(j, new Integer((j == lastAction) ? episode.get(i).second().intValue() : INITIALQVALUE));
//                }
//            }
//        }
//        if(observationStorage.containsKey(episode.get(episode.size()-1).first().first())) {
//            this.observationStorage.get(episode.get(episode.size() - 1).first().first()).put(lastAction, new Integer((int) episode.get(episode.size() - 1).second().doubleValue()));
//        }else{
//            this.observationStorage.put(episode.get(episode.size()-1).first().first(), new HashMap<Integer, Integer>());
//
//            for(int j = 0; j < 5; j++)
//            {
//                this.observationStorage.get(episode.get(episode.size()-1).first().first()).put(j, new Integer((int)((j == lastAction) ? episode.get(episode.size() - 1).second().doubleValue() : INITIALQVALUE)));
//            }
//        }
//        epsilon = (epsilon <= 0.125) ? epsilon = 0.125 : epsilon-0.001;
//        System.out.println(epsilon);
//        episode = new ArrayList<Pair<Pair<String, Integer>,Double>>();
//    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

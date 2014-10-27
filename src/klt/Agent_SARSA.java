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
    private int episodeCounter = 30;
    private Integer lastAction;
    private double alpha = 0.2; //Lernrate
    private double gamma = 0.8; //Discountrate
    private double epsilon = 0.6; //exploration rate
    private final int INITIALQVALUE = 5; //initial q values

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
        episode.add(new Pair<Pair<String, Integer>, Double>(new Pair<String, Integer>(lastObservation, lastAction),v));
        lastObservation = observation.toString();

        Action returnAction = new Action(1, 0, 0);
        if (episodeCounter < 30){
            System.out.println("hi\n");
        }
        returnAction.intArray[0] = this.getBestAction(observation);

        lastAction = returnAction.intArray[0];
        return returnAction;
    }

    @Override
    public void agent_end(double v) {
        episode.add(new Pair<Pair<String, Integer>, Double>(new Pair<String, Integer>(lastObservation, lastAction),v));
        for(int i = 0; i < episode.size()-1; i++){
           String key = episode.get(i).first().first();
            if(observationStorage.containsKey(episode.get(i).first().first())){

                HashMap<Integer, Integer> states = observationStorage.get(episode.get(i).first().first());
                int valueBrain = states.get(episode.get(i).first().second());
                double valueEpisode = episode.get(i).second();
                states.put(episode.get(i).first().second(), (int) (valueBrain + alpha * (valueEpisode + gamma * episode.get(i+1).second() - valueBrain)));

            }else{
                this.observationStorage.put(key, new HashMap<Integer, Integer>());

                for(int j = 0; j < 5; j++)
                {
                    this.observationStorage.get(key).put(j, new Integer((j == lastAction) ? episode.get(i).second().intValue() : INITIALQVALUE));
                }
            }
        }
        this.observationStorage.get(episode.get(episode.size()-1).first().first()).put(lastAction, new Integer((int) episode.get(episode.size()-1).second().doubleValue()));

        episode = new ArrayList<Pair<Pair<String, Integer>,Double>>();
    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

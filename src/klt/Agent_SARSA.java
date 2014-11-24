package klt;

import klt.util.AgentLogUtil;
import klt.util.SarsaLogElement;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;

import klt.util.Actions_E;

/**
 * Created by Tobi on 26.10.2014.
 * Credit goes to "https://github.com/HAW-AI/PO-LA-2012-WS-Cliff" the general construction bases on their version
 */
public class Agent_SARSA extends Agent{

    //private List<Pair<Pair<String,Integer>,Double>> episode;
    private Observation lastObservation;
    private Observation beforeLastObservation;
    private AgentLogUtil agentLogUtil = new AgentLogUtil(0);

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

    public Agent_SARSA(int logLastN, String saveFilePath, double explorationRate, boolean trainingMode, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath, explorationRate, trainingMode, debugState);
        this.agentLogUtil = new AgentLogUtil(logLastN);
    }

    @Override
    public void agent_init(String s) {
        agentLogUtil.clear();
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
            //updateValues(v);

            agentLogUtil.add(lastObservation.toString(), lastAction);
            SarsaLogElement currentLogElem = agentLogUtil.getLastElem();

            double reward = v;
            double currentQ = INITIALQVALUE;
            //distribute reward
            if (this.observationStorage.containsKey(lastObservation)) {
                currentQ = observationStorage.get(lastObservation).get(lastAction);
            }
            reward = currentQ + alpha * (v - currentQ);


            currentLogElem.setValueNextAction(0);
            currentLogElem.setValueBefore(currentQ);
            currentLogElem.setReward(v);
            currentLogElem.setValueAfter(reward);

            setRewardForActionObservation(reward, lastObservation.toString(), lastAction, ((ObservationWithActions) lastObservation).getActions());

            {        //lower the explorationrate
                epsilon -= 0.0003;
                if (epsilon < EPSILONMINIMUM) {
                    epsilon = EPSILONMINIMUM;
                }
            }
            agentLogUtil.logLastQValueUodates(observationStorage);
        }
    }

    private void updateValues(double r){

        agentLogUtil.add(beforeLastObservation.toString(), beforeLastAction);
        SarsaLogElement currentElem = agentLogUtil.getLastElem();
        currentElem.setReward(r);

        double reward = r;
        double qNext = INITIALQVALUE;
        double qThis = INITIALQVALUE;

        //distribute reward
        if (this.observationStorage.containsKey(beforeLastObservation)){
            qThis = observationStorage.get(beforeLastObservation).get(beforeLastAction);
        }
        if (observationStorage.containsKey(lastObservation)) {
            qNext = observationStorage.get(lastObservation).get(lastAction);
        }
        reward = qThis + alpha * (r + gamma * qNext - qThis);

        currentElem.setValueAfter(reward);
        currentElem.setValueBefore(qThis);
        currentElem.setReward(r);
        currentElem.setValueNextAction(qNext);
        setRewardForActionObservation(reward, beforeLastObservation.toString(), beforeLastAction, ((ObservationWithActions) beforeLastObservation).getActions());
    }

    @Override
    public String agent_message(String s) {
        return null;
    }
}

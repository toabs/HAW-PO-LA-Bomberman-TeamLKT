package klt.agent;

import klt.ObservationWithActions;
import klt.util.Actions_E;
import klt.util.AgentLogUtil;
import klt.util.DebugState;
import klt.util.SarsaLogElement;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import java.io.IOException;

/**
 * Created by Tobi on 26.10.2014.
 * Credit goes to "https://github.com/HAW-AI/PO-LA-2012-WS-Cliff" the general construction bases on their version
 *
 * This is an implementation which uses the Sarsa algorithm.
 */
public class AgentSarsa extends Agent{

    //private List<Pair<Pair<String,Integer>,Double>> episode;
    private ObservationWithActions lastObservation;
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
     * With this constructor it will just run what the agent learned. The agent will not train or log anything.
     * @param saveFilePath
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public AgentSarsa(String saveFilePath) throws IOException, ClassNotFoundException {
        this(saveFilePath, 0.0, false, DebugState.NO_DEBUG);
    }

    /**
     * With this constructor the agent will run and learn normally.
     *
     * @param saveFilePath          Path to save the reults to.
     * @param explorationRate       Exploration rate for this instance.
     * @param trainingMode          Is this instance allowed to train?
     * @param debugState            The debug state for this instances of environment, agent and KI.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public AgentSarsa(String saveFilePath, double explorationRate, boolean trainingMode, DebugState debugState) throws IOException, ClassNotFoundException {
        super(saveFilePath, debugState);
        this.epsilon = explorationRate;
        this.trainingMode = trainingMode;
    }

    /**
     * With this constructor the agent will run normally but he will also log the last q value changes after each round.
     *
     * @param logLastN              Number of q value changes to log.
     * @param saveFilePath          Path to save the training results to.
     * @param explorationRate       Exploration rate for this instance.
     * @param trainingMode          Is this instance allowed to train?
     * @param debugState            The debug state for this instances of environment, agent and KI.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public AgentSarsa(int logLastN, String saveFilePath, double explorationRate, boolean trainingMode, DebugState debugState) throws IOException, ClassNotFoundException {
        this(saveFilePath, explorationRate, trainingMode, debugState);
        debugState.setqLoggingEnabled(true);
        this.agentLogUtil = new AgentLogUtil(logLastN);
    }

    /**
     * This method is called at the start of each round.
     * Here it is used to clear the logging utility if it is used at all.
     *
     * @param s     Not used here.
     */
    @Override
    public void agent_init(String s) {
        if(debugState.isqLoggingEnabled()) {
            agentLogUtil.clear();
        }
    }

    /**
     * This method is called at the start and is used to determine the first action for a round.
     *
     * @param observation   The current observation.
     * @return      An action with the highest reward for the given observation.
     */
    @Override
    public Action agent_start(Observation observation) {
        lastObservation = (ObservationWithActions) observation;
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, ((ObservationWithActions) observation).getActions());

        lastAction = returnAction.intArray[0];
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
        beforeLastAction = lastAction;
        beforeLastObservation = lastObservation;
        lastObservation = (ObservationWithActions) observation;

        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = this.getBestAction(observation, ((ObservationWithActions) observation).getActions());

        if (trainingMode) {         //if the trainingmode is enabled the agent will sometimes randomly choose a random action
            if (this.randGenerator.nextDouble() < epsilon) {
                Actions_E[] actionArray = ((ObservationWithActions) observation).getActions().toArray(new Actions_E[((ObservationWithActions) observation).getActions().size()]);
                
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

    /**
     * This method is called at the end of a round.
     * It is used to take the reward for the last action into account.
     *
     * @param v         The reward for the last action.
     */
    @Override
    public void agent_end(double v) {
        if(trainingMode) {
            //updateValues(v);

            SarsaLogElement currentLogElem = null;
            if(debugState.isqLoggingEnabled()) {
                agentLogUtil.add(lastObservation.toString(), lastAction);
                currentLogElem = agentLogUtil.getLastElem();
            }

            double reward;
            double currentQ = INITIALQVALUE;
            //distribute reward
            if (this.observationStorage.containsKey(lastObservation)) {
                currentQ = observationStorage.get(lastObservation).get(lastAction);
            }
            reward = currentQ + alpha * (v - currentQ);


            if(debugState.isqLoggingEnabled()) {
                currentLogElem.setValueNextAction(0);
                currentLogElem.setValueBefore(currentQ);
                currentLogElem.setReward(v);
                currentLogElem.setValueAfter(reward);
            }

            setRewardForActionObservation(reward, lastObservation.toString(), lastAction);

                        //lower the explorationrate
            epsilon -= 0.0003;
            //epsilon -= 0.03;
            if (epsilon < EPSILONMINIMUM) {
                epsilon = EPSILONMINIMUM;
            }

            if(debugState.isqLoggingEnabled()) {
                agentLogUtil.logLastQValueUodates(observationStorage);
            }
        }
    }

    /**
     * Updates the reward according to the rules of the algorithm.
     *
     * @param r     Reward to take into account.
     */
    private void updateValues(double r){

        SarsaLogElement currentElem = null;
        if(debugState.isqLoggingEnabled()) {
            agentLogUtil.add(beforeLastObservation.toString(), beforeLastAction);
            currentElem = agentLogUtil.getLastElem();
            currentElem.setReward(r);
        }

        double reward;
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

        if(debugState.isqLoggingEnabled()) {
            currentElem.setValueAfter(reward);
            currentElem.setValueBefore(qThis);
            currentElem.setReward(r);
            currentElem.setValueNextAction(qNext);
        }

        setRewardForActionObservation(reward, beforeLastObservation.toString(), beforeLastAction);
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

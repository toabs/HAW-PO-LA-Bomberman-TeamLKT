package klt.util;

import java.util.HashMap;

/**
 * Created by Tobi on 23.11.2014.
 */
public class SarsaLogElement {

    private String observation;
    private Integer action;
    private double valueBefore;
    private double valueAfter;
    private double valueNextAction;
    private double reward;
    private double epsilon;

    public SarsaLogElement(String obs, Integer action){
        this.observation = obs;
        this.action = action;
    }

    public void setValueAfter(double valueAfter){
        this.valueAfter = valueAfter;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void setValueBefore(double valueBefore){
        this.valueBefore = valueBefore;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public void setValueNextAction(double valueNextAction) {
        this.valueNextAction = valueNextAction;
    }

    public String getObservation() {
        return observation;
    }

    public Integer getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SarsaLogElement that = (SarsaLogElement) o;

        if (!action.equals(that.action)) return false;
        if (!observation.equals(that.observation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = observation.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }

    public void logElem(HashMap<String, HashMap<Integer, Double>> storage, boolean sarsaLMode){
        System.out.println("Observation: " + observation + " Action: " + action + "\n");
        HashMap<Integer, Double> actionQValues = storage.get(observation);
        System.out.print("Value before: ");
        for (int i = 0; i < 6; i++){
            double value;
            if (actionQValues.containsKey(i)){
                 value = actionQValues.get(i);
            }else{
                value = -1;
            }

            if (i == action){
                value = valueBefore;
            }

            System.out.print(i + ": " + value + " | ");
        }

        System.out.println("\n\n\t\t\t ||");
        System.out.print("\t\t\t ||" + "\t\t Reward: " + reward + "\t QValueNextAction: " + valueNextAction);
        if(sarsaLMode){
            System.out.print("\t epsilon: " + epsilon);
        }
        System.out.println("\n\t\t\t \\/\n");
        System.out.print("Value after: ");

        for (int i = 0; i < 6; i++){
            double value;
            if (actionQValues.containsKey(i)){
                value = actionQValues.get(i);
            }else{
                value = -1;
            }

            if (i == action){
                value = valueAfter;
            }

            System.out.print(i + ": " + value + " | ");
        }
        System.out.print("\n");
    }
}

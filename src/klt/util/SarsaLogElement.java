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

    public SarsaLogElement(String obs, Integer action){
        this.observation = obs;
        this.action = action;
        this.valueBefore = valueBefore;
    }

    public void setValueAfter(double valueAfter){
        this.valueAfter = valueAfter;
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

    public double getValueBefore() {
        return valueBefore;
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

    public double getValueAfter() {

        return valueAfter;
    }

    public double getValueNextAction() {
        return valueNextAction;
    }

    public double getReward() {
        return reward;
    }

    public void logElem(HashMap<String, HashMap<Integer, Double>> storage){
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
        System.out.println("\t\t\t ||" + "\t\t Reward: " + reward + "\t QValueNextAction: " + valueNextAction);
        System.out.println("\t\t\t \\/\n");
        System.out.print("Value after: ");

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
        System.out.print("\n");
    }
}

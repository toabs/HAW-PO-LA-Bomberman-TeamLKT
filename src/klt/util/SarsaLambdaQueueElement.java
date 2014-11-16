package klt.util;

/**
 * Created by Tobi on 16.11.2014.
 */
public class SarsaLambdaQueueElement {
    private double value;
    private String observation;
    private Integer action;

    public SarsaLambdaQueueElement(String observation, int action, double value){
        this.value=value;
        this.observation=observation;
        this.action=action;
    }

    public SarsaLambdaQueueElement(SarsaLambdaQueueElement other){
        this.value = other.getValue();
        this.observation = other.getObservation();
        this.action = other.getAction();
    }

    public double getValue() {
        return value;
    }

    public String getObservation() {
        return observation;
    }

    public Integer getAction() {
        return action;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SarsaLambdaQueueElement that = (SarsaLambdaQueueElement) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (observation != null ? !observation.equals(that.observation) : that.observation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = observation != null ? observation.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }
}

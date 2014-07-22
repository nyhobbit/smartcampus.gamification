package eu.trentorise.game.application.model;

/**
 *
 * @author Luca Piras
 */
public class EnumerationParam {
    
    protected String name;
    protected Action action;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "EnumerationParam{" + "name=" + name + ", action=" + action + '}';
    }
}
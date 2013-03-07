package mil.af.rl.jcat.integration;

import java.util.ArrayList;

public class MTask extends Task {
    
    private ArrayList<String> effects;

    public MTask(String gd, String name, int taskType) {
        super(gd, name, taskType);
        effects = new ArrayList<String>();
        
    }
    public void addEffect(String effect)
    {
        effects.add(effect);
    }
    public ArrayList<String> getEffects()
    {
        return effects;
    }

}

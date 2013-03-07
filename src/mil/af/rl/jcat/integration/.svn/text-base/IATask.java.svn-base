package mil.af.rl.jcat.integration;

import java.util.HashMap;

import mil.af.rl.jcat.util.Guid;

public class IATask extends Task {
    
    private HashMap<Integer,Integer> mapping = new HashMap<Integer,Integer>();
    public IATask(String name, int taskType) {
        super(new Guid().toString(), name, taskType);
        
    }
    
    
    
    // in here we will rnor the probabilities if they happen at the same time to reflect
    // numerous instances of the same attack, in this case we assume they occur with the
    // same probability
    public void addTime(int time, float prob)
    {
        
        if(mapping.containsKey(time))
        {
            int i = mapping.get(time);
            mapping.put(time,++i);
        }else{
            mapping.put(time,1);
        }
        if(super.schedule.containsKey(time))
        {
            System.out.println("ADDING time "+time+" prob: "+prob);
            // rnor the bitch with the new prob (which will be new default prob for that happening)
            schedule.put(time,rnor(mapping.get(time),prob));
        }else{
            schedule.put(time,prob);
        }
    }
    
    private float rnor(int instance, float prob)
    {
        return 1.0f - (float)Math.pow(1-prob,instance);
    }
    
    

}

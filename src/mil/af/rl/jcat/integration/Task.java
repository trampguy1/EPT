package mil.af.rl.jcat.integration;

import java.util.HashMap;

import mil.af.rl.jcat.util.Guid;

public class Task {

    public static int SCHEDULE = 0;
    public static int EVIDENCE = 1;
    private String name;
    private Guid guid;
    private String subject;
    private boolean proximity;
    protected HashMap<Integer,Float> schedule;
    private int type = 0;
    
    
    public Task(String gd, String name, int taskType) {
        guid = new Guid(gd);
        this.name = name;
        schedule = new HashMap<Integer,Float>();
        type = taskType;
    }
    
    public Task(String name,int taskType)
    {
    	guid = new Guid();
    	this.name = name;
        schedule = new HashMap<Integer,Float>();
        type = taskType;
    	
    }
    
    public int getTaskType()
    {
        return type;
    }
    
    public void addTime(int time, float prob)
    {
        schedule.put(time,prob);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean getVacinity() {
        return proximity;
    }

    public void setVacinity(boolean vacinity) {
        proximity = vacinity;
    }

    public Guid getGuid() {
        return guid;
    }

    public HashMap<Integer, Float> getSchedule() {
        return schedule;
    }

}

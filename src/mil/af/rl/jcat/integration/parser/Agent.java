package mil.af.rl.jcat.integration.parser;


public interface Agent extends Runnable{
    
    public static int OUPUT = 0;
    public static int INPUT = 1;
    
    public void setExecuteInterval(int sec); 
    public void detach();
    public boolean isAlive();
    public int getInterval();
    public int getType();
    
}

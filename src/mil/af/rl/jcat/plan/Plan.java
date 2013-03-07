package mil.af.rl.jcat.plan;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: Plan.java
 * </p>
 * <p>
 * Description: EnvUtils the methods a plan should implement
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: C3I Associates
 * </p>
 * 
 * @author Edward Verenich
 * @version 1.0
 */

public interface Plan
{
    /**
     * Adds an event to a plan
     * 
     * @param e
     *            Event
     */
    public void addEvent(Event e);

    /**
     * Removes an event from a plan
     * 
     * @param e
     *            Event event to remove
     * @return boolean true is rmoved
     */
    public void removeEvent(Event e);

    /**
     * Method removes an event given its guid
     * 
     * @param guid
     *            String
     * @return boolean
     */
    public void removeEvent(String guid);

    /**
     * Adds a mechanism to a plan
     * 
     * @param m
     *            Mechanism
     */
    public void addMechanism(Mechanism m);

    /**
     * Removes a mechanism from a plan
     * 
     * @param m
     *            Mechanism to remove
     * @return boolean true if removed
     */
    public void removeMechanism(Mechanism m);

    /**
     * Method removes a mechanism given its guid
     * 
     * @param guid
     *            String
     * @return boolean
     */
    public void removeMechanism(String guid);

    /**
     * Returns a plan item given the guid
     * 
     * @param guid
     *            String item id
     * @return item Object
     */
    public Object findMechanism(String guid);

    /**
     * Returns a plan item given the guid
     * 
     * @param guid
     *            String
     * @return Object
     */
    public Object findEvent(String guid);

    /**
     * Method adds a collection of events
     * 
     * @param events
     *            Map
     */
    public void addEvents(Map events);

    /**
     * Method adds a collection of mechanisms
     * 
     * @param mechanisms
     *            Map
     */
    public void addMechanisms(Map mechanisms);

    /**
     * @return
     */
    public HashMap getItems();
}


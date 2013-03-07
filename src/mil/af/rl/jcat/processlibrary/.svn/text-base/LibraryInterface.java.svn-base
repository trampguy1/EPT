/*
 * Created on Jun 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package mil.af.rl.jcat.processlibrary;

import java.util.Collection;
import java.util.Vector;

import mil.af.rl.jcat.exceptions.SignalException;
import mil.af.rl.jcat.exceptions.SignalModeConflictException;
import mil.af.rl.jcat.util.Guid;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author lemmerj
 * 
 */
public interface LibraryInterface
{
    /**
     * Adds an existing signal to an existing process. It is added to this
     * process as a cause
     * 
     * @param process
     *            Guid of the process to which signal is to be added as a cause;
     *            the guid must be one returned by createProcess()
     * @param signal
     *            Guid of signal to be added to 'process'; this Guid must be one
     *            returned by createSignal
     * @return the 'mode' of the signal: in this case SignalType.CAUSE
     * @throws SignalModeConflictException
     */
    public abstract int addCause(Guid process, Guid signal)
            throws SignalModeConflictException;

    /**
     * Adds an existing signal to an existing process. It is added to this
     * process as a effect
     * 
     * @param process
     *            Guid of the process to which signal is to be added as a
     *            effect; the guid must be one returned by createProcess()
     * @param signal
     *            Guid of signal to be added to 'process'; this Guid must be one
     *            returned by createSignal
     * @return the 'mode' of the signal: in this case SignalType.EFFECT
     * @throws SignalModeConflictException
     */
    public abstract int addEffect(Guid process, Guid signal)
            throws SignalModeConflictException;

    /**
     * @param process
     *            The guid of the process whose SignalData is being modified
     * @param protocol
     *            As of 1 June 04, the only implemented protocol is RNOR.
     *            Protocols such as AND will be added later
     * @param signals
     *            the group of signals for which the probability is being
     *            provided. All signals in the group must already be in the same
     *            ModeSet, and they must not be distributed across different
     *            protocol sets. If they are not in the same mode set, or if
     *            subsets appear in different protocol sets, a "Bad Elicited
     *            Valeu" exception will be thrown
     * @param prob
     *            The probability that the set of signals, acting together, but
     *            with no other signals, and ignoring any leak will [cause,
     *            inhibit, result from] the process].
     * @return SignalType for the protocol into which this elicited value is
     *         placed.
     */
    public abstract boolean addElicitedValue(Guid process, int protocol,
            Collection signals, float prob, String grpName);

    /**
     * Adds an existing signal to an existing process. It is added to this
     * process as an inhibitor
     * 
     * @param process
     *            Guid of the process to which signal is to be added as an
     *            inhibitor; the guid must be one returned by createProcess()
     * @param signal
     *            Guid of signal to be added to 'process'; this Guid must be one
     *            returned by createSignal
     * @return the 'mode' of the signal: in this case SignalType.INHIBIT
     * @throws SignalModeConflictException
     */
    public abstract int addInhibitor(Guid process, Guid signal)
            throws SignalModeConflictException;

    public abstract String getSignalName(Guid sig);

    public abstract boolean setSignalInversion(Guid proc, Guid sig,
            boolean inverted);

    public abstract Document addProcessToDocument(Guid proc, Document data,
            Element el);

    public abstract Guid createProcess(float[] defaults, int defaultsSubType);

    public abstract Guid createSignal();

    /**
     * @param process
     *            Guid of the process from which the elicited value is to be
     *            deleted
     * @param signals
     *            The set of signals (e.g. groupr) for which the value has been
     *            elicicted
     * @return true if the elicitation has been found and the deletion has
     *         succeeded. The method searchs in the process for the mode and
     *         protocol containing exactly the signals in collection. If found
     *         the elicitation is deleted. Note that these signals themselves
     *         are not removed from the protocol, the mode or the process
     */
    public abstract boolean deleteElicitedValue(Guid process, Collection signals);

    public abstract boolean deleteSignalFromProcess(Guid process, Guid signal);// returns

    public abstract boolean deserializeLibrary(String fromURL);

    /*public abstract boolean getCPTable(Guid p, Vector guidActiveSignals,
            Vector causalCPT, Vector causalOrder, Vector inhibitingCPT,
            Vector inhibitingOrder, Vector effectPT, Vector effectOrder)
            throws SignalException;*/

    public void getSignalCPT(Guid signalID, Vector activeSignals,
            Vector causalCPT, Vector causalOrder);

//    /**
//     * @return Returns the defaultSingleSignalCausalProbability.
//     */
//    public abstract float getDefaultSingleSignalCausalProbability();
//
//    /**
//     * @return Returns the defaultSingleSignalEffectProbability.
//     */
//    public abstract float getDefaultSingleSignalEffectProbability();
//
//    /**
//     * @return Returns the defaultSingleSignalInhibitProbability.
//     */
//    public abstract float getDefaultSingleSignalInhibitProbability();

    public abstract Document getProcessDocument(Guid guid);

    /**
     * @param p
     *            Guid of the process in which to form the AND group
     * @param grp
     *            Collection of signals already in the process's NOR group from
     *            which to contstruct the AND group
     * @param andProb
     *            the probability of causing process when all the signals in the
     *            group are active
     * @throws SignalException
     *             exception message explains cause
     */
    public abstract void organizeSimpleANDGroup(Guid p, Collection grp,
            float andProb) throws SignalException;

    public abstract boolean serializeLibrary(String toURL);

//    /**
//     * @param defaultSingleSignalCausalProbability
//     *            The defaultSingleSignalCausalProbability to set.
//     */
//    public abstract void setDefaultSingleSignalCausalProbability(
//            float defaultSingleSignalCausalProbability);
//
//    /**
//     * @param defaultSingleSignalEffectProbability
//     *            The defaultSingleSignalEffectProbability to set.
//     */
//    public abstract void setDefaultSingleSignalEffectProbability(
//            float defaultSingleSignalEffectProbability);
//
//    /**
//     * @param defaultSingleSignalInhibitProbability
//     *            The defaultSingleSignalInhibitProbability to set.
//     */
//    public abstract void setDefaultSingleSignalInhibitProbability(
//            float defaultSingleSignalInhibitProbability);

    public abstract void setProcessFromDocument(Document data, Element el);

    public abstract void setProcessName(Guid proc, String name);

    public abstract void setSignalName(Guid sig, String name);

    public void getSignalAdder(Guid signalID, Vector activeSignals,
            Vector causalCPT, Vector causalOrder);
}
/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.util;

import org.eclipse.emf.ecore.EObject;

import com.tibco.xpd.n2.gat.adapters.EventFlowType;
import com.tibco.xpd.n2.gat.adapters.EventTriggerType;
import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.CatchThrow;
import com.tibco.xpd.xpdl2.EndEvent;
import com.tibco.xpd.xpdl2.Event;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.IntermediateEvent;
import com.tibco.xpd.xpdl2.ResultType;
import com.tibco.xpd.xpdl2.StartEvent;
import com.tibco.xpd.xpdl2.TriggerResultLink;
import com.tibco.xpd.xpdl2.TriggerResultSignal;
import com.tibco.xpd.xpdl2.TriggerTimer;
import com.tibco.xpd.xpdl2.TriggerType;

/**
 * Extracted utility methods from {@code com.tibco.xpd.processeditor.xpdl2.util.EventObjectUtil}.
 *
 * <p>Contains the 8 methods used by GAT event generators. All methods are pure
 * EMF model navigation with no Eclipse Platform dependency.</p>
 *
 * @see Section 5.4 and Appendix C of the design document
 */
public final class GatEventObjectUtil
{
    private GatEventObjectUtil()
    {
        // Static utility class
    }

    /**
     * Determine the EventTriggerType for an event activity.
     *
     * <p>Big switch over TriggerType/ResultType values for Start/Intermediate/End events.
     * For intermediate events, checks CatchThrow to distinguish catch vs throw variants.</p>
     *
     * @param activity the event activity
     * @return the EventTriggerType, or EVENT_NONE if unknown
     */
    public static EventTriggerType getEventTriggerType(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return EventTriggerType.EVENT_NONE_LITERAL;
        }

        Event event = activity.getEvent();

        if (event instanceof StartEvent)
        {
            return getStartEventTriggerType((StartEvent) event);
        }
        else if (event instanceof IntermediateEvent)
        {
            return getIntermediateEventTriggerType((IntermediateEvent) event, activity);
        }
        else if (event instanceof EndEvent)
        {
            return getEndEventTriggerType((EndEvent) event);
        }

        return EventTriggerType.EVENT_NONE_LITERAL;
    }

    private static EventTriggerType getStartEventTriggerType(StartEvent startEvent)
    {
        TriggerType trigger = startEvent.getTrigger();
        if (trigger == null)
        {
            return EventTriggerType.EVENT_NONE_LITERAL;
        }

        switch (trigger.getValue())
        {
			case TriggerType.NONE:
                return EventTriggerType.EVENT_NONE_LITERAL;
			case TriggerType.MESSAGE:
                return EventTriggerType.EVENT_MESSAGE_CATCH_LITERAL;
			case TriggerType.TIMER:
                return EventTriggerType.EVENT_TIMER_LITERAL;
			case TriggerType.SIGNAL:
                return EventTriggerType.EVENT_SIGNAL_CATCH_LITERAL;
			case TriggerType.CONDITIONAL:
                return EventTriggerType.EVENT_CONDITIONAL_LITERAL;
			case TriggerType.MULTIPLE:
                return EventTriggerType.EVENT_MULTIPLE_CATCH_LITERAL;
            default:
                return EventTriggerType.EVENT_NONE_LITERAL;
        }
    }

    private static EventTriggerType getIntermediateEventTriggerType(
            IntermediateEvent intermediateEvent, Activity activity)
    {
        TriggerType trigger = intermediateEvent.getTrigger();
        if (trigger == null)
        {
            return EventTriggerType.EVENT_NONE_LITERAL;
        }

        CatchThrow catchThrow = getCatchThrowType(activity);

        switch (trigger.getValue())
        {
			case TriggerType.NONE:
                return EventTriggerType.EVENT_NONE_LITERAL;
			case TriggerType.MESSAGE:
                if (catchThrow == CatchThrow.THROW)
                {
                    return EventTriggerType.EVENT_MESSAGE_THROW_LITERAL;
                }
                return EventTriggerType.EVENT_MESSAGE_CATCH_LITERAL;
			case TriggerType.TIMER:
                return EventTriggerType.EVENT_TIMER_LITERAL;
			case TriggerType.ERROR:
                return EventTriggerType.EVENT_ERROR_LITERAL;
			case TriggerType.CANCEL:
                return EventTriggerType.EVENT_CANCEL_LITERAL;
			case TriggerType.COMPENSATION:
                if (catchThrow == CatchThrow.THROW)
                {
                    return EventTriggerType.EVENT_COMPENSATION_THROW_LITERAL;
                }
                return EventTriggerType.EVENT_COMPENSATION_CATCH_LITERAL;
			case TriggerType.SIGNAL:
                if (catchThrow == CatchThrow.THROW)
                {
                    return EventTriggerType.EVENT_SIGNAL_THROW_LITERAL;
                }
                return EventTriggerType.EVENT_SIGNAL_CATCH_LITERAL;
			case TriggerType.MULTIPLE:
                if (catchThrow == CatchThrow.THROW)
                {
                    return EventTriggerType.EVENT_MULTIPLE_THROW_LITERAL;
                }
                return EventTriggerType.EVENT_MULTIPLE_CATCH_LITERAL;
			case TriggerType.LINK:
                if (catchThrow == CatchThrow.THROW)
                {
                    return EventTriggerType.EVENT_LINK_THROW_LITERAL;
                }
                return EventTriggerType.EVENT_LINK_CATCH_LITERAL;
			case TriggerType.CONDITIONAL:
                return EventTriggerType.EVENT_CONDITIONAL_LITERAL;
            default:
                return EventTriggerType.EVENT_NONE_LITERAL;
        }
    }

    private static EventTriggerType getEndEventTriggerType(EndEvent endEvent)
    {
        ResultType result = endEvent.getResult();
        if (result == null)
        {
            return EventTriggerType.EVENT_NONE_LITERAL;
        }

        switch (result.getValue())
        {
			case ResultType.NONE:
                return EventTriggerType.EVENT_NONE_LITERAL;
			case ResultType.MESSAGE:
                return EventTriggerType.EVENT_MESSAGE_THROW_LITERAL;
			case ResultType.ERROR:
                return EventTriggerType.EVENT_ERROR_LITERAL;
			case ResultType.CANCEL:
                return EventTriggerType.EVENT_CANCEL_LITERAL;
			case ResultType.COMPENSATION:
                return EventTriggerType.EVENT_COMPENSATION_THROW_LITERAL;
			case ResultType.SIGNAL:
                return EventTriggerType.EVENT_SIGNAL_THROW_LITERAL;
			case ResultType.TERMINATE:
                return EventTriggerType.EVENT_TERMINATE_LITERAL;
			case ResultType.MULTIPLE:
                return EventTriggerType.EVENT_MULTIPLE_THROW_LITERAL;
            default:
                return EventTriggerType.EVENT_NONE_LITERAL;
        }
    }

    /**
     * Check if an event activity is attached to a task (i.e., is a boundary event).
     *
     * <p>An event is attached if it is an intermediate event and has an incoming
     * transition from another activity, or if it has a target attribute set.</p>
     *
     * @param activity the event activity
     * @return true if it is a boundary event attached to a task
     */
    public static boolean isAttachedToTask(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return false;
        }
        Event event = activity.getEvent();
        if (event instanceof IntermediateEvent)
        {
            IntermediateEvent ie = (IntermediateEvent) event;
            String target = ie.getTarget();
            return target != null && !target.isEmpty();
        }
        return false;
    }

    /**
     * Get the task activity to which a boundary event is attached.
     *
     * @param activity the boundary event activity
     * @return the host task activity, or null if not attached
     */
    public static Activity getTaskAttachedTo(Activity activity)
    {
        String taskId = getTaskIdAttachedTo(activity);
        if (taskId == null)
        {
            return null;
        }
        FlowContainer flowContainer = activity.getFlowContainer();
        if (flowContainer != null && flowContainer.getActivities() != null)
        {
            for (Activity a : flowContainer.getActivities())
            {
                if (taskId.equals(a.getId()))
                {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * Get the ID of the task to which a boundary event is attached.
     *
     * @param activity the boundary event activity
     * @return the host task ID, or null
     */
    public static String getTaskIdAttachedTo(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return null;
        }
        Event event = activity.getEvent();
        if (event instanceof IntermediateEvent)
        {
            return ((IntermediateEvent) event).getTarget();
        }
        return null;
    }

    /**
     * Check if an event is non-cancelling (via xpdExtension attribute).
     *
     * @param activity the event activity
     * @return true if non-cancelling (non-interrupting)
     */
    public static boolean isNonCancellingEvent(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return false;
        }
        Event ev = activity.getEvent();
        EObject triggerNode = ev.getEventTriggerTypeNode();

        if (triggerNode instanceof TriggerTimer)
        {
            Object val = GatModelUtil.getOtherAttribute(triggerNode,
                    XpdExtensionPackage.eINSTANCE.getDocumentRoot_ContinueOnTimeout());
            if (val instanceof Boolean)
            {
                return ((Boolean) val).booleanValue();
            }
        }
        else if (triggerNode instanceof TriggerResultSignal)
        {
            Object val = GatModelUtil.getOtherAttribute(triggerNode,
                    XpdExtensionPackage.eINSTANCE.getDocumentRoot_NonCancelling());
            if (val instanceof Boolean)
            {
                return ((Boolean) val).booleanValue();
            }
        }

        return false;
    }

    /**
     * Get the link event ID for a link event.
     *
     * @param activity the link event activity
     * @return the link event target/source ID, or null
     */
    public static String getLinkEventId(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return null;
        }
        Event event = activity.getEvent();
        if (event instanceof IntermediateEvent)
        {
            TriggerResultLink link = ((IntermediateEvent) event).getTriggerResultLink();
            if (link != null)
            {
                return link.getName();
            }
        }
        return null;
    }

    /**
     * Get the CatchThrow type for an event activity.
     *
     * <p>For intermediate events, this determines whether the event catches or
     * throws. Uses the XPDL IntermediateEvent's TriggerResultLink if available,
     * otherwise infers from xpdExtension or connectivity.</p>
     *
     * @param activity the event activity
     * @return CatchThrow.CATCH or CatchThrow.THROW
     */
    public static CatchThrow getCatchThrowType(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return CatchThrow.CATCH;
        }
        Event event = activity.getEvent();
        if (event instanceof IntermediateEvent)
        {
            IntermediateEvent ie = (IntermediateEvent) event;
            // Check if it's attached (boundary) -- boundary events are always catch
            if (ie.getTarget() != null && !ie.getTarget().isEmpty())
            {
                return CatchThrow.CATCH;
            }
            // Check trigger type to infer catch/throw
            TriggerType trigger = ie.getTrigger();
            if (trigger != null)
            {
                switch (trigger.getValue())
                {
					case TriggerType.ERROR:
					case TriggerType.TIMER:
					case TriggerType.CONDITIONAL:
                        return CatchThrow.CATCH;
                    default:
                        // For message, signal, compensation, link, multiple:
                        // check xpdExtension CatchThrow attribute
                        Object ct = GatModelUtil.getOtherAttribute(activity,
                                XpdExtensionPackage.eINSTANCE.getDocumentRoot_CatchThrow());
                        if (ct instanceof CatchThrow)
                        {
                            return (CatchThrow) ct;
                        }
                        break;
                }
            }
        }
        else if (event instanceof StartEvent)
        {
            return CatchThrow.CATCH;
        }
        else if (event instanceof EndEvent)
        {
            return CatchThrow.THROW;
        }

        return CatchThrow.CATCH;
    }

    /**
     * Get the EventFlowType for an event activity (start/intermediate/end).
     *
     * @param activity the event activity
     * @return the EventFlowType
     */
    public static EventFlowType getFlowType(Activity activity)
    {
        if (activity == null || activity.getEvent() == null)
        {
            return null;
        }
        Event event = activity.getEvent();
        if (event instanceof StartEvent)
        {
            return EventFlowType.FLOW_START_LITERAL;
        }
        else if (event instanceof IntermediateEvent)
        {
            return EventFlowType.FLOW_INTERMEDIATE_LITERAL;
        }
        else if (event instanceof EndEvent)
        {
            return EventFlowType.FLOW_END_LITERAL;
        }
        return null;
    }
}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.adapters;

/**
 * Standalone version of {@code com.tibco.xpd.processwidget.adapters.EventTriggerType}.
 *
 * <p>Same pattern as {@link TaskType}: custom class with {@code int type} field
 * and static {@code *_LITERAL} instances. GAT code uses {@code .equals()}
 * comparisons against these constants to determine the event definition type
 * for each XPDL event.</p>
 */
public class EventTriggerType
{
    private final int    type;
    private final String name;

    private EventTriggerType(int type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public int getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    // Event trigger type constants
    public static final EventTriggerType EVENT_NONE_LITERAL                = new EventTriggerType(0, "None");
    public static final EventTriggerType EVENT_MESSAGE_CATCH_LITERAL       = new EventTriggerType(1, "MessageCatch");
    public static final EventTriggerType EVENT_MESSAGE_THROW_LITERAL       = new EventTriggerType(2, "MessageThrow");
    public static final EventTriggerType EVENT_TIMER_LITERAL               = new EventTriggerType(3, "Timer");
    public static final EventTriggerType EVENT_ERROR_LITERAL               = new EventTriggerType(4, "Error");
    public static final EventTriggerType EVENT_CANCEL_LITERAL              = new EventTriggerType(5, "Cancel");
    public static final EventTriggerType EVENT_COMPENSATION_CATCH_LITERAL  = new EventTriggerType(6, "CompensationCatch");
    public static final EventTriggerType EVENT_COMPENSATION_THROW_LITERAL  = new EventTriggerType(7, "CompensationThrow");
    public static final EventTriggerType EVENT_CONDITIONAL_LITERAL         = new EventTriggerType(8, "Conditional");
    public static final EventTriggerType EVENT_MULTIPLE_CATCH_LITERAL      = new EventTriggerType(9, "MultipleCatch");
    public static final EventTriggerType EVENT_MULTIPLE_THROW_LITERAL      = new EventTriggerType(10, "MultipleThrow");
    public static final EventTriggerType EVENT_TERMINATE_LITERAL           = new EventTriggerType(11, "Terminate");
    public static final EventTriggerType EVENT_SIGNAL_CATCH_LITERAL        = new EventTriggerType(12, "SignalCatch");
    public static final EventTriggerType EVENT_SIGNAL_THROW_LITERAL        = new EventTriggerType(13, "SignalThrow");
    public static final EventTriggerType EVENT_LINK_CATCH_LITERAL          = new EventTriggerType(14, "LinkCatch");
    public static final EventTriggerType EVENT_LINK_THROW_LITERAL          = new EventTriggerType(15, "LinkThrow");

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof EventTriggerType)) return false;
        return type == ((EventTriggerType) obj).type;
    }

    @Override
    public int hashCode()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

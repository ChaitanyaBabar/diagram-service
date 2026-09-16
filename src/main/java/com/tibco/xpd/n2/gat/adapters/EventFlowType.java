/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.adapters;

/**
 * Standalone version of {@code com.tibco.xpd.processwidget.adapters.EventFlowType}.
 *
 * <p>Represents the position of an event in the process flow (start, intermediate, end).
 * Used by event generators to determine event shape and behavior.</p>
 */
public class EventFlowType
{
    private final int    type;
    private final String name;

    private EventFlowType(int type, String name)
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

    // Flow type constants
    public static final EventFlowType FLOW_START_LITERAL        = new EventFlowType(0, "Start");
    public static final EventFlowType FLOW_INTERMEDIATE_LITERAL = new EventFlowType(1, "Intermediate");
    public static final EventFlowType FLOW_END_LITERAL          = new EventFlowType(2, "End");

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof EventFlowType)) return false;
        return type == ((EventFlowType) obj).type;
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

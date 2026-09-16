/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.adapters;

/**
 * Standalone version of {@code com.tibco.xpd.processwidget.adapters.TaskType}.
 *
 * <p>The original class uses a custom pattern with {@code int type} field and
 * static {@code *_LITERAL} instances (NOT a Java enum). The GAT generation code
 * only uses {@code .equals()} comparisons against these literal constants.</p>
 *
 * <p>The {@code name} field in the original has NLS (externalized string)
 * dependency. Since GAT code never uses the name, we hardcode simple names here.</p>
 */
public class TaskType
{
    private final int    type;
    private final String name;

    private TaskType(int type, String name)
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

    // Type constants matching the original TaskType values
    public static final TaskType NONE_LITERAL                = new TaskType(0, "None");
    public static final TaskType SERVICE_LITERAL             = new TaskType(1, "Service");
    public static final TaskType USER_LITERAL                = new TaskType(2, "User");
    public static final TaskType MANUAL_LITERAL              = new TaskType(3, "Manual");
    public static final TaskType RECEIVE_LITERAL             = new TaskType(4, "Receive");
    public static final TaskType REFERENCE_LITERAL           = new TaskType(5, "Reference");
    public static final TaskType SCRIPT_LITERAL              = new TaskType(6, "Script");
    public static final TaskType SEND_LITERAL                = new TaskType(7, "Send");
    public static final TaskType SUBPROCESS_LITERAL          = new TaskType(8, "SubProcess");
    public static final TaskType EMBEDDED_SUBPROCESS_LITERAL = new TaskType(9, "EmbeddedSubProcess");
    public static final TaskType EVENT_SUBPROCESS_LITERAL    = new TaskType(10, "EventSubProcess");
    public static final TaskType DTABLE_LITERAL              = new TaskType(11, "DecisionTable");

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof TaskType)) return false;
        return type == ((TaskType) obj).type;
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

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.exception;

/**
 * Thrown when a specified processId is not found in the XPDL package.
 */
public class ProcessNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final String processId;

    public ProcessNotFoundException(String processId)
    {
        super("Process not found: " + processId);
        this.processId = processId;
    }

    public String getProcessId()
    {
        return processId;
    }
}

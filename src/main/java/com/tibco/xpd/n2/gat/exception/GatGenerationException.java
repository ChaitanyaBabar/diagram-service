/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.exception;

/**
 * Thrown when GAT model generation fails for a loaded process.
 */
public class GatGenerationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public GatGenerationException(String message)
    {
        super(message);
    }

    public GatGenerationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

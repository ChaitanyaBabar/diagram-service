/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.exception;

/**
 * Thrown when an XPDL file cannot be parsed into an EMF model.
 * This typically indicates malformed XML, missing required elements,
 * or EMF deserialization failures.
 */
public class XpdlParseException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public XpdlParseException(String message)
    {
        super(message);
    }

    public XpdlParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

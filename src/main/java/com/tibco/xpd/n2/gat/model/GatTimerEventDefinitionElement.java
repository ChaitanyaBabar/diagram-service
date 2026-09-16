/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail TimerEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatTimerEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = -981154752917133008L;

	/**
	 * Construct
	 */
	public GatTimerEventDefinitionElement(String id)
	{
		super("TimerEventDefinition", id);
	}

}

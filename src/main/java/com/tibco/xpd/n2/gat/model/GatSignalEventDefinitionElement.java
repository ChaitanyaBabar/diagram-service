/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail SignalEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatSignalEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = -8200933205432237524L;

	/**
	 * Construct
	 */
	public GatSignalEventDefinitionElement(String id)
	{
		super("SignalEventDefinition", id);
	}

}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail TerminateEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatTerminateEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = -1941521860819421276L;

	/**
	 * Construct
	 */
	public GatTerminateEventDefinitionElement(String id)
	{
		super("TerminateEventDefinition", id);
	}

}

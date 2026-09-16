/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail CancelEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatCancelEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = 4319197509864804756L;

	/**
	 * Construct
	 */
	public GatCancelEventDefinitionElement(String id)
	{
		super("CancelEventDefinition", id);
	}

}

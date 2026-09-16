/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail MessageEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatMessageEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = -6676219346062203435L;

	/**
	 * Construct
	 */
	public GatMessageEventDefinitionElement(String id)
	{
		super("MessageEventDefinition", id);
	}

}

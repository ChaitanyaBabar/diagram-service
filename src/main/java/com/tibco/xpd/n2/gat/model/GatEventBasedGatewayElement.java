/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Event-Based Exclusive Gateway model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatEventBasedGatewayElement extends GatGatewayElement
{
	private static final long serialVersionUID = -898752676856930896L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatEventBasedGatewayElement(String id, String internalName, String name)
	{
		super("EventBasedGateway", id, internalName, name);
	}

}

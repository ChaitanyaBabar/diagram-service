/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Exclusive Gateway model element
 *
 * @author Sid Allway
 * @since 23 Dec 2025
 */
@SuppressWarnings("nls")
public class GatExclusiveGatewayElement extends GatGatewayElement
{
	private static final long serialVersionUID = 5114109771611369295L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatExclusiveGatewayElement(String id, String internalName, String name)
	{
		super("ExclusiveGateway", id, internalName, name);
	}

}

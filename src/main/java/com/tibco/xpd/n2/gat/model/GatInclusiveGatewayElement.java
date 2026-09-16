/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Inclusive Gateway model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatInclusiveGatewayElement extends GatGatewayElement
{
	private static final long serialVersionUID = 4835885725181052405L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatInclusiveGatewayElement(String id, String internalName, String name)
	{
		super("InclusiveGateway", id, internalName, name);
	}

}

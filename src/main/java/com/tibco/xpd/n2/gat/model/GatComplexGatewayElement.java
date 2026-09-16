/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Complex Gateway model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatComplexGatewayElement extends GatGatewayElement
{
	private static final long serialVersionUID = -7843705167737200728L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatComplexGatewayElement(String id, String internalName, String name)
	{
		super("ComplexGateway", id, internalName, name);
	}

}

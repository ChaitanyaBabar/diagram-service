/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Parallel Gateway model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatParallelGatewayElement extends GatGatewayElement
{
	private static final long serialVersionUID = -7843705167737200728L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatParallelGatewayElement(String id, String internalName, String name)
	{
		super("ParallelGateway", id, internalName, name);
	}

}

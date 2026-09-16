/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail IntermediateThrowEvent model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatIntermediateThrowEventElement extends GatEventElement
{
	private static final long serialVersionUID = -3717481125795282472L;

	/**
	 * Construct
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatIntermediateThrowEventElement(String id, String internalName, String name)
	{
		super("IntermediateThrowEvent", id, internalName, name);
	}

}

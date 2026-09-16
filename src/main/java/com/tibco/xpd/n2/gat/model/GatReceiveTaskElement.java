/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail ReceiveTask model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatReceiveTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = -857913594375663389L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatReceiveTaskElement(String id, String internalName, String name)
	{
		super("ReceiveTask", id, internalName, name);
	}

}

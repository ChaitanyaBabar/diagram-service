/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail ServiceInvokeTask model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatServiceInvokeTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = 983564198160417740L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatServiceInvokeTaskElement(String id, String internalName, String name)
	{
		super("ServiceInvokeTask", id, internalName, name);
	}

}

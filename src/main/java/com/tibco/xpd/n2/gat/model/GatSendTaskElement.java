/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail SendTask model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatSendTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = -8080021386173427380L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatSendTaskElement(String id, String internalName, String name)
	{
		super("SendTask", id, internalName, name);
	}

}

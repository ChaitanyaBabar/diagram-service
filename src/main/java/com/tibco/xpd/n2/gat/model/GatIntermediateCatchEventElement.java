/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail IntermediateCatchEvent model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatIntermediateCatchEventElement extends GatEventElement
{
	private static final long serialVersionUID = -2625522991120947569L;

	/**
	 * Construct
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatIntermediateCatchEventElement(String id, String internalName, String name)
	{
		super("IntermediateCatchEvent", id, internalName, name);
	}

}

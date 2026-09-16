/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail DatabaseTask model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatDatabaseTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = 9106625832084645228L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatDatabaseTaskElement(String id, String internalName, String name)
	{
		super("DatabaseTask", id, internalName, name);
	}

}

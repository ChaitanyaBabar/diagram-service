/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Email Task model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatEmailTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = -2859737796744062978L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatEmailTaskElement(String id, String internalName, String name)
	{
		super("EmailTask", id, internalName, name);
	}

}

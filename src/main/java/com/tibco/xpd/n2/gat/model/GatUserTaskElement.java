/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail User Task model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatUserTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = 8520627625521213060L;

	/**
	 * Construct
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatUserTaskElement(String id, String internalName, String name)
	{
		this("UserTask", id, internalName, name);
	}

	/**
	 * Constructor for sub-classes, allowing override of type name.
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	protected GatUserTaskElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

}

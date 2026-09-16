/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Ad-Hoc User Task model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatAdHocUserTaskElement extends GatUserTaskElement
{
	private static final long serialVersionUID = -4689645961252639681L;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatAdHocUserTaskElement(String id, String internalName, String name)
	{
		super("AdHocUserTask", id, internalName, name);
	}

}

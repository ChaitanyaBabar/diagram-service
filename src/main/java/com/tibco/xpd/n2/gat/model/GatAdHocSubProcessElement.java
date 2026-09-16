/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Ad-Hoc Embedded Sub-Process model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatAdHocSubProcessElement extends GatSubProcessElement
{
	private static final long serialVersionUID = 6029214991502593493L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatAdHocSubProcessElement(String id, String internalName, String name)
	{
		super("AdHocSubProcess", id, internalName, name);
	}

}

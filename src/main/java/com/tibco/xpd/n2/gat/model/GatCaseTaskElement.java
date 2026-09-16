/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail CaseTask model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatCaseTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = 2830837946782136115L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatCaseTaskElement(String id, String internalName, String name)
	{
		super("CaseTask", id, internalName, name);
	}

}

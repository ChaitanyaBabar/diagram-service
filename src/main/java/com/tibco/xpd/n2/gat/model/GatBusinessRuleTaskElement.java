/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail BusinessRuleTask model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatBusinessRuleTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = -7347064003941604784L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatBusinessRuleTaskElement(String id, String internalName, String name)
	{
		super("BusinessRuleTask", id, internalName, name);
	}

}

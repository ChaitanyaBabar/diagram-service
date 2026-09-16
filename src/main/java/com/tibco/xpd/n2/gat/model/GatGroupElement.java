/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Group model element
 *
 * @author cbabar
 * @since Jul 20, 2026
 */
@SuppressWarnings("nls")
public class GatGroupElement extends GatNamedElement
{
	private static final long serialVersionUID = 7293018456230915847L;

	/**
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatGroupElement(String id, String internalName, String name)
	{
		super("Group", id, internalName, name);
	}

	/**
	 * Set the category value reference for this Group.
	 *
	 * @param categoryValueRef
	 */
	public void setCategoryValueRef(String categoryValueRef)
	{
		put("categoryValueRef", categoryValueRef);
	}
}

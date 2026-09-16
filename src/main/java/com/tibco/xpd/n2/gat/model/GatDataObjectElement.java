/*
* Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
*/

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail DataObject model element
 *
 * @author cbabar
 * @since Jul 15, 2026
 */
@SuppressWarnings("nls")
public class GatDataObjectElement extends GatIdElement
{
	private static final long serialVersionUID = -3530300038644184949L;

	/**
	 * @param id
	 */
	public GatDataObjectElement(String id)
	{
		super("DataObject", id);
	}

	public void setIsCollection(Boolean isCollection){
		put("isCollection", isCollection);
	}

}

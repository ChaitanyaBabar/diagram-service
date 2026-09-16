/*
* Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
*/

package com.tibco.xpd.n2.gat.model;

/**
 *
 *
 * @author cbabar
 * @since Jul 15, 2026
 */
@SuppressWarnings("nls")
public class GatDataObjectReferenceElement extends GatNamedElement
{
	private static final long serialVersionUID = -109654197126577456L;

	/**
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatDataObjectReferenceElement(String id, String internalName, String name)
	{
		super("DataObjectReference", id, internalName, name);
	}

	/**
	 * Set the DataObject id that is referenced by this DataObject reference
	 *
	 * @param dataObjectRef
	 */
	public void setDataObjectRef(String dataObjectRef)
	{
		put("dataObjectRef", dataObjectRef);
	}

}

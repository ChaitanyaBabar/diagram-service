/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Empty Task model element
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
@SuppressWarnings("nls")
public class GatEmptyTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = -1028002542910107990L;

	/**
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatEmptyTaskElement(String id, String internalName, String name)
	{
		super("EmptyTask", id, internalName, name);
	}

}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail for flow elements (activities and sequence-flows) in a process
 *
 * @author Sid Allway
 * @since 15 Dec 2025
 */
@SuppressWarnings("nls")
public abstract class GatFlowElement extends GatNamedElement
{
	private static final long serialVersionUID = -6694402627746269088L;

	/**
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatFlowElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

}

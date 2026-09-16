/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail for flow nodes in a process
 *
 * @author Sid Allway
 * @since 15 Dec 2025
 */
@SuppressWarnings("nls")
public abstract class GatFlowNodeElement extends GatFlowElement
{
	private static final long serialVersionUID = -6694402627746269088L;

	/**
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatFlowNodeElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

	/**
	 * Set the outgoing default sequence flow id.
	 *
	 * @param sequenceFlowId
	 */
	public void setDefault(String sequenceFlowId)
	{
		put("default", sequenceFlowId);
	}

}

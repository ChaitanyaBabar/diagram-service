/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail base Gateway element
 *
 * @author Sid Allway
 * @since 23 Dec 2025
 */
@SuppressWarnings("nls")
abstract class GatGatewayElement extends GatFlowNodeElement
{
	private static final long serialVersionUID = 55927385403308568L;

	/**
	 * Constructor
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatGatewayElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

	/**
	 * Set the default sequence flow
	 *
	 * @param defaultFlowId
	 */
	@Override
	public void setDefault(String defaultFlowId)
	{
		put("default", defaultFlowId);
	}

}

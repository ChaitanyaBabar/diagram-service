/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Base class for all Graphical Audit Trail Event Definitions
 *
 * @author Sid Allway
 * @since 19 Jan 2026
 */
public abstract class GatEventDefinitionElement extends GatIdElement
{
	private static final long serialVersionUID = 6086514460857570872L;

	/**
	 * @param typeName
	 * @param id TODO
	 */
	protected GatEventDefinitionElement(String typeName, String id)
	{
		super(typeName, id);
	}

}

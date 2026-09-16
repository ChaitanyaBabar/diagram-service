/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail LinkEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatLinkEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = 1080464180303613446L;

	/**
	 * Construct
	 */
	public GatLinkEventDefinitionElement(String id)
	{
		super("LinkEventDefinition", id);
	}

	/**
	 * Set the name property.
	 *
	 * @param name
	 */
	public void setName(String name) {
		put("name", name);
	}

}

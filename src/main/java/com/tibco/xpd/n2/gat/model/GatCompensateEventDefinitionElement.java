/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail CompensateEventDefinition model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatCompensateEventDefinitionElement extends GatEventDefinitionElement
{
	private static final long serialVersionUID = 3654935701042279084L;

	/**
	 * Construct
	 */
	public GatCompensateEventDefinitionElement(String id)
	{
		super("CompensateEventDefinition", id);
	}

}

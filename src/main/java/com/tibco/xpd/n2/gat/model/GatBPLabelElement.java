/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.n2.gat.model.diagram.GatDiagramElement;

/**
 * Graphical Audit Trail BPLabel element
 *
 * @author Sid Allway
 * @since 24 Dec 2025
 */
@SuppressWarnings("nls")
public class GatBPLabelElement extends GatDiagramElement
{

	/**
	 * Constructor
	 *
	 * @param typeName
	 * @param modelElement
	 */
	public GatBPLabelElement(GatIdElement modelElement)
	{
		super("BPShape", modelElement);
	}

	/**
	 * Set the Bounds of the label
	 *
	 * @param boundsElement
	 */
	public void setBounds(GatBoundsElement boundsElement) {
		put("bounds", boundsElement);
	}

	/**
	 * Get the label bounds
	 *
	 * @return {@link GatBoundsElement}
	 */
	public GatBoundsElement getBounds()
	{
		return (GatBoundsElement) get("bounds");
	}

}

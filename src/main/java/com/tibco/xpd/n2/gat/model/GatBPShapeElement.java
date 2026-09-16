/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.n2.gat.model.diagram.GatDiagramElement;

/**
 * Graphical Audit Trail BPShape element
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
@SuppressWarnings("nls")
public class GatBPShapeElement extends GatDiagramElement
{
	private static final long serialVersionUID = -2922651113369579924L;

	/**
	 * @param typeName
	 * @param modelElement
	 */
	public GatBPShapeElement(GatIdElement modelElement)
	{
		super("BPShape", modelElement);
	}

	/**
	 * Set the Bounds in the shape
	 *
	 * @param boundsElement
	 */
	public void setBounds(GatBoundsElement boundsElement) {
		put("bounds", boundsElement);
	}

	/**
	 * Get the shape bounds
	 *
	 * @return {@link GatBoundsElement}
	 */
	public GatBoundsElement getBounds()
	{
		return (GatBoundsElement) get("bounds");
	}

	/**
	 * Set the isExpanded property
	 *
	 * @param isExpanded
	 */
	public void setIsExpanded(Boolean isExpanded)
	{
		put("isExpanded", isExpanded);
	}

	/**
	 * Set the isExpanded property
	 *
	 * @return {@link Boolean}
	 */
	public Boolean getIsExpanded()
	{
		return (Boolean) get("isExpanded");
	}

}

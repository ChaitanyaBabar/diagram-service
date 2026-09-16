/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tibco.xpd.n2.gat.model.GatBPLabelElement;
import com.tibco.xpd.n2.gat.model.GatIdElement;

/**
 * Graphical Audit Trail BPEdge diagram model element
 *
 * @author Sid Allway
 * @since 24 Dec 2025
 */
@SuppressWarnings("nls")
public class GatBPEdgeElement extends GatDiagramElement
{
	private static final long serialVersionUID = 7770347778758941600L;

	private List<GatPointElement>	wayPoint			= new ArrayList<>();

	/**
	 * Constructor
	 *
	 * @param modelElement
	 */
	public GatBPEdgeElement(GatIdElement modelElement)
	{
		super("BPEdge", modelElement);

		put("waypoint", wayPoint);
	}

	/**
	 * Set the label
	 *
	 * @param label
	 */
	public void setLabel(GatBPLabelElement label)
	{
		put("label", label);
	}

	/**
	 * Get the label
	 *
	 * @return {@link GatBPLabelElement}
	 */
	public GatBPLabelElement getLabel()
	{
		return (GatBPLabelElement) get("label");
	}

	/**
	 * Add a point to waypoint list
	 *
	 * @param point
	 */
	public void addWayPoint(GatPointElement point)
	{
		wayPoint.add(point);
	}

	/**
	 * Get waypoint list
	 *
	 * @return List of flow point coordinates
	 */
	public Collection<GatPointElement> getWaypoint()
	{
		return wayPoint;
	}

}

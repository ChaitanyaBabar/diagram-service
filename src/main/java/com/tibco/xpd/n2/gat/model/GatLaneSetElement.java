/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphical Audit Trail laneSet element.
 *
 * @author Sid Allway
 * @since 23 Mar 2026
 */
@SuppressWarnings("nls")
public class GatLaneSetElement extends GatIdElement
{
	private static final long		serialVersionUID	= -8659553170268378865L;

	/**
	 * The lanes property
	 */
	private List<GatLaneElement>	lanes;

	/**
	 * @param id
	 */
	public GatLaneSetElement(String id)
	{
		super("LaneSet", id);

		lanes = new ArrayList<GatLaneElement>();
		put("lanes", lanes);
	}

	/**
	 * Add a lane element to the GAT laneSet
	 *
	 * @param laneElement
	 */
	public void addLaneElement(GatLaneElement laneElement)
	{
		lanes.add(laneElement);
	}
}

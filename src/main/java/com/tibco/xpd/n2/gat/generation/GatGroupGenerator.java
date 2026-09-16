/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatGroupElement;
import com.tibco.xpd.n2.gat.model.GatIdElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.Category;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.Group;
import com.tibco.xpd.xpdl2.Lane;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Pool;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail generator for Group artifacts.
 *
 * @author cbabar
 * @since Jul 20, 2026
 */
@SuppressWarnings("nls")
public class GatGroupGenerator extends GatArtifactGenerator
{
	/**
	 * @param parent
	 * @param xpdlArtifact
	 */
	public GatGroupGenerator(BaseGatGenerator parent, Artifact xpdlArtifact)
	{
		super(parent, xpdlArtifact);
	}

	/**
	 * Generate the Group artifact element.
	 *
	 * @return {@link GatGroupElement}
	 */
	@Override
	public GatGroupElement generate()
	{
		Artifact xpdlArtifact = getXpdlArtifact();

		GatGroupElement groupElement = new GatGroupElement(xpdlArtifact.getId(), xpdlArtifact.getName(),
				GatModelUtil.getDisplayNameOrName(xpdlArtifact));

		Group group = xpdlArtifact.getGroup();

		if (group != null)
		{
			Category category = group.getCategory();

			if (category != null && category.getName() != null && !category.getName().isEmpty())
			{
				groupElement.setCategoryValueRef(category.getName());
			}
		}

		return groupElement;
	}

	/**
	 * Group coordinates in XPDL are already stored as top-left (not center-point like other artifacts), so we skip the
	 * center-to-top-left conversion and only apply the pool/lane offset.
	 */
	@Override
	protected GatBPShapeElement generateShape(GatIdElement modelElement)
	{
		GatBPShapeElement shapeElement = new GatBPShapeElement(modelElement);

		NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(getXpdlArtifact());
		Coordinates coords = ngi.getCoordinates();

		double adjustedX = coords.getXCoordinate();
		double adjustedY = coords.getYCoordinate();

		Lane lane = GatModelUtil.getLane(getXpdlProcess(), ngi.getLaneId());

		if (lane != null)
		{
			adjustedX += POOL_HEADER_WIDTH + LANE_HEADER_WIDTH + 2;

			Pool pool = lane.getParentPool();

			for (Lane poolLane : pool.getLanes())
			{
				if (poolLane.equals(lane))
				{
					break;
				}

				NodeGraphicsInfo laneNgi = GatModelUtil.getNodeGraphicsInfo(poolLane);
				double laneHeight = laneNgi.getHeight() + LANE_CONTENT_MARGIN + 1;

				adjustedY += laneHeight;
			}
		}

		shapeElement.setBounds(new GatBoundsElement(adjustedX, adjustedY, getEffectiveWidth(ngi), getEffectiveHeight(ngi)));

		return shapeElement;
	}
}

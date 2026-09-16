/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.Lane;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Pool;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.ViewType;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Base Graphical Audit Trail generator for Flow Elements
 *
 * Provides common flow element capabilities, such as translating from XPDL coordinate system to GAT model coordinate
 * system.
 *
 * @author Sid Allway
 * @since Jan 2026
 */
public abstract class GatFlowElementGenerator extends BaseGatGenerator
{
	/**
	 * @param parent
	 */
	public GatFlowElementGenerator(BaseGatGenerator parent)
	{
		super(parent);
	}

	/**
	 * Get the flow container of the XPDL flow element
	 *
	 * @return {@link FlowContainer}
	 */
	protected abstract FlowContainer getXpdlFlowContainer();

	/**
	 * Updates the given bounds from XPDL to Graphical Audit Trail coordinate system
	 *
	 * @param elementCoords
	 * @param laneAncestor
	 *            Lane ancestor or null if point is in a process without pools/lanes
	 *
	 * @return Updated coords
	 */
	public DoublePoint adjustToGatCoordSystem(DoublePoint elementCoords, Lane laneAncestor)
	{
		DoublePoint adjustedCoords = elementCoords;

		/*
		 * The GAT coordinate system is as follows...
		 *
		 * Coordinates are absolute within the 'Host Diagram' where the host diagram the nearest ancestor that is EITHER
		 * the diagram for top-level process OR the diagram for a collapsed embedded sub-process
		 *
		 * Whereas in XPDL, coordinates are offset relative to parent lane (or process if no lanes) or embedded
		 * sub-process.
		 *
		 * So we need to recurs back thru the parent flow container tree, adding the parent's top-left until we get to a
		 * parent process OR collapsed sub-process.
		 */
		boolean hasCollapsedSubProcAncestor = false;

		FlowContainer flowContainer = getXpdlFlowContainer();

		while (!(flowContainer instanceof Process)) // Quit when we get to top-level process
		{
			/* Must be the activity set for an embedded sub-process */
			ActivitySet activitySet = (ActivitySet) flowContainer;

			Activity parentEmbeddedSubProcessActivity = GatModelUtil.getEmbSubProcActivityForActSet(getXpdlProcess(),
					activitySet.getId());

			/*
			 * If our parent is a collapsed sub-process, then we want coords in GAT to be relative to the parent, which
			 * they already are in XPDL - so we can just quit now.
			 */
			if (ViewType.COLLAPSED.equals(parentEmbeddedSubProcessActivity.getBlockActivity().getView()))
			{
				hasCollapsedSubProcAncestor = true;

				/*
				 * Offset allowing for embedded sub-process border size (in XPDL, the coords are relative to the inner
				 * content rectangle)
				 */
				adjustedCoords = new DoublePoint(
						// Allow for emb sub-proc left border
						adjustedCoords.getX() + EMBEDDED_SUBPROCESS_BORDER_WIDTH,
						// Allow for emb sub-proc top border
						adjustedCoords.getY() + EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT);
				break;
			}

			/*
			 * Otherwise, parent is expanded, so in GAT we want our activity's coords to be relative to its parent (and
			 * so on), so add our parent's top left coords to ours (remembering that in XPDL coords x, y are center of
			 * object).
			 */
			NodeGraphicsInfo parentNgi = GatModelUtil.getNodeGraphicsInfo(parentEmbeddedSubProcessActivity);
			Coordinates parentCoords = parentNgi.getCoordinates();

			double parentTopLeftX = parentCoords.getXCoordinate() - (parentNgi.getWidth() / 2);
			double parentTopLeftY = parentCoords.getYCoordinate() - (parentNgi.getHeight() / 2);

			/*
			 * Offset provided bounds relative to this parent sub-process, also allowing for embedded sub-process border
			 * size (in XPDL, the coords are relative to the inner content rectangle)
			 */
			adjustedCoords = new DoublePoint(
					// Allow for emb sub-proc left border
					adjustedCoords.getX() + parentTopLeftX + EMBEDDED_SUBPROCESS_BORDER_WIDTH,
					// Allow for emb sub-proc top border
					adjustedCoords.getY() + parentTopLeftY + EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT);

			/* Recurs to parent of this embedded sub-process activity */
			flowContainer = parentEmbeddedSubProcessActivity.getFlowContainer();
		}

		/*
		 * For activities that are not somewhere under a collapsed sub-process, we also need to allow for their Pool and lane
		 * offsets
		 */
		if (!hasCollapsedSubProcAncestor)
		{
			adjustedCoords = adjustCoordsForPoolAndLane(adjustedCoords, laneAncestor);
		}

		return adjustedCoords;
	}

	/**
	 * After the the super class has adjusted the coordinates to absolute within XPDL lane (if there is one) then adjust
	 * into absolute coords by offsetting for the parent lane & pool (if necessary)
	 *
	 * @param adjustedCoords
	 * @param laneAncestor
	 *            Lane ancestor or null if point is in a process without pools/lanes
	 *
	 * @return {@link DoublePoint} The new location if there is one ELSE the original coords passed
	 */
	private DoublePoint adjustCoordsForPoolAndLane(DoublePoint adjustedCoords, Lane laneAncestor)
	{
		if (laneAncestor == null)
		{
			return adjustedCoords;
		}

		double xOffset = POOL_HEADER_WIDTH + LANE_HEADER_WIDTH + 2;
		double yOffset = 0;

		Pool pool = laneAncestor.getParentPool();

		for (Lane lane : pool.getLanes())
		{
			if (lane.equals(laneAncestor))
			{
				break;
			}

			NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(lane);
			double laneHeight = ngi.getHeight(); // lane height without borders

			// allow for content margin and 1-pixel borders
			laneHeight += LANE_CONTENT_MARGIN + 1;

			yOffset += laneHeight;
		}

		return new DoublePoint(xOffset + adjustedCoords.getX(), yOffset + adjustedCoords.getY());
	}

	/**
	 * Little data class for holding double precision point coordinates
	 */
	protected class DoublePoint
	{
		private double	x;

		private double	y;

		/**
		 * @param x
		 * @param y
		 */
		public DoublePoint(double x, double y)
		{
			super();
			this.x = x;
			this.y = y;
		}

		/**
		 * @return the x
		 */
		public double getX()
		{
			return x;
		}

		/**
		 * @param x
		 *            the x to set
		 */
		public void setX(double x)
		{
			this.x = x;
		}

		/**
		 * @return the y
		 */
		public double getY()
		{
			return y;
		}

		/**
		 * @param y
		 *            the y to set
		 */
		public void setY(double y)
		{
			this.y = y;
		}

		/**
		 * @see java.lang.Object#toString()
		 *
		 * @return
		 */
		@Override
		public String toString()
		{
			return String.format("X=%f, Y=%f", x, y);
		}
	}

}

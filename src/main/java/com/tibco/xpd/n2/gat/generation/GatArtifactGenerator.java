/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatIdElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.Lane;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Pool;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.ViewType;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Abstract base generator for XPDL Artifact elements that produce BPShape diagram elements (DataObject, TextAnnotation,
 * Group). Provides the common center-to-top-left coordinate transform and pool/lane offset calculation.
 *
 * <p>
 * Edge-based artifacts like Association do NOT extend this class - they extend {@link BaseGatGenerator} directly.
 * </p>
 *
 * @author cbabar
 * @since Jul 17, 2026
 */
public abstract class GatArtifactGenerator extends BaseGatGenerator
{
	private Artifact xpdlArtifact;

	/**
	 * @param parent
	 * @param xpdlArtifact
	 */
	public GatArtifactGenerator(BaseGatGenerator parent, Artifact xpdlArtifact)
	{
		super(parent);
		this.xpdlArtifact = xpdlArtifact;
	}

	/**
	 * @return The XPDL Artifact being generated.
	 */
	protected Artifact getXpdlArtifact()
	{
		return xpdlArtifact;
	}

	/**
	 * Get the effective width for the artifact shape. Subclasses may override to provide fallback values when XPDL
	 * stores zero width (e.g. for elements never resized in Studio).
	 *
	 * @param ngi
	 * @return The effective width
	 */
	protected double getEffectiveWidth(NodeGraphicsInfo ngi)
	{
		return ngi.getWidth();
	}

	/**
	 * Get the effective height for the artifact shape. Subclasses may override to provide fallback values when XPDL
	 * stores zero height.
	 *
	 * @param ngi
	 * @return The effective height
	 */
	protected double getEffectiveHeight(NodeGraphicsInfo ngi)
	{
		return ngi.getHeight();
	}

	/**
	 * Generate the BPShape diagram element for the artifact, applying center-to-top-left coordinate transform and
	 * adjusting to GAT coordinate system (sub-process ancestry walk + pool/lane offsets).
	 *
	 * @param modelElement
	 *            The semantic model element this shape represents
	 * @return {@link GatBPShapeElement}
	 */
	protected GatBPShapeElement generateShape(GatIdElement modelElement)
	{
		GatBPShapeElement shapeElement = new GatBPShapeElement(modelElement);

		NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(xpdlArtifact);
		Coordinates coords = ngi.getCoordinates();

		double shapeWidth = getEffectiveWidth(ngi);
		double shapeHeight = getEffectiveHeight(ngi);

		double topLeftX = coords.getXCoordinate() - (shapeWidth / 2);
		double topLeftY = coords.getYCoordinate() - (shapeHeight / 2);

		DoublePoint gatCoords = adjustToGatCoordSystem(topLeftX, topLeftY, ngi.getLaneId());

		shapeElement.setBounds(new GatBoundsElement(gatCoords.getX(), gatCoords.getY(), shapeWidth, shapeHeight));

		return shapeElement;
	}

	/**
	 * Adjusts artifact coordinates from XPDL coordinate system to GAT coordinate system.
	 *
	 * <p>
	 * For artifacts inside an expanded sub-process, walks up through the sub-process ancestry accumulating parent
	 * sub-process offsets and border margins (mirroring {@code GatFlowElementGenerator.adjustToGatCoordSystem()} for
	 * flow nodes). For artifacts at the top-level process, performs the standard pool/lane offset.
	 * </p>
	 *
	 * <p>
	 * In XPDL, artifacts inside a sub-process have their {@code laneId} set to the {@link ActivitySet} ID (not a real
	 * Lane ID). This method detects that case and resolves the real lane from the outermost parent sub-process activity.
	 * </p>
	 *
	 * @param x
	 *            top-left X coordinate
	 * @param y
	 *            top-left Y coordinate
	 * @param laneId
	 *            the artifact's laneId from NodeGraphicsInfo
	 * @return adjusted coordinates as {@link DoublePoint}
	 */
	public DoublePoint adjustToGatCoordSystem(double x, double y, String laneId)
	{
		boolean hasCollapsedSubProcAncestor = false;
		Lane resolvedLane = null;

		/*
		 * Check if laneId matches an ActivitySet ID (artifact is inside a sub-process) rather than a real Lane ID.
		 */
		ActivitySet activitySet = null;

		for (ActivitySet actSet : getXpdlProcess().getActivitySets())
		{
			if (actSet.getId().equals(laneId))
			{
				activitySet = actSet;
				break;
			}
		}

		if (activitySet != null)
		{
			/*
			 * Artifact is inside a sub-process. Walk up through ancestry accumulating parent sub-process top-left
			 * offsets and border margins until we reach the top-level process or a collapsed sub-process.
			 */
			FlowContainer flowContainer = activitySet;
			Activity outermostParent = null;

			while (!(flowContainer instanceof Process))
			{
				ActivitySet actSet = (ActivitySet) flowContainer;

				Activity parentSubProc = GatModelUtil.getEmbSubProcActivityForActSet(getXpdlProcess(),
						actSet.getId());

				outermostParent = parentSubProc;

				if (ViewType.COLLAPSED.equals(parentSubProc.getBlockActivity().getView()))
				{
					hasCollapsedSubProcAncestor = true;

					x += EMBEDDED_SUBPROCESS_BORDER_WIDTH;
					y += EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT;
					break;
				}

				NodeGraphicsInfo parentNgi = GatModelUtil.getNodeGraphicsInfo(parentSubProc);
				Coordinates parentCoords = parentNgi.getCoordinates();

				double parentTopLeftX = parentCoords.getXCoordinate() - (parentNgi.getWidth() / 2);
				double parentTopLeftY = parentCoords.getYCoordinate() - (parentNgi.getHeight() / 2);

				x += parentTopLeftX + EMBEDDED_SUBPROCESS_BORDER_WIDTH;
				y += parentTopLeftY + EMBEDDED_SUBPROCESS_TOP_BORDER_HEIGHT;

				flowContainer = parentSubProc.getFlowContainer();
			}

			if (!hasCollapsedSubProcAncestor && outermostParent != null)
			{
				resolvedLane = GatModelUtil.getLaneAncestor(outermostParent);
			}
		}
		else
		{
			resolvedLane = GatModelUtil.getLane(getXpdlProcess(), laneId);
		}

		if (!hasCollapsedSubProcAncestor)
		{
			return adjustCoordsForPoolAndLane(x, y, resolvedLane);
		}

		return new DoublePoint(x, y);
	}

	/**
	 * Adjusts coordinates to account for Pool header and Lane header/height offsets.
	 *
	 * @param x
	 *            current adjusted X coordinate
	 * @param y
	 *            current adjusted Y coordinate
	 * @param lane
	 *            the resolved Lane ancestor, or {@code null} if the process has no pools/lanes
	 * @return adjusted coordinates as {@link DoublePoint}
	 */
	private DoublePoint adjustCoordsForPoolAndLane(double x, double y, Lane lane)
	{
		if (lane == null)
		{
			return new DoublePoint(x, y);
		}

		x += POOL_HEADER_WIDTH + LANE_HEADER_WIDTH + 2;

		Pool pool = lane.getParentPool();

		for (Lane poolLane : pool.getLanes())
		{
			if (poolLane.equals(lane))
			{
				break;
			}

			NodeGraphicsInfo laneNgi = GatModelUtil.getNodeGraphicsInfo(poolLane);
			double laneHeight = laneNgi.getHeight() + LANE_CONTENT_MARGIN + 1;

			y += laneHeight;
		}

		return new DoublePoint(x, y);
	}

	/**
	 * Data class for holding double precision point coordinates.
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
		 * @return the y
		 */
		public double getY()
		{
			return y;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return String.format("X=%f, Y=%f", x, y);
		}
	}
}

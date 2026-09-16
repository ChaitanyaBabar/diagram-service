/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatIdElement;
import com.tibco.xpd.n2.gat.model.GatTextAnnotationElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail generator for TextAnnotation artifacts.
 *
 * @author cbabar
 * @since Jul 16, 2026
 */
@SuppressWarnings("nls")
public class GatTextAnnotationGenerator extends GatArtifactGenerator
{
	private static final double	DEFAULT_CHAR_WIDTH	= 7.0;

	private static final double	DEFAULT_HEIGHT		= 20.0;

	private static final double	MIN_WIDTH			= 50.0;

	/**
	 * @param parent
	 * @param xpdlArtifact
	 */
	public GatTextAnnotationGenerator(BaseGatGenerator parent, Artifact xpdlArtifact)
	{
		super(parent, xpdlArtifact);
	}

	/**
	 * Generate the TextAnnotation artifact element.
	 *
	 * @return {@link GatTextAnnotationElement}
	 */
	@Override
	public GatTextAnnotationElement generate()
	{
		Artifact xpdlArtifact = getXpdlArtifact();

		return new GatTextAnnotationElement(xpdlArtifact.getId(), xpdlArtifact.getTextAnnotation());
	}

	/**
	 * Overrides the base {@link GatArtifactGenerator#generateShape(GatIdElement)} because TextAnnotation XPDL coordinates
	 * use a different convention to other artifacts (DataObject, Group) and activities.
	 * <p>
	 * In the diagram editor, {@code NoteEditPart.refreshVisuals()} positions TextAnnotations by only adjusting Y
	 * ({@code loc.y -= size.height / 2}) while leaving X unchanged. In contrast, {@code TaskEditPart.refreshVisuals()}
	 * adjusts both axes ({@code loc.x -= width/2; loc.y -= height/2}). This means TextAnnotation XPDL coordinates store
	 * X as top-left and Y as vertical midpoint, whereas activities and other artifacts store center for both axes.
	 * <p>
	 * The base class subtracts both {@code width/2} and {@code height/2}, which is wrong for TextAnnotation X. This
	 * override skips the {@code -width/2} for X (already top-left) but keeps the {@code -height/2} for Y (midpoint to
	 * top-left conversion).
	 */
	@Override
	protected GatBPShapeElement generateShape(GatIdElement modelElement)
	{
		GatBPShapeElement shapeElement = new GatBPShapeElement(modelElement);

		NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(getXpdlArtifact());
		Coordinates coords = ngi.getCoordinates();

		double shapeWidth = getEffectiveWidth(ngi);
		double shapeHeight = getEffectiveHeight(ngi);

		DoublePoint gatCoords = adjustToGatCoordSystem(coords.getXCoordinate(),
				coords.getYCoordinate() - (shapeHeight / 2), ngi.getLaneId());

		shapeElement.setBounds(new GatBoundsElement(gatCoords.getX(), gatCoords.getY(), shapeWidth, shapeHeight));

		return shapeElement;
	}

	/**
	 * XPDL may store Width as 0 for annotations that were never resized in Studio. In that case, estimate from the
	 * annotation text length.
	 */
	@Override
	protected double getEffectiveWidth(NodeGraphicsInfo ngi)
	{
		if (ngi.getWidth() > 0)
		{
			return ngi.getWidth();
		}

		String text = getXpdlArtifact().getTextAnnotation();

		if (text != null && !text.isEmpty())
		{
			return Math.max(MIN_WIDTH, text.length() * DEFAULT_CHAR_WIDTH);
		}

		return MIN_WIDTH;
	}

	/**
	 * XPDL may store Height as 0 for annotations that were never resized in Studio. In that case, use a default height.
	 */
	@Override
	protected double getEffectiveHeight(NodeGraphicsInfo ngi)
	{
		if (ngi.getHeight() > 0)
		{
			return ngi.getHeight();
		}

		return DEFAULT_HEIGHT;
	}
}

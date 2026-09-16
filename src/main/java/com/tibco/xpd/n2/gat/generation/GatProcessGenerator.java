/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.tibco.xpd.n2.gat.model.GatAssociationElement;
import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatCollaborationElement;
import com.tibco.xpd.n2.gat.model.GatDataObjectElement;
import com.tibco.xpd.n2.gat.model.GatDataObjectReferenceElement;
import com.tibco.xpd.n2.gat.model.GatDefinitionsElement;
import com.tibco.xpd.n2.gat.model.GatGroupElement;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.model.GatLaneElement;
import com.tibco.xpd.n2.gat.model.GatLaneSetElement;
import com.tibco.xpd.n2.gat.model.GatParticipantElement;
import com.tibco.xpd.n2.gat.model.GatProcessElement;
import com.tibco.xpd.n2.gat.model.GatSequenceFlowElement;
import com.tibco.xpd.n2.gat.model.GatTextAnnotationElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPDiagramElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPEdgeElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBoundsElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.ArtifactType;
import com.tibco.xpd.xpdl2.Association;
import com.tibco.xpd.xpdl2.Coordinates;
import com.tibco.xpd.xpdl2.Lane;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Pool;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.Transition;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail generator for the Definitions model element.
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
@SuppressWarnings("nls")
public class GatProcessGenerator extends BaseGatGenerator
{
	private Process					xpdlProcess;
	private GatDefinitionsElement	definitionsElement;
	private GatProcessElement		processElement;
	private GatBPDiagramElement		diagramElement;
	private double					xpdlProcessLanesWidth;

	public GatProcessGenerator(Process xpdlProcess)
	{
		super(null);
		this.xpdlProcess = xpdlProcess;
	}

	@Override
	public GatDefinitionsElement generate()
	{
		definitionsElement = new GatDefinitionsElement(xpdlProcess.getId() + "_gat");

		GatCollaborationGenerator collaborationGenerator = new GatCollaborationGenerator(this, xpdlProcess);
		GatCollaborationElement collaborationElement = collaborationGenerator.generate();
		definitionsElement.setCollaboration(collaborationElement);

		processElement = new GatProcessElement(xpdlProcess.getId());
		definitionsElement.setProcess(processElement);

		Pool pool = getXpdlProcessPool();

		diagramElement = new GatBPDiagramElement(pool != null ? collaborationElement : processElement);
		definitionsElement.addDiagram(diagramElement);

		if (pool != null)
		{
			GatBPShapeElement poolShape = generatePoolShape(pool, collaborationGenerator.getParticipant());
			diagramElement.addDiagramElement(poolShape);
		}

		generateLanes();
		generateActivities();
		generateDataObjects();
		generateAnnotations();
		generateGroups();
		generateSequenceFlows();
		generateAssociations();

		return definitionsElement;
	}

	private GatBPShapeElement generatePoolShape(Pool xpdlPool, GatParticipantElement participant)
	{
		GatBPShapeElement shapeElement = new GatBPShapeElement(participant);

		xpdlProcessLanesWidth = MINIMUM_LANE_WIDTH;

		for (Activity xpdlActivity : xpdlProcess.getActivities())
		{
			NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(xpdlActivity);
			Coordinates coords = ngi.getCoordinates();
			double rhs = coords.getXCoordinate() + (ngi.getWidth() / 2);

			if ((rhs + LANE_RHS_OBJECT_MARGIN_X) > xpdlProcessLanesWidth)
			{
				xpdlProcessLanesWidth = rhs + LANE_RHS_OBJECT_MARGIN_X;
			}
		}

		for (Artifact xpdlArtifact : GatModelUtil.getAllArtifactsInProcess(xpdlProcess))
		{
			NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(xpdlArtifact);
			Coordinates coords = ngi.getCoordinates();
			double rhs;

			if (ArtifactType.ANNOTATION_LITERAL == xpdlArtifact.getArtifactType())
			{
				double width = ngi.getWidth();

				if (width <= 0)
				{
					String text = xpdlArtifact.getTextAnnotation();
					width = (text != null && !text.isEmpty()) ? Math.max(50.0, text.length() * 7.0) : 50.0;
				}

				rhs = coords.getXCoordinate() + width;
			}
			else
			{
				rhs = coords.getXCoordinate() + (ngi.getWidth() / 2);
			}

			if ((rhs + LANE_RHS_OBJECT_MARGIN_X) > xpdlProcessLanesWidth)
			{
				xpdlProcessLanesWidth = rhs + LANE_RHS_OBJECT_MARGIN_X;
			}
		}

		xpdlProcessLanesWidth += (LANE_CONTENT_MARGIN * 2);
		xpdlProcessLanesWidth += LANE_HEADER_WIDTH;
		double poolWidth = xpdlProcessLanesWidth + POOL_HEADER_WIDTH;

		double lanesHeight = 0;

		for (Lane xpdlLane : xpdlPool.getLanes())
		{
			NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(xpdlLane);
			lanesHeight += ngi.getHeight();
		}

		int numBorders = xpdlPool.getLanes().size() + 1;
		lanesHeight += (LANE_CONTENT_MARGIN * numBorders);

		shapeElement.setBounds(new GatBoundsElement(0.0, 0.0, poolWidth, lanesHeight));

		return shapeElement;
	}

	private void generateLanes()
	{
		Pool xpdlPool = getXpdlProcessPool();

		if (xpdlPool != null)
		{
			GatLaneSetElement laneSetElement = new GatLaneSetElement(xpdlPool.getId() + "_laneSet");
			processElement.addLaneSetElement(laneSetElement);

			double laneYOffset = 0;

			EList<Lane> xpdlLanes = xpdlPool.getLanes();

			for (Lane xpdlLane : xpdlLanes)
			{
				GatLaneElement laneElement = new GatLaneElement(xpdlLane.getId(), xpdlLane.getName(),
						GatModelUtil.getDisplayNameOrName(xpdlLane));
				laneSetElement.addLaneElement(laneElement);

				List<Activity> activitiesInLane = GatModelUtil.getActivitiesInLane(xpdlLane);

				for (Activity activity : activitiesInLane)
				{
					laneElement.addFlowNodeRef(activity.getId());
				}

				NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(xpdlLane);
				double laneHeight = ngi.getHeight();
				laneHeight += LANE_CONTENT_MARGIN + 1;

				GatBPShapeElement shapeElement = new GatBPShapeElement(laneElement);
				diagramElement.addDiagramElement(shapeElement);

				GatBoundsElement laneBounds = new GatBoundsElement(
						(double) POOL_HEADER_WIDTH - 1, laneYOffset,
						xpdlProcessLanesWidth,
						laneHeight);
				shapeElement.setBounds(laneBounds);

				laneYOffset += laneHeight;
			}
		}
	}

	private void generateActivities()
	{
		for (Activity xpdlActivity : xpdlProcess.getActivities())
		{
			GatFlowNodeGenerator activityGenerator = GatFlowNodeGeneratorFactory.createGenerator(this, xpdlActivity);

			GatFlowNodeElement gatActivity = activityGenerator.generate();
			processElement.addFlowElement(gatActivity);

			GatBPShapeElement shapeElement = activityGenerator.generateShape(gatActivity);
			getGatDiagramElement().addDiagramElement(shapeElement);
		}
	}

	private void generateDataObjects()
	{
		for (Artifact xpdlArtifact : GatModelUtil.getAllArtifactsInProcess(xpdlProcess))
		{
			if (ArtifactType.DATA_OBJECT_LITERAL == xpdlArtifact.getArtifactType())
			{
				if (isArtifactInSubProcess(xpdlArtifact))
				{
					continue;
				}

				GatDataObjectReferenceGenerator generator = new GatDataObjectReferenceGenerator(this, xpdlArtifact);

				GatDataObjectReferenceElement referenceElement = generator.generate();
				processElement.addFlowElement(referenceElement);

				GatDataObjectElement dataObjectElement = generator.generateDataObject();
				processElement.addFlowElement(dataObjectElement);

				GatBPShapeElement shapeElement = generator.generateShape(referenceElement);
				getGatDiagramElement().addDiagramElement(shapeElement);
			}
		}
	}

	private void generateSequenceFlows()
	{
		for (Transition xpdlSequenceFlow : xpdlProcess.getTransitions())
		{
			GatSequenceFlowGenerator sequenceFlowGenerator = new GatSequenceFlowGenerator(this, xpdlSequenceFlow);

			GatSequenceFlowElement sequenceFlowElement = sequenceFlowGenerator.generate();
			processElement.addFlowElement(sequenceFlowElement);

			GatBPEdgeElement bpEdgeElement = sequenceFlowGenerator.generateBPEdge(sequenceFlowElement);
			getGatDiagramElement().addDiagramElement(bpEdgeElement);
		}
	}

	private void generateAnnotations()
	{
		for (Artifact xpdlArtifact : GatModelUtil.getAllArtifactsInProcess(xpdlProcess))
		{
			if (ArtifactType.ANNOTATION_LITERAL == xpdlArtifact.getArtifactType())
			{
				if (isArtifactInSubProcess(xpdlArtifact))
				{
					continue;
				}

				GatTextAnnotationGenerator generator = new GatTextAnnotationGenerator(this, xpdlArtifact);

				GatTextAnnotationElement annotationElement = generator.generate();
				processElement.addArtifact(annotationElement);

				GatBPShapeElement shapeElement = generator.generateShape(annotationElement);
				getGatDiagramElement().addDiagramElement(shapeElement);
			}
		}
	}

	private void generateGroups()
	{
		for (Artifact xpdlArtifact : GatModelUtil.getAllArtifactsInProcess(xpdlProcess))
		{
			if (ArtifactType.GROUP_LITERAL == xpdlArtifact.getArtifactType())
			{
				GatGroupGenerator generator = new GatGroupGenerator(this, xpdlArtifact);

				GatGroupElement groupElement = generator.generate();
				processElement.addArtifact(groupElement);

				GatBPShapeElement shapeElement = generator.generateShape(groupElement);
				getGatDiagramElement().addDiagramElement(shapeElement);
			}
		}
	}

	private void generateAssociations()
	{
		for (Association xpdlAssociation : GatModelUtil.getAllAssociationsInProc(xpdlProcess))
		{
			if (isAssociationInSubProcess(xpdlAssociation))
			{
				continue;
			}

			GatAssociationGenerator generator = new GatAssociationGenerator(this, xpdlAssociation, null);

			GatAssociationElement associationElement = generator.generate();
			processElement.addArtifact(associationElement);

			GatBPEdgeElement edgeElement = generator.generateBPEdge(associationElement);
			getGatDiagramElement().addDiagramElement(edgeElement);
		}
	}

	@Override
	public GatDefinitionsElement getGatDefinitionsElement()
	{
		return definitionsElement;
	}

	@Override
	public GatProcessElement getGatProcessElement()
	{
		return processElement;
	}

	@Override
	public GatBPDiagramElement getGatDiagramElement()
	{
		return diagramElement;
	}

	@Override
	public Process getXpdlProcess()
	{
		return xpdlProcess;
	}

	private Pool getXpdlProcessPool()
	{
		Collection<Pool> pools = GatModelUtil.getProcessPools(xpdlProcess);

		if (pools.size() > 1)
		{
			throw new RuntimeException("Graphical Audit Trail models do not support profecsses with multiple pools.");
		}

		if (pools.size() == 1)
		{
			return pools.iterator().next();
		}

		return null;
	}

	private boolean isArtifactInSubProcess(Artifact artifact)
	{
		NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(artifact);

		if (ngi != null)
		{
			String laneId = ngi.getLaneId();

			for (ActivitySet actSet : xpdlProcess.getActivitySets())
			{
				if (actSet.getId().equals(laneId))
				{
					return true;
				}
			}
		}

		return false;
	}

	private boolean isAssociationInSubProcess(Association association)
	{
		String source = association.getSource();
		String target = association.getTarget();

		for (ActivitySet actSet : xpdlProcess.getActivitySets())
		{
			if (isEndpointInActivitySet(source, actSet) && isEndpointInActivitySet(target, actSet))
			{
				return true;
			}
		}

		return false;
	}

	private boolean isEndpointInActivitySet(String elementId, ActivitySet actSet)
	{
		if (actSet.getActivity(elementId) != null)
		{
			return true;
		}

		String actSetId = actSet.getId();

		for (Artifact artifact : GatModelUtil.getAllArtifactsInProcess(xpdlProcess))
		{
			if (elementId.equals(artifact.getId()))
			{
				NodeGraphicsInfo ngi = GatModelUtil.getNodeGraphicsInfo(artifact);

				if (ngi != null && actSetId.equals(ngi.getLaneId()))
				{
					return true;
				}
			}
		}

		return false;
	}

}

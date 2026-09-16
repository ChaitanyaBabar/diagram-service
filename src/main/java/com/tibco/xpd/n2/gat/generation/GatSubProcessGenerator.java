/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import java.util.List;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatAdHocSubProcessElement;
import com.tibco.xpd.n2.gat.model.GatAssociationElement;
import com.tibco.xpd.n2.gat.model.GatBPShapeElement;
import com.tibco.xpd.n2.gat.model.GatDataObjectElement;
import com.tibco.xpd.n2.gat.model.GatDataObjectReferenceElement;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.model.GatSequenceFlowElement;
import com.tibco.xpd.n2.gat.model.GatSubProcessElement;
import com.tibco.xpd.n2.gat.model.GatTextAnnotationElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPDiagramElement;
import com.tibco.xpd.n2.gat.model.diagram.GatBPEdgeElement;
import com.tibco.xpd.n2.gat.util.GatTaskObjectUtil;
import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.ArtifactType;
import com.tibco.xpd.xpdl2.Association;
import com.tibco.xpd.xpdl2.BlockActivity;
import com.tibco.xpd.xpdl2.Transition;
import com.tibco.xpd.xpdl2.ViewType;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail Generator for Embedded/Event Sub-Process activities
 *
 * @author Sid Allway
 * @since Jan 2026
 */
public class GatSubProcessGenerator extends GatActivityGenerator
{
	/**
	 * If generating a collapsed Embedded sub-process then it will have it's on BPDiagram element containing the diagram
	 * content of all it's children (if it's expanded then the child content goes directly into the parent flow
	 * containers BPDiagram element - so this diagram will be none in that case)
	 */
	private GatBPDiagramElement collapsedSubProcessDiagram = null;

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param xpdlActivity
	 */
	public GatSubProcessGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);

	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatActivityGenerator#generateActivity(com.tibco.xpd.xpdl2.Activity)
	 *
	 * @param xpdlActivity
	 * @return
	 */
	@Override
	protected GatActivityElement generateActivity(Activity xpdlActivity)
	{
		/*
		 * Normal and Ad-Hoc embedded sub-process are different GAT model types.
		 */
		GatSubProcessElement subProcessElement;

		ActivitySet activitySet = GatTaskObjectUtil.getActivitySet(xpdlActivity);

		if (activitySet.isAdHoc())
		{
			subProcessElement = new GatAdHocSubProcessElement(xpdlActivity.getId(), xpdlActivity.getName(),
					GatModelUtil.getDisplayNameOrName(xpdlActivity));
		}
		else
		{
			subProcessElement = new GatSubProcessElement(xpdlActivity.getId(), xpdlActivity.getName(),
					GatModelUtil.getDisplayNameOrName(xpdlActivity));
		}

		/*
		 * If generating a collapsed Embedded sub-process then it will have it's on BPDiagram element containing the
		 * diagram content of all it's children (if it's expanded then the child content goes directly into the parent
		 * flow containers BPDiagram element - so this diagram will be none in that case)
		 */
		if (isXpdlSubProcessCollapsed())
		{
			/* Create and add the BPDiagram element for this collapsed embedded sub-process child content */
			collapsedSubProcessDiagram = new GatBPDiagramElement(subProcessElement);

			getGatDefinitionsElement().addDiagram(collapsedSubProcessDiagram);
		}

		/*
		 * Set whether it's a plain Embedded Sub-Process or an Event Sub-Process
		 */
		BlockActivity blockActivity = xpdlActivity.getBlockActivity();

		if (blockActivity != null)
		{
			Object isEventSubProcess = GatModelUtil.getOtherAttribute(blockActivity,
					XpdExtensionPackage.eINSTANCE.getDocumentRoot_IsEventSubProcess());

			if (Boolean.TRUE.equals(isEventSubProcess))
			{
				subProcessElement.setTriggeredByEvent(Boolean.TRUE);
			}
			else
			{
				subProcessElement.setTriggeredByEvent(Boolean.FALSE);
			}

			/*
			 * Add the Embedded Sub-Process child activities and flows.
			 */
			if (activitySet != null)
			{
				/*
				 * Generate for all the activities in top level of process.
				 */
				for (Activity xpdlChildActivity : activitySet.getActivities())
				{
					/* Create generator for the activity */
					GatFlowNodeGenerator activityGenerator = GatFlowNodeGeneratorFactory.createGenerator(this,
							xpdlChildActivity);

					/* Generate the semantic model for the activity */
					GatFlowNodeElement gatActivity = activityGenerator.generate();
					subProcessElement.addFlowElement(gatActivity);

					/*
					 * Generate the Diagram shape model for the activity and add to the correct BPDiagram element
					 * depending on whether this sub-process is expanded (use parent BPDiagram) or collapsed (use
					 * sub-process BPDiagram).
					 */
					GatBPShapeElement shapeElement = activityGenerator.generateShape(gatActivity);
					getGatDiagramElement().addDiagramElement(shapeElement);
				}

				/*
				 * Generate for all the sequence flows in top level of process.
				 */
				for (Transition xpdlChildSequenceFlow : activitySet.getTransitions())
				{
					/* Create generator for this sequence flow */
					GatSequenceFlowGenerator sequenceFlowGenerator = new GatSequenceFlowGenerator(this,
							xpdlChildSequenceFlow);

					/* Generate the semantic model for the sequence flow */
					GatSequenceFlowElement sequenceFlowElement = sequenceFlowGenerator.generate();
					subProcessElement.addFlowElement(sequenceFlowElement);

					/*
					 * Generate the diagram model for the sequence flow and add to the correct BPDiagram element
					 * depending on whether this sub-process is expanded (use parent BPDiagram) or collapsed (use
					 * sub-process BPDiagram).
					 */
					GatBPEdgeElement bpEdgeElement = sequenceFlowGenerator.generateBPEdge(sequenceFlowElement);
					getGatDiagramElement().addDiagramElement(bpEdgeElement);
				}

				/*
				 * Generate artifacts (DataObject/DataObjectReference, TextAnnotation) inside this sub-process.
				 * Artifacts are stored at Package level with laneId = ActivitySet ID.
				 */
				List<Artifact> subProcArtifacts = GatModelUtil.getArtifactsInEmbeddedSubProc(xpdlActivity);

				for (Artifact xpdlArtifact : subProcArtifacts)
				{
					if (ArtifactType.DATA_OBJECT_LITERAL == xpdlArtifact.getArtifactType())
					{
						GatDataObjectReferenceGenerator generator = new GatDataObjectReferenceGenerator(this,
								xpdlArtifact);

						GatDataObjectReferenceElement referenceElement = generator.generate();
						subProcessElement.addFlowElement(referenceElement);

						GatDataObjectElement dataObjectElement = generator.generateDataObject();
						subProcessElement.addFlowElement(dataObjectElement);

						GatBPShapeElement shapeElement = generator.generateShape(referenceElement);
						getGatDiagramElement().addDiagramElement(shapeElement);
					}
					else if (ArtifactType.ANNOTATION_LITERAL == xpdlArtifact.getArtifactType())
					{
						GatTextAnnotationGenerator generator = new GatTextAnnotationGenerator(this, xpdlArtifact);

						GatTextAnnotationElement annotationElement = generator.generate();
						subProcessElement.addArtifact(annotationElement);

						GatBPShapeElement shapeElement = generator.generateShape(annotationElement);
						getGatDiagramElement().addDiagramElement(shapeElement);
					}
				}

				/*
				 * Generate associations whose BOTH endpoints are inside this sub-process. Associations are stored
				 * at Package level and reference source/target by ID. An endpoint is "inside" if it's an Activity
				 * in the ActivitySet or an Artifact with laneId = ActivitySet ID.
				 */
				for (Association xpdlAssociation : GatModelUtil.getAllAssociationsInProc(getXpdlProcess()))
				{
					if (isEndpointInSubProcess(xpdlAssociation.getSource(), activitySet, subProcArtifacts)
							&& isEndpointInSubProcess(xpdlAssociation.getTarget(), activitySet, subProcArtifacts))
					{
						GatAssociationGenerator associationGenerator = new GatAssociationGenerator(this,
								xpdlAssociation, activitySet);

						GatAssociationElement associationElement = associationGenerator.generate();
						subProcessElement.addArtifact(associationElement);

						GatBPEdgeElement edgeElement = associationGenerator.generateBPEdge(associationElement);
						getGatDiagramElement().addDiagramElement(edgeElement);
					}
				}
			}
		}

		return subProcessElement;
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatActivityGenerator#generateShape(com.tibco.xpd.n2.gat.model.GatFlowNodeElement)
	 *
	 * @param flowNodeElement
	 * @return
	 */
	@Override
	public GatBPShapeElement generateShape(GatFlowNodeElement flowNodeElement)
	{
		GatBPShapeElement bpShapeElement = super.generateShape(flowNodeElement);

		/* Tag the shape as expanded or not */
		bpShapeElement.setIsExpanded(!isXpdlSubProcessCollapsed());

		return bpShapeElement;
	}

	/**
	 * @see com.tibco.xpd.n2.gat.generation.BaseGatGenerator#getGatDiagramElement()
	 *
	 * @return
	 */
	@Override
	public GatBPDiagramElement getGatDiagramElement()
	{
		/*
		 * If generating a collapsed Embedded sub-process then it will have it's on BPDiagram element containing the
		 * diagram content of all it's children (if it's expanded then the child content goes directly into the parent
		 * flow containers BPDiagram element - so this diagram will be none in that case)
		 *
		 * Another way to look at it is that the 'BPDiagram container elements are eitehr the root process or a
		 * collapsed embedded sub-process and an object's/flow's shape/edge is placed in the BPDiagram for the nearest
		 * of these ancestor containers'.
		 *
		 * So for a collapsed sub-process (our diagram != null) we return our own BPDiagram element...
		 */
		if (collapsedSubProcessDiagram != null)
		{
			return collapsedSubProcessDiagram;
		}

		/*
		 * ... or for an expanded sub-process we return the parent's BPDiagram.
		 *
		 * So basically we end up returning the nearest collapsed-sub-process parent or the top-level process.
		 */
		return getParent().getGatDiagramElement();
	}

	/**
	 * @return <code>true</code> if the XPDL Embedded Sub-Process is collapsed.
	 */
	private boolean isXpdlSubProcessCollapsed()
	{
		BlockActivity blockActivity = getXpdlActivity().getBlockActivity();

		if (blockActivity != null)
		{
			return ViewType.COLLAPSED.equals(blockActivity.getView());
		}

		return true;
	}

	/**
	 * Check whether the given element ID refers to an Activity or Artifact inside the given sub-process.
	 *
	 * @param elementId
	 *            the source or target ID of an association
	 * @param activitySet
	 *            the sub-process's ActivitySet
	 * @param subProcArtifacts
	 *            artifacts belonging to this sub-process (from {@code getArtifactsInEmbeddedSubProc})
	 * @return true if the element is inside this sub-process
	 */
	private boolean isEndpointInSubProcess(String elementId, ActivitySet activitySet, List<Artifact> subProcArtifacts)
	{
		if (activitySet.getActivity(elementId) != null)
		{
			return true;
		}

		for (Artifact art : subProcArtifacts)
		{
			if (elementId.equals(art.getId()))
			{
				return true;
			}
		}

		return false;
	}

}

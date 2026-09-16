/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatDataObjectElement;
import com.tibco.xpd.n2.gat.model.GatDataObjectReferenceElement;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.n2.gat.util.GatModelUtil;

/**
 * Graphical Audit Trail generator for DataObject artifacts.
 *
 * Generates both the bp:DataObjectReference and bp:DataObject flow elements, plus the BPShape diagram element for the
 * reference.
 *
 * @author cbabar
 * @since Jul 15, 2026
 */
@SuppressWarnings("nls")
public class GatDataObjectReferenceGenerator extends GatArtifactGenerator
{
	/**
	 * @param parent
	 * @param xpdlArtifact
	 */
	public GatDataObjectReferenceGenerator(BaseGatGenerator parent, Artifact xpdlArtifact)
	{
		super(parent, xpdlArtifact);
	}

	/**
	 * Generate the DataObjectReference flow element.
	 *
	 * @return {@link GatDataObjectReferenceElement}
	 */
	@Override
	public GatDataObjectReferenceElement generate()
	{
		Artifact xpdlArtifact = getXpdlArtifact();

		GatDataObjectReferenceElement referenceElement = new GatDataObjectReferenceElement(xpdlArtifact.getId(),
				xpdlArtifact.getName(), GatModelUtil.getDisplayNameOrName(xpdlArtifact));

		referenceElement.setDataObjectRef(getDataObjectId());

		return referenceElement;
	}

	/**
	 * Generate the DataObject flow element.
	 *
	 * @return {@link GatDataObjectElement}
	 */
	public GatDataObjectElement generateDataObject()
	{
		return new GatDataObjectElement(getDataObjectId());
	}

	/**
	 * @return The DataObject ID, either from the XPDL DataObject child element or synthesized from the artifact ID.
	 */
	private String getDataObjectId()
	{
		Artifact xpdlArtifact = getXpdlArtifact();

		if (xpdlArtifact.getDataObject() != null)
		{
			return xpdlArtifact.getDataObject().getId();
		}

		return xpdlArtifact.getId() + "_dataObject";
	}

}

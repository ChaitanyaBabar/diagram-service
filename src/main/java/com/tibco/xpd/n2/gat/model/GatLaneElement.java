/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphical Audit Trail lane element.
 *
 * @author Sid Allway
 * @since 23 Mar 2026
 */
@SuppressWarnings("nls")
public class GatLaneElement extends GatNamedElement
{
	private static final long		serialVersionUID	= -8659553170268378865L;

	/**
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatLaneElement(String id, String internalName, String name)
	{
		super("Lane", id, internalName, name);

		flowNodeRef = new ArrayList<String>();
		put("flowNodeRef", flowNodeRef);
	}

	/**
	 * The flowNodeRef property
	 */
	private List<String> flowNodeRef;

	/**
	 * Add a flow node reference element to the GAT lane flowNodeRef(s) array
	 *
	 * @param CollaborationElement
	 */
	public void addFlowNodeRef(String flowNodeId)
	{
		flowNodeRef.add(flowNodeId);
	}
}

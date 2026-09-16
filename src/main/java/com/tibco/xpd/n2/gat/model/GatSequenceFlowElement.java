/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Graphical Audit Trail Sequence Flow model element
 *
 * @author Sid Allway
 * @since 24 Dec 2025
 */
@SuppressWarnings("nls")
public class GatSequenceFlowElement extends GatFlowElement
{
	private static final long serialVersionUID = -132613934913723984L;

	/**
	 * @param id
	 * @param name
	 */
	public GatSequenceFlowElement(String id, String name)
	{
		super("SequenceFlow", id, null /* flows have no internal name property */, name);
	}

	/**
	 * Set the activity id that is the source of this sequence flow
	 *
	 * @param sourceRef
	 */
	public void setSourceRef(String sourceRef)
	{
		put("sourceRef", sourceRef);
	}

	/**
	 * Set the activity id that is the target of this sequence flow
	 *
	 * @param sourceRef
	 */
	public void setTargetRef(String targetRef)
	{
		put("targetRef", targetRef);
	}

	/**
	 * Tag this sequence flow as a conditional flow.
	 *
	 * As the Graphical Audit Trail representation of sequence flows does not need the semantic detail of the condition,
	 * we will just add a blank conditionExpression element to the model
	 */
	public void setIsCondition()
	{
		Map<String, String> conditionExpression = new LinkedHashMap<String, String>();
		conditionExpression.put("$type", "bp:OperationExpression");

		put("conditionExpression", conditionExpression);
	}

}

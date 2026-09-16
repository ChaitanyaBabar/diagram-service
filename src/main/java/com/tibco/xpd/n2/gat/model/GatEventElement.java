/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all Graphical Audit Trail Event model elements
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public abstract class GatEventElement extends GatFlowNodeElement
{
	private static final long		serialVersionUID	= 424414132822306635L;

	/**
	 * The eventDefinitions array property.
	 */
	List<GatEventDefinitionElement>	eventDefinitions	= null;

	/**
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	protected GatEventElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

	/**
	 * Add an EventDefinition to the eventDefinitions array property.
	 *
	 * @param eventDefinitionElement
	 */
	public void addEventDefintion(GatEventDefinitionElement eventDefinitionElement)
	{
		/* Create and add array property list if not done so already. */
		if (eventDefinitions == null)
		{
			eventDefinitions = new ArrayList<>();
			put("eventDefinitions", eventDefinitions);
		}

		/* Add event defoinition to the eventDefinitions array */
		eventDefinitions.add(eventDefinitionElement);
	}

}

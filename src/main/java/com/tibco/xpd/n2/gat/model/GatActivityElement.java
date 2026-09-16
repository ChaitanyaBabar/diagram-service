/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Base class for all GAT 'Activity' based elements (Tasks, Call Activity and Sub-Process)
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatActivityElement extends GatFlowNodeElement
{
	private static final long serialVersionUID = 4416197702922202407L;

	/**
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatActivityElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

	/**
	 * Set the multiInstanceLoopCharacteristics property
	 *
	 * @param multiInstanceLoopCharacteristicsElement
	 */
	public void setMultiInstanceLoopCharacteristics(
			GatMultiInstanceLoopCharacteristicsElement multiInstanceLoopCharacteristicsElement)
	{
		put("multiInstanceLoopCharacteristics", multiInstanceLoopCharacteristicsElement);
	}

	/**
	 * Set the standardLoopCharacteristics property
	 *
	 * @param standardLoopCharacteristics
	 */
	public void setStandardLoopCharacteristics(
			GatStandardLoopCharacteristicsElement standardLoopCharacteristicsElement)
	{
		put("standardLoopCharacteristics", standardLoopCharacteristicsElement);
	}
}

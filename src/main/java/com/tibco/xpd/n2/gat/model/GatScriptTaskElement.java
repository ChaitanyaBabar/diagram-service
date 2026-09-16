/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Script Task model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatScriptTaskElement extends GatActivityElement
{
	private static final long serialVersionUID = 5969089849620869617L;

	/**
	 * Construct
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatScriptTaskElement(String id, String internalName, String name)
	{
		super("ScriptTask", id, internalName, name);
	}

}

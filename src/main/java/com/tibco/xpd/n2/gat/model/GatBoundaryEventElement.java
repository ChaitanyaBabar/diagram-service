/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail BoundaryEvent model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatBoundaryEventElement extends GatEventElement
{
	private static final long serialVersionUID = -3717481125795282472L;

	/**
	 * Construct
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatBoundaryEventElement(String id, String internalName, String name)
	{
		super("BoundaryEvent", id, internalName, name);
	}

	/**
	 * Set the attachedToRef property
	 *
	 * @param attachedToRef
	 */
	public void setAttachedToRef(String attachedToRef)
	{
		put("attachedToRef", attachedToRef);
	}

	/**
	 * Set the cancelActivity property
	 *
	 * @param cancelActivity
	 */
	public void setCancelActivity(Boolean cancelActivity)
	{
		put("cancelActivity", cancelActivity);
	}
}

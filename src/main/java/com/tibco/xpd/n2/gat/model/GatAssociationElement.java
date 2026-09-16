/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail Association model element
 *
 * @author cbabar
 * @since Jul 16, 2026
 */
@SuppressWarnings("nls")
public class GatAssociationElement extends GatIdElement
{
	private static final long serialVersionUID = 5832166341172527166L;

	/**
	 * @param id
	 */
	public GatAssociationElement(String id)
	{
		super("Association", id);
	}

	/**
	 * @param sourceRef
	 */
	public void setSourceRef(String sourceRef)
	{
		put("sourceRef", sourceRef);
	}

	/**
	 * @param targetRef
	 */
	public void setTargetRef(String targetRef)
	{
		put("targetRef", targetRef);
	}

	/**
	 * @param direction
	 */
	public void setAssociationDirection(String direction)
	{
		put("associationDirection", direction);
	}
}

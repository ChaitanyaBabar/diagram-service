/*
 * Copyright (c) 2004-2026. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail TextAnnotation model element
 *
 * @author cbabar
 * @since Jul 16, 2026
 */
@SuppressWarnings("nls")
public class GatTextAnnotationElement extends GatIdElement
{
	private static final long serialVersionUID = 4721055230061416055L;

	/**
	 * @param id
	 * @param text
	 */
	public GatTextAnnotationElement(String id, String text)
	{
		super("TextAnnotation", id);

		if (text != null && !text.isEmpty())
		{
			put("text", text);
		}
	}
}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail participant element. This is the equivalent of an XPDL Pool
 *
 * @author Sid Allway
 * @since 23 Mar 2026
 */
@SuppressWarnings("nls")
public class GatParticipantElement extends GatNamedElement
{
	private static final long serialVersionUID = 2273796552258834460L;

	/**
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatParticipantElement(String id, String internalName, String name)
	{
		super("Participant", id, internalName, name);
	}

	/**
	 * Set the process id that this Pool is related to.
	 *
	 * @param sourceRef
	 */
	public void setProcessRef(String processRef)
	{
		put("processRef", processRef);
	}

}

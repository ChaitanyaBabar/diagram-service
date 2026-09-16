/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

/**
 * Graphical Audit Trail CallActivity (invoke sub-process) model element
 *
 * @author Sid Allway
 * @since Jan 2026
 */
@SuppressWarnings("nls")
public class GatCallActivityElement extends GatActivityElement
{
	private static final long serialVersionUID = -1660009892973287522L;

	/**
	 * Enumeration of values for the CallActivity.invocationMode property
	 */
	public enum InvocationMode
	{
		Synchronous,
		AsynchronousAttached,
		AsynchronousDetached
	}

	/**
	 * Construct
	 *
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatCallActivityElement(String id, String internalName, String name)
	{
		super("CallActivity", id, internalName, name);
	}

	/**
	 * Construct with alternative type name.
	 *
	 * @param typeName
	 * @param id
	 * @param internalName
	 * @param name
	 */
	public GatCallActivityElement(String typeName, String id, String internalName, String name)
	{
		super(typeName, id, internalName, name);
	}

	/**
	 * Set the applicationId property (i.e. the application (project) that the called sub-process resides in).
	 *
	 * @param applicationId
	 */
	public void setApplicationId(String applicationId)
	{
		put("applicationId", applicationId);
	}

	/**
	 * Set the professArtifactId property (i.e. the id of the called called sub-process).
	 *
	 * @param processArtifactId
	 */
	public void setProcessArtifactId(String processArtifactId)
	{
		put("processArtifactId", processArtifactId);
	}

	/**
	 * Set the invocationMode property
	 *
	 * @param invocationMode
	 */
	public void setInvocationMode(InvocationMode invocationMode)
	{
		put("invocationMode", invocationMode != null ? invocationMode.name() : null);
	}
}

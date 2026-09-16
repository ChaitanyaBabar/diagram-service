/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphical Audit Trail collaboration element.
 *
 * @author Sid Allway
 * @since 23 Mar 2026
 */
@SuppressWarnings("nls")
public class GatCollaborationElement extends GatIdElement
{
	private static final long serialVersionUID = 4293118158399540296L;

	/**
	 * The participants property
	 */
	private List<GatParticipantElement>	participants		= null;

	/**
	 * The artifacts property
	 */
	private List<GatIdElement>			artifacts			= null;

	/**
	 * @param id
	 */
	public GatCollaborationElement(String id)
	{
		super("Collaboration", id);
	}

	/**
	 * Add a child element to the participants property
	 *
	 * @param participantElement
	 */
	public void addParticipantElement(GatParticipantElement participantElement)
	{
		if (participants == null)
		{
			participants = new ArrayList<GatParticipantElement>();
			put("participants", participants);
		}

		participants.add(participantElement);
	}

	/**
	 * Add a child element to the artifacts property
	 *
	 * @param artifactElement
	 */
	public void addArtifactElement(GatIdElement artifactElement)
	{
		if (artifacts == null)
		{
			participants = new ArrayList<GatParticipantElement>();
			put("artifacts", artifacts);
		}

		artifacts.add(artifactElement);
	}
}

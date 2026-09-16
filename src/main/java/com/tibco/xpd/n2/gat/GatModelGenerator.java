/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tibco.xpd.n2.gat.generation.GatProcessGenerator;
import com.tibco.xpd.n2.gat.model.GatDefinitionsElement;
import com.tibco.xpd.xpdl2.Process;

/**
 * Graphical Audit Model generator
 *
 * @author Sid Allway
 * @since 17 Dec 2025
 */
public class GatModelGenerator
{
	/**
	 * The source XPDL process
	 */
	private Process					xpdlProcess;

	/**
	 * Constructor
	 *
	 * @param xpdlProcess
	 */
	public GatModelGenerator(Process xpdlProcess)
	{
		super();
		this.xpdlProcess = xpdlProcess;
	}

	/**
	 * Generate the Graphical Audit Trail model for the given process and return it serialised as a JSON formatted
	 * string.
	 *
	 * @param process
	 *
	 * @return Graphical Audit Trail JSON string for the given process.
	 */
	public String generateJsonGatModel()
	{
		GatProcessGenerator processGenerator = new GatProcessGenerator(xpdlProcess);

		GatDefinitionsElement definitionsElement = processGenerator.generate();

		return serialiseToJSON(definitionsElement);
	}

	/**
	 * Serializes the whole Graphical Audit Trail Definitions as JSON and returns it.
	 *
	 * @return the runtime case access model serialised as JSON
	 */
	private String serialiseToJSON(GatDefinitionsElement definitionsElement)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String json = gson.toJson(definitionsElement);

		return json;
	}

}

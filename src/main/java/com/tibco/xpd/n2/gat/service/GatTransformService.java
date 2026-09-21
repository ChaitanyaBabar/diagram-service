/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tibco.xpd.n2.gat.exception.GatGenerationException;
import com.tibco.xpd.n2.gat.generation.GatProcessGenerator;
import com.tibco.xpd.n2.gat.model.GatDefinitionsElement;
import com.tibco.xpd.n2.gat.xpdl.StandaloneXpdlLoader;
import com.tibco.xpd.xpdl2.Process;

/**
 * Service that orchestrates the XPDL-to-GAT transformation pipeline.
 *
 * <p>Flow:</p>
 * <ol>
 *   <li>Parse XPDL XML into EMF Process object(s) via {@link StandaloneXpdlLoader}</li>
 *   <li>Feed each Process into {@link GatProcessGenerator} to produce a {@link GatDefinitionsElement}</li>
 *   <li>Serialize the result to JSON via Gson</li>
 * </ol>
 */
@Service
public class GatTransformService
{
    private static final Logger LOG = LoggerFactory.getLogger(GatTransformService.class);

    private final StandaloneXpdlLoader xpdlLoader;
    private final Gson                 gson;

    public GatTransformService(StandaloneXpdlLoader xpdlLoader)
    {
        this.xpdlLoader = xpdlLoader;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Transform a single XPDL process into a GAT JSON model.
     *
     * @param xpdlInput the XPDL file input stream
     * @param processId optional process ID to select; null means first process
     * @return GAT JSON string
     */
    public String transformXpdlToGat(InputStream xpdlInput, String processId)
    {
        LOG.debug("Loading XPDL, processId={}", processId);
        Process process = xpdlLoader.loadProcess(xpdlInput, processId);

        LOG.debug("Generating GAT model for process: {}", process.getId());
        try
        {
            GatProcessGenerator processGenerator = new GatProcessGenerator(process);
            GatDefinitionsElement definitionsElement = processGenerator.generate();
            return gson.toJson(definitionsElement);
        }
        catch (Exception e)
        {
            throw new GatGenerationException(
                    "Failed to generate GAT model for process: " + process.getId(), e);
        }
    }

    /**
     * Transform all processes in an XPDL package into GAT JSON models.
     *
     * @param xpdlInput the XPDL file input stream
     * @return JSON array of GAT models (one per process)
     */
    public String transformAllProcesses(InputStream xpdlInput)
    {
        LOG.debug("Loading all processes from XPDL");
        List<Process> processes = xpdlLoader.loadAllProcesses(xpdlInput);

        LOG.debug("Generating GAT models for {} processes", processes.size());
        List<GatDefinitionsElement> results = new ArrayList<>();

        for (Process process : processes)
        {
            try
            {
                GatProcessGenerator processGenerator = new GatProcessGenerator(process);
                GatDefinitionsElement definitionsElement = processGenerator.generate();

                // Include process identification so the client can distinguish processes
                definitionsElement.put("processId", process.getId());
                definitionsElement.put("processName",
                        process.getName() != null ? process.getName() : process.getId());

                results.add(definitionsElement);
            }
            catch (Exception e)
            {
                LOG.error("Failed to generate GAT model for process: {}", process.getId(), e);
                throw new GatGenerationException(
                        "Failed to generate GAT model for process: " + process.getId(), e);
            }
        }

        return gson.toJson(results);
    }
}

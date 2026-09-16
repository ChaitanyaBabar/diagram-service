/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.controller;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tibco.xpd.n2.gat.exception.GatGenerationException;
import com.tibco.xpd.n2.gat.exception.ProcessNotFoundException;
import com.tibco.xpd.n2.gat.exception.XpdlParseException;
import com.tibco.xpd.n2.gat.service.GatTransformService;

/**
 * REST controller for transforming XPDL files into GAT diagram models.
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li>{@code POST /api/diagram/transform} - Transform a single process from an XPDL file</li>
 *   <li>{@code POST /api/diagram/transform-all} - Transform all processes from an XPDL file</li>
 *   <li>{@code GET /api/diagram/health} - Health check</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/diagram")
public class GatController
{
    private static final Logger LOG = LoggerFactory.getLogger(GatController.class);

    private final GatTransformService gatTransformService;

    public GatController(GatTransformService gatTransformService)
    {
        this.gatTransformService = gatTransformService;
    }

    /**
     * Transform a single process from an XPDL file into a GAT diagram model.
     *
     * @param xpdlFile  the XPDL file (multipart upload)
     * @param processId optional process ID; if omitted, transforms the first process
     * @return GAT JSON response
     */
    @PostMapping(value = "/transform",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> transformXpdlToGat(
            @RequestParam("file") MultipartFile xpdlFile,
            @RequestParam(value = "processId", required = false) String processId)
    {
        LOG.info("Received transform request for file: {}, processId: {}",
                xpdlFile.getOriginalFilename(), processId);

        try (InputStream is = xpdlFile.getInputStream())
        {
            String gatJson = gatTransformService.transformXpdlToGat(is, processId);
            return ResponseEntity.ok(gatJson);
        }
        catch (XpdlParseException e)
        {
            LOG.error("XPDL parse error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(errorJson("XPDL_PARSE_ERROR", e.getMessage()));
        }
        catch (ProcessNotFoundException e)
        {
            LOG.warn("Process not found: {}", e.getProcessId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorJson("PROCESS_NOT_FOUND", e.getMessage()));
        }
        catch (GatGenerationException e)
        {
            LOG.error("GAT generation error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(errorJson("GAT_GENERATION_ERROR", e.getMessage()));
        }
        catch (Exception e)
        {
            LOG.error("Unexpected error during transform: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(errorJson("INTERNAL_ERROR", e.getMessage()));
        }
    }

    /**
     * Transform all processes from an XPDL file into GAT diagram models.
     *
     * @param xpdlFile the XPDL file (multipart upload)
     * @return JSON array of GAT models
     */
    @PostMapping(value = "/transform-all",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> transformAllProcesses(
            @RequestParam("file") MultipartFile xpdlFile)
    {
        LOG.info("Received transform-all request for file: {}",
                xpdlFile.getOriginalFilename());

        try (InputStream is = xpdlFile.getInputStream())
        {
            String gatJson = gatTransformService.transformAllProcesses(is);
            return ResponseEntity.ok(gatJson);
        }
        catch (XpdlParseException e)
        {
            LOG.error("XPDL parse error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(errorJson("XPDL_PARSE_ERROR", e.getMessage()));
        }
        catch (Exception e)
        {
            LOG.error("Unexpected error during transform-all: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(errorJson("INTERNAL_ERROR", e.getMessage()));
        }
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health()
    {
        return ResponseEntity.ok("{\"status\": \"UP\"}");
    }

    /**
     * Creates a simple JSON error response.
     */
    private String errorJson(String errorCode, String message)
    {
        // Escape quotes in message for safe JSON
        String escapedMessage = message != null
                ? message.replace("\\", "\\\\").replace("\"", "\\\"")
                : "Unknown error";
        return String.format("{\"error\": \"%s\", \"message\": \"%s\"}", errorCode, escapedMessage);
    }
}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the BPME XPDL Diagram Service.
 *
 * This service accepts XPDL files via REST and returns GAT (Graphical Audit Trail)
 * diagram models as JSON. It extracts the GAT generation logic from the Eclipse-based
 * TIBCO BPM Studio (S5x) into a headless, standalone service.
 */
@SpringBootApplication
public class DiagramServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(DiagramServiceApplication.class, args);
    }
}

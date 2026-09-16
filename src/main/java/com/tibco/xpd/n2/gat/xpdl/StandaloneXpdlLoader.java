/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tibco.xpd.n2.gat.exception.ProcessNotFoundException;
import com.tibco.xpd.n2.gat.exception.XpdlParseException;
import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.DocumentRoot;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.Xpdl2Package;

/**
 * Loads XPDL XML into EMF Process objects in standalone mode (no Eclipse Platform).
 *
 * <p>This component handles EMF package registration and resource factory setup
 * that would normally be performed by the Eclipse extension registry in an OSGi
 * environment. It uses {@link StandaloneXpdlResourceFactory} to create
 * {@link StandaloneXpdlResource} instances that bypass the Eclipse transactional
 * editing domain used by the Studio's {@code ResourceExtensions}.</p>
 *
 * <p>Thread safety: Each call creates its own ResourceSet, so concurrent calls
 * are safe. However, the static EMF package registration is only done once.</p>
 */
@Component
public class StandaloneXpdlLoader
{
    private static final Logger LOG = LoggerFactory.getLogger(StandaloneXpdlLoader.class);

    /**
     * Static initializer: register EMF packages for standalone use.
     * In Eclipse/OSGi, this is done automatically via the extension registry.
     * In standalone mode, we must trigger the PackageImpl.init() methods explicitly.
     */
    static
    {
        // Trigger Xpdl2PackageImpl.init() - registers the XPDL 2.1 EMF package
        Xpdl2Package.eINSTANCE.eClass();
        // Trigger XpdExtensionPackageImpl.init() - registers the xpdExtension EMF package
        XpdExtensionPackage.eINSTANCE.eClass();

        LOG.info("EMF packages registered: Xpdl2Package, XpdExtensionPackage");
    }

    /**
     * Load a single process from an XPDL input stream.
     *
     * @param xpdlInput the XPDL XML input stream
     * @param processId the ID of the process to load; null means load the first process
     * @return the EMF Process object
     * @throws XpdlParseException       if the XPDL cannot be parsed
     * @throws ProcessNotFoundException if the specified processId is not found
     */
    public Process loadProcess(InputStream xpdlInput, String processId)
    {
        com.tibco.xpd.xpdl2.Package xpdlPackage = loadPackage(xpdlInput);
        EList<Process> processes = xpdlPackage.getProcesses();

        if (processes == null || processes.isEmpty())
        {
            throw new XpdlParseException("XPDL package contains no processes");
        }

        if (processId != null && !processId.isEmpty())
        {
            return processes.stream()
                    .filter(p -> processId.equals(p.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ProcessNotFoundException(processId));
        }

        // Default: return the first process
        Process firstProcess = processes.get(0);
        LOG.debug("No processId specified, using first process: {}", firstProcess.getId());
        return firstProcess;
    }

    /**
     * Load all processes from an XPDL input stream.
     *
     * @param xpdlInput the XPDL XML input stream
     * @return list of all EMF Process objects in the package
     * @throws XpdlParseException if the XPDL cannot be parsed
     */
    public List<Process> loadAllProcesses(InputStream xpdlInput)
    {
        com.tibco.xpd.xpdl2.Package xpdlPackage = loadPackage(xpdlInput);
        EList<Process> processes = xpdlPackage.getProcesses();

        if (processes == null || processes.isEmpty())
        {
            LOG.warn("XPDL package contains no processes");
            return Collections.emptyList();
        }

        LOG.debug("Loaded {} processes from XPDL package", processes.size());
        return processes;
    }

    /**
     * Parse XPDL XML into an EMF Package object.
     *
     * <p>Creates a fresh ResourceSet for each invocation to ensure thread safety.
     * Registers the {@link StandaloneXpdlResourceFactory} for the ".xpdl" extension
     * so that EMF uses our custom resource implementation (which avoids Eclipse
     * transactional editing domain dependencies).</p>
     *
     * @param xpdlInput the XPDL XML input stream
     * @return the top-level XPDL Package
     * @throws XpdlParseException if parsing fails
     */
    private com.tibco.xpd.xpdl2.Package loadPackage(InputStream xpdlInput)
    {
        try
        {
            ResourceSet resourceSet = new ResourceSetImpl();

            // Register resource factory for .xpdl extension
            resourceSet.getResourceFactoryRegistry()
                    .getExtensionToFactoryMap()
                    .put("xpdl", new StandaloneXpdlResourceFactory());

            // Create a resource with a synthetic URI (content comes from InputStream)
            URI uri = URI.createURI("input.xpdl");
            Resource resource = resourceSet.createResource(uri);

            // Load the XPDL XML from the input stream
			resource.load(xpdlInput, ((XMLResourceImpl) resource).getDefaultLoadOptions());

            // Check for loading errors
            if (!resource.getErrors().isEmpty())
            {
                StringBuilder errorMsg = new StringBuilder("XPDL loading errors: ");
                for (Resource.Diagnostic diag : resource.getErrors())
                {
                    errorMsg.append(diag.getMessage()).append("; ");
                }
                LOG.warn(errorMsg.toString());
            }

            // The root element of an XPDL resource is a DocumentRoot containing a Package
            if (resource.getContents().isEmpty())
            {
                throw new XpdlParseException("XPDL resource loaded but contains no content");
            }

            Object root = resource.getContents().get(0);
            if (root instanceof DocumentRoot)
            {
                DocumentRoot docRoot = (DocumentRoot) root;
                com.tibco.xpd.xpdl2.Package pkg = docRoot.getPackage();
                if (pkg == null)
                {
                    throw new XpdlParseException(
                            "XPDL DocumentRoot does not contain a Package element");
                }
                LOG.debug("Loaded XPDL package: id={}, name={}",
                        pkg.getId(), pkg.getName());
                return pkg;
            }
            else if (root instanceof com.tibco.xpd.xpdl2.Package)
            {
                // Some EMF configurations may return the Package directly
                return (com.tibco.xpd.xpdl2.Package) root;
            }
            else
            {
                throw new XpdlParseException(
                        "Unexpected root element type: " + root.getClass().getName()
                        + ". Expected DocumentRoot or Package.");
            }
        }
        catch (XpdlParseException e)
        {
            throw e; // Re-throw our own exceptions
        }
        catch (IOException e)
        {
            throw new XpdlParseException("Failed to read XPDL input stream", e);
        }
        catch (Exception e)
        {
            throw new XpdlParseException("Failed to parse XPDL: " + e.getMessage(), e);
        }
    }
}

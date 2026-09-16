/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

/**
 * Factory for creating {@link StandaloneXpdlResource} instances.
 *
 * <p>This replaces the Eclipse-based {@code Xpdl2ResourceFactoryImpl} which creates
 * {@code Xpdl2ResourceImpl} instances that depend on {@code ResourceExtensions}
 * (which in turn requires Eclipse's transactional editing domain).</p>
 *
 * <p>Configures the same load options as the original factory:</p>
 * <ul>
 *   <li>{@code OPTION_EXTENDED_META_DATA} - enables extended metadata processing for
 *       XPDL's mixed content and xpdExtension "other" attributes/elements</li>
 *   <li>{@code OPTION_SCHEMA_LOCATION} - processes xsi:schemaLocation attributes</li>
 *   <li>{@code OPTION_USE_LEXICAL_HANDLER} - preserves CDATA sections and entity references</li>
 *   <li>UTF-8 encoding</li>
 * </ul>
 */
public class StandaloneXpdlResourceFactory extends ResourceFactoryImpl
{
    @Override
    public Resource createResource(URI uri)
    {
        StandaloneXpdlResource resource = new StandaloneXpdlResource(uri);

        // Match the load options from the original Xpdl2ResourceFactoryImpl
        resource.getDefaultLoadOptions().put(
                XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
        resource.getDefaultLoadOptions().put(
                XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        resource.getDefaultLoadOptions().put(
                XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);

        // UTF-8 encoding (as per original factory)
        resource.setEncoding("UTF-8");

        return resource;
    }
}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.xpdl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.emf.ecore.xml.type.InvalidDatatypeValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone port of {@code com.tibco.xpd.xpdl2.extension.HelperExtensions}.
 *
 * <p>Extends {@link XMLHelperImpl} to add:</p>
 * <ul>
 *   <li>Namespace prefix tracking (required by the SAX parser extensions for
 *       wrapper element detection)</li>
 *   <li>Lenient feature resolution — when a feature is not found in the given
 *       namespace, tries alternative namespace lookups and subclass-wrap
 *       annotation matching before falling back to null (with a warning log)
 *       instead of throwing {@code FeatureNotFoundException}</li>
 *   <li>Lenient data type creation — handles empty string values gracefully
 *       instead of throwing {@code InvalidDatatypeValueException}</li>
 * </ul>
 *
 * <p>This replaces Eclipse Platform logging ({@code ILog}, {@code Platform.getLog})
 * with SLF4J.</p>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class StandaloneHelperExtensions extends XMLHelperImpl
{
    private static final Logger LOG = LoggerFactory.getLogger(StandaloneHelperExtensions.class);

    /**
     * Tracks (namespace, name) pairs that have already been logged as unknown,
     * to avoid flooding logs with repeated warnings for the same unknown feature.
     */
    private static class FeatureAndNamespace
    {
        private final String name;
        private final String namespace;

        FeatureAndNamespace(String name, String namespace)
        {
            this.name = name == null ? "" : name;
            this.namespace = namespace == null ? "" : namespace;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof FeatureAndNamespace)
            {
                FeatureAndNamespace other = (FeatureAndNamespace) obj;
                return name.equals(other.name) && namespace.equals(other.namespace);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return name.hashCode() * 31 + namespace.hashCode();
        }
    }

    private final List<FeatureAndNamespace> knownUnknownFeatures = new ArrayList<>();

    /** Namespace URI -> list of recorded prefixes, populated during parsing */
    private final Map prefixes = new HashMap();

    /**
     * Construct a helper for the given XML resource.
     *
     * @param resource the XML resource being loaded
     */
    public StandaloneHelperExtensions(XMLResource resource)
    {
        super(resource);
    }

    /**
     * Records namespace prefix mappings encountered during SAX parsing.
     * In addition to the standard XMLHelper behavior, stores the prefix
     * in our local map so that {@link StandaloneSAXParserExtensions} can
     * resolve wrapper element names with their namespace prefixes.
     */
    @Override
    public void addPrefix(String prefix, String uri)
    {
        super.addPrefix(prefix, uri);

        if (prefixes.containsKey(uri))
        {
            List val = (List) prefixes.get(uri);
            val.add(prefix);
        }
        else
        {
            List val = new ArrayList();
            val.add(prefix);
            prefixes.put(uri, val);
        }
    }

    /**
     * Returns the recorded prefixes for a given namespace URI.
     *
     * @param nsURI the namespace URI
     * @return list of prefix strings, or empty list if none recorded
     */
    public List getRecordedPrefixes(String nsURI)
    {
        List result = (List) prefixes.get(nsURI);
        if (result == null)
        {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /**
     * Lenient data type value creation. Handles empty strings gracefully
     * by attempting to create a default instance instead of throwing.
     */
    @Override
    protected Object createFromString(EFactory eFactory, EDataType eDataType, String value)
    {
        Object result = null;
        try
        {
            result = super.createFromString(eFactory, eDataType, value);
        }
        catch (InvalidDatatypeValueException e)
        {
            if (value == null || value.isEmpty())
            {
                try
                {
                    result = eDataType.getInstanceClass().newInstance();
                }
                catch (InstantiationException | IllegalAccessException e1)
                {
                    LOG.warn("Failed to create default instance for empty value of type {}",
                            eDataType.getName(), e1);
                }
            }
            else
            {
                throw e;
            }
        }
        return result;
    }

    /**
     * Extended feature resolution with fallback strategies.
     *
     * <p>When the standard EMF feature lookup fails, this method tries:</p>
     * <ol>
     *   <li>For attributes: retry with null namespace (handles older Studio files
     *       that incorrectly placed xpdl2: prefix on unqualified attributes)</li>
     *   <li>Scan for subclass-wrap annotations (handles wrapper elements for
     *       abstract type hierarchies like Implementation and Event)</li>
     *   <li>If still not found: log a warning and return null (lenient) instead
     *       of throwing FeatureNotFoundException</li>
     * </ol>
     */
    @Override
    public EStructuralFeature getFeature(EClass eClass, String namespaceURI,
            String name, boolean isElement)
    {
        EStructuralFeature feature = super.getFeature(eClass, namespaceURI, name, isElement);

        // Fallback for attributes: some older Studio files placed "xpdl2:" prefix
        // on attributes that should be unqualified. Retry with null namespace.
        if (feature == null && !isElement)
        {
            feature = super.getFeature(eClass, null, name, isElement);
        }

        // Fallback for subclass-wrap annotations: check if this element name
        // matches a concrete subclass of an abstract type referenced by a feature
        // with a "subclass-wrap" annotation.
        if (feature == null)
        {
            EList refs = eClass.getEAllReferences();
            for (Iterator iter = refs.iterator(); iter.hasNext(); )
            {
                EReference ref = (EReference) iter.next();
                EList annotations = ref.getEAnnotations();
                for (Iterator iterator = annotations.iterator(); iterator.hasNext(); )
                {
                    EAnnotation ann = (EAnnotation) iterator.next();
                    Object val = ann.getDetails()
                            .get(StandaloneExtensionsConstants.SUBCLASS_WRAP_ANNOTATION);
                    if (val != null)
                    {
                        EClassifier parent = ref.getEType();
                        EClassifier eCl = parent.getEPackage().getEClassifier(name);
                        if (eCl != null
                                && parent.getInstanceClass()
                                        .isAssignableFrom(eCl.getInstanceClass()))
                        {
                            return ref;
                        }
                    }
                }
            }
        }

        // If still not found, log a warning (once per unique feature) and return null.
        // This is lenient: the element will be skipped rather than causing a parse failure.
        if (feature == null)
        {
            FeatureAndNamespace feat = new FeatureAndNamespace(name, namespaceURI);
            if (!knownUnknownFeatures.contains(feat))
            {
                String resourceUri = (this.getResource() != null && this.getResource().getURI() != null)
                        ? this.getResource().getURI().toString()
                        : "<unknown>";
                LOG.warn("{} contains an unexpected element/attribute '{}' (from namespace '{}').",
                        resourceUri, name, namespaceURI);
                knownUnknownFeatures.add(feat);
            }
        }

        return feature;
    }

    /**
     * Returns null — subtype name override not needed for loading.
     */
    public String getSubTypeName(EObject peekObject, EStructuralFeature feature)
    {
        return null;
    }
}

/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.Artifact;
import com.tibco.xpd.xpdl2.Association;
import com.tibco.xpd.xpdl2.ConnectorGraphicsInfo;
import com.tibco.xpd.xpdl2.FlowContainer;
import com.tibco.xpd.xpdl2.GraphicalConnector;
import com.tibco.xpd.xpdl2.GraphicalNode;
import com.tibco.xpd.xpdl2.Lane;
import com.tibco.xpd.xpdl2.NamedElement;
import com.tibco.xpd.xpdl2.NodeGraphicsInfo;
import com.tibco.xpd.xpdl2.Pool;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.Transition;

/**
 * Extracted utility methods from {@code com.tibco.xpd.xpdl2.util.Xpdl2ModelUtil}.
 *
 * <p>Contains only the ~16 methods used by the GAT generation code. All methods
 * are pure EMF model navigation with no Eclipse Platform dependency.</p>
 *
 * <p><b>NOTE:</b> Method bodies will be populated by the implementer agent when
 * copying GAT generation classes from S5x. The signatures below match the
 * original Xpdl2ModelUtil exactly so that generation code can switch from
 * {@code Xpdl2ModelUtil.xxx()} to {@code GatModelUtil.xxx()} with a simple
 * search-and-replace.</p>
 *
 * @see Section 5.4 and Appendix A of the design document
 */
public final class GatModelUtil
{
    /** Tool ID used by Studio for XPDL graphics info */
    public static final String STUDIO_SPECIFIC_TOOL_ID = "XPD";

    /** Suffix for boundary event position graphics info */
    public static final String BORDER_EVENTPOS_IDSUFFIX = "BorderEventPosition";

    /** Suffix for connection/connector graphics info */
    public static final String CONNECTION_INFO_IDSUFFIX = "ConnectionInfo";

    /** Suffix for literal waypoints connector graphics info (ACE-10076) */
    public static final String LITERALPOINTS_INFO_IDSUFFIX = "LiteralPoints";

    private GatModelUtil()
    {
        // Static utility class
    }

    /**
     * Get the NodeGraphicsInfo for the default Studio tool ("XPD").
     *
     * @param node the graphical node
     * @return the NodeGraphicsInfo, or null if not found
     */
    public static NodeGraphicsInfo getNodeGraphicsInfo(GraphicalNode node)
    {
        if (node == null)
        {
            return null;
        }
        EList<NodeGraphicsInfo> infoList = node.getNodeGraphicsInfos();
        if (infoList != null)
        {
            for (NodeGraphicsInfo info : infoList)
            {
                String toolId = info.getToolId();
                if (STUDIO_SPECIFIC_TOOL_ID.equals(toolId))
                {
                    return info;
                }
            }
        }
        // Fall back to first entry if no tool-specific one found
        return (infoList != null && !infoList.isEmpty()) ? infoList.get(0) : null;
    }

    /**
     * Get the NodeGraphicsInfo for a specific tool suffix (e.g., "BorderEventPos").
     *
     * @param node   the graphical node
     * @param suffix the tool ID suffix
     * @return the NodeGraphicsInfo, or null if not found
     */
    public static NodeGraphicsInfo getNodeGraphicsInfo(GraphicalNode node, String suffix)
    {
        if (node == null || suffix == null)
        {
            return null;
        }
        String toolId = STUDIO_SPECIFIC_TOOL_ID + "." + suffix;
        EList<NodeGraphicsInfo> infoList = node.getNodeGraphicsInfos();
        if (infoList != null)
        {
            for (NodeGraphicsInfo info : infoList)
            {
                if (toolId.equals(info.getToolId()))
                {
                    return info;
                }
            }
        }
        return null;
    }

    /**
     * Get the display name of a named element, falling back to the element name.
     *
     * @param element the named element
     * @return the display name or element name, or empty string if neither exists
     */
    public static String getDisplayNameOrName(NamedElement element)
    {
        if (element == null)
        {
            return "";
        }
        String displayName = getDisplayName(element);
        if (displayName != null && !displayName.isEmpty())
        {
            return displayName;
        }
        String name = element.getName();
        return name != null ? name : "";
    }

    /**
     * Get the display name from xpdExtension's "DisplayName" other attribute.
     *
     * @param element the named element
     * @return the display name, or null if not set
     */
    public static String getDisplayName(NamedElement element)
    {
        if (element == null)
        {
            return null;
        }
        Object displayName = getOtherAttribute(element,
                XpdExtensionPackage.eINSTANCE.getDocumentRoot_DisplayName());
        return displayName instanceof String ? (String) displayName : null;
    }

    /**
     * Get a value from the "other" attribute FeatureMap (xpdExtension attributes).
     *
     * @param eObject the EObject to query
     * @param feature the structural feature to look up
     * @return the attribute value, or null
     */
    public static Object getOtherAttribute(EObject eObject, EStructuralFeature feature)
    {
        if (eObject == null || feature == null)
        {
            return null;
        }
        // Access the "otherAttributes" FeatureMap on the EObject
        EStructuralFeature otherAttrFeature = eObject.eClass()
                .getEStructuralFeature("otherAttributes");
        if (otherAttrFeature != null)
        {
            Object otherAttrs = eObject.eGet(otherAttrFeature);
            if (otherAttrs instanceof FeatureMap)
            {
                FeatureMap featureMap = (FeatureMap) otherAttrs;
                return featureMap.get(feature, false);
            }
        }
        return null;
    }

    /**
     * Get a value from the "other" element FeatureMap (xpdExtension elements).
     *
     * @param eObject the EObject to query
     * @param feature the structural feature to look up
     * @return the element value, or null
     */
    public static Object getOtherElement(EObject eObject, EStructuralFeature feature)
    {
        if (eObject == null || feature == null)
        {
            return null;
        }
        EStructuralFeature otherElemFeature = eObject.eClass()
                .getEStructuralFeature("otherElements");
        if (otherElemFeature != null)
        {
            Object otherElems = eObject.eGet(otherElemFeature);
            if (otherElems instanceof FeatureMap)
            {
                FeatureMap featureMap = (FeatureMap) otherElems;
                return featureMap.get(feature, false);
            }
        }
        return null;
    }

    /**
     * Get all pools associated with a process.
     *
     * @param process the XPDL process
     * @return collection of pools, or empty collection
     */
    public static Collection<Pool> getProcessPools(Process process)
    {
        if (process == null || process.getPackage() == null)
        {
            return Collections.emptyList();
        }
        List<Pool> processPools = new ArrayList<>();
        EList<Pool> allPools = process.getPackage().getPools();
        if (allPools != null)
        {
            String processId = process.getId();
            for (Pool pool : allPools)
            {
				if (processId != null && processId.equals(pool.getProcessId()))
                {
                    processPools.add(pool);
                }
            }
        }
        return processPools;
    }

    /**
     * Get all activities in a specific lane.
     *
     * @param lane the lane
     * @return list of activities in the lane
     */
    public static List<Activity> getActivitiesInLane(Lane lane)
    {
        List<Activity> activitiesInLane = new ArrayList<>();

        Process process = getProcessFromLane(lane);

        if (process != null)
        {
            String laneId = lane.getId();
            EList<Activity> activities = process.getActivities();

            if (activities != null)
            {
                for (Activity act : activities)
                {
                    NodeGraphicsInfo gi = getNodeGraphicsInfo(act);
                    if (gi != null && laneId.equals(gi.getLaneId()))
                    {
                        activitiesInLane.add(act);
                    }
                }
            }
        }
        return activitiesInLane;
    }

    private static Process getProcessFromLane(Lane lane)
    {
        if (lane == null)
        {
            return null;
        }
        Pool pool = lane.getParentPool();
        if (pool != null)
        {
            return getProcessFromPool(pool);
        }
        return null;
    }

    private static Process getProcessFromPool(Pool pool)
    {
		if (pool == null || pool.getProcessId() == null)
        {
            return null;
        }
        com.tibco.xpd.xpdl2.Package pkg = (com.tibco.xpd.xpdl2.Package) pool.eContainer();
        if (pkg != null)
        {
			String processId = pool.getProcessId();
            EList<Process> processes = pkg.getProcesses();
            if (processes != null)
            {
                for (Process p : processes)
                {
                    if (processId.equals(p.getId()))
                    {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get a lane by ID from a process.
     *
     * @param process the process
     * @param laneId  the lane ID
     * @return the Lane, or null if not found
     */
    public static Lane getLane(Process process, String laneId)
    {
        if (process == null || laneId == null)
        {
            return null;
        }
        for (Pool pool : getProcessPools(process))
        {
            EList<Lane> lanes = pool.getLanes();
            if (lanes != null)
            {
                for (Lane lane : lanes)
                {
                    if (laneId.equals(lane.getId()))
                    {
                        return lane;
                    }
                    // Check nested lanes
                    Lane nested = findLaneRecursive(lane, laneId);
                    if (nested != null)
                    {
                        return nested;
                    }
                }
            }
        }
        return null;
    }

    private static Lane findLaneRecursive(Lane parent, String laneId)
    {
		EList<Lane> childLanes = parent.getNestedLane();
        if (childLanes != null)
        {
            for (Lane child : childLanes)
            {
                if (laneId.equals(child.getId()))
                {
                    return child;
                }
                Lane nested = findLaneRecursive(child, laneId);
                if (nested != null)
                {
                    return nested;
                }
            }
        }
        return null;
    }

    /**
     * Get the Lane ancestor of an EObject. For an Activity directly in a Lane,
     * returns that Lane. For an Activity inside an ActivitySet (embedded subprocess),
     * walks up the subprocess hierarchy to find the containing Lane.
     *
     * @param eObject the starting EObject (typically an Activity)
     * @return the containing Lane, or null
     */
    public static Lane getLaneAncestor(EObject eObject)
    {
        EObject container = getContainer(eObject);
        if (container instanceof ActivitySet)
        {
            return getActivitySetParentLane((ActivitySet) container);
        }
        if (container instanceof Lane)
        {
            return (Lane) container;
        }
        return null;
    }

    /**
     * Get the Lane that contains the embedded subprocess activity owning the
     * given ActivitySet. Walks up through nested embedded subprocesses.
     *
     * @param actSet the ActivitySet
     * @return the containing Lane, or null
     */
    public static Lane getActivitySetParentLane(ActivitySet actSet)
    {
        Process process = actSet.getProcess();
        Lane lane = null;
        String lookForActSet = actSet.getId();

        while (lane == null)
        {
            Activity embSubProc = getEmbSubProcActivityForActSet(process, lookForActSet);
            if (embSubProc == null)
            {
                break;
            }

            EObject laneOrActSet = getContainer(embSubProc);

            if (laneOrActSet instanceof Lane)
            {
                lane = (Lane) laneOrActSet;
            }
            else if (laneOrActSet instanceof ActivitySet)
            {
                lookForActSet = ((ActivitySet) laneOrActSet).getId();
            }
            else
            {
                break;
            }
        }

        return lane;
    }

    /**
     * Get the parent container of an Activity or Artifact. For Activities inside
     * an ActivitySet, returns the ActivitySet. For Activities/Artifacts in a Lane,
     * returns the Lane (via NodeGraphicsInfo.laneId lookup).
     *
     * @param eObject the Activity or Artifact
     * @return the containing Lane or ActivitySet, or null
     */
    public static EObject getContainer(EObject eObject)
    {
        EObject container = null;

        if (eObject instanceof Activity)
        {
            FlowContainer actContainer = ((Activity) eObject).getFlowContainer();
            if (actContainer instanceof ActivitySet)
            {
                container = actContainer;
            }
        }

        if (container == null)
        {
            NodeGraphicsInfo gi = null;

            if (eObject instanceof Activity)
            {
                gi = getNodeGraphicsInfo((Activity) eObject);
            }
            else if (eObject instanceof Artifact)
            {
                gi = getNodeGraphicsInfo((Artifact) eObject);
            }

            if (gi != null)
            {
                String laneId = gi.getLaneId();
                com.tibco.xpd.xpdl2.Package pkg = getPackage(eObject);
                if (pkg != null && laneId != null)
                {
                    EObject laneOrActSet = pkg.findNamedElement(laneId);
                    if (laneOrActSet instanceof Lane || laneOrActSet instanceof ActivitySet)
                    {
                        container = laneOrActSet;
                    }
                }
            }
        }

        return container;
    }

    /**
     * Return the XPDL Package that the given object belongs to.
     *
     * @param any any model object
     * @return the Package, or null
     */
    public static com.tibco.xpd.xpdl2.Package getPackage(EObject any)
    {
        EObject pkg = any;
        while (pkg != null && !(pkg instanceof com.tibco.xpd.xpdl2.Package))
        {
            pkg = pkg.eContainer();
        }
        return (com.tibco.xpd.xpdl2.Package) pkg;
    }

    /**
     * Find the embedded subprocess activity that references a given ActivitySet.
     *
     * @param process      the process
     * @param activitySetId the ID of the ActivitySet
     * @return the Activity containing the BlockActivity, or null
     */
    public static Activity getEmbSubProcActivityForActSet(Process process, String activitySetId)
    {
        if (process == null || activitySetId == null)
        {
            return null;
        }
        for (Activity activity : getAllActivitiesInProc(process))
        {
            if (activity.getBlockActivity() != null
                    && activitySetId.equals(activity.getBlockActivity().getActivitySetId()))
            {
                return activity;
            }
        }
        return null;
    }

    /**
     * Get all activities in a process, including those in nested ActivitySets.
     *
     * @param process the process
     * @return list of all activities
     */
    public static List<Activity> getAllActivitiesInProc(Process process)
    {
        if (process == null)
        {
            return Collections.emptyList();
        }
        List<Activity> allActivities = new ArrayList<>();

        // Activities directly in the process
        EList<Activity> processActivities = process.getActivities();
        if (processActivities != null)
        {
            allActivities.addAll(processActivities);
        }

        // Activities in ActivitySets (embedded subprocesses)
        EList<ActivitySet> activitySets = process.getActivitySets();
        if (activitySets != null)
        {
            for (ActivitySet actSet : activitySets)
            {
                EList<Activity> setActivities = actSet.getActivities();
                if (setActivities != null)
                {
                    allActivities.addAll(setActivities);
                }
            }
        }
        return allActivities;
    }

    /**
     * Get all artifacts in a process.
     *
     * @param process the process
     * @return list of all artifacts
     */
    public static List<Artifact> getAllArtifactsInProcess(Process process)
    {
        if (process == null || process.getPackage() == null)
        {
            return Collections.emptyList();
        }
        List<Artifact> result = new ArrayList<>();
        EList<Artifact> artifacts = process.getPackage().getArtifacts();
        if (artifacts != null)
        {
            result.addAll(artifacts);
        }
        return result;
    }

    /**
     * Get all associations in a process.
     *
     * @param process the process
     * @return list of all associations
     */
    public static List<Association> getAllAssociationsInProc(Process process)
    {
        if (process == null || process.getPackage() == null)
        {
            return Collections.emptyList();
        }
        List<Association> result = new ArrayList<>();
        EList<Association> associations = process.getPackage().getAssociations();
        if (associations != null)
        {
            result.addAll(associations);
        }
        return result;
    }

    /**
     * Get outgoing transitions from a given activity ID.
     *
     * @param activityId    the source activity ID
     * @param flowContainer the flow container (Process or ActivitySet)
     * @return list of outgoing transitions
     */
    public static List<Transition> getOutgoingTransitions(String activityId,
            FlowContainer flowContainer)
    {
        if (activityId == null || flowContainer == null)
        {
            return Collections.emptyList();
        }
        List<Transition> outgoing = new ArrayList<>();
        EList<Transition> transitions = flowContainer.getTransitions();
        if (transitions != null)
        {
            for (Transition t : transitions)
            {
                if (activityId.equals(t.getFrom()))
                {
                    outgoing.add(t);
                }
            }
        }
        return outgoing;
    }

    /**
     * Get the ConnectorGraphicsInfo for the default connection info tool.
     *
     * @param connector the graphical connector (Transition, Association, or MessageFlow)
     * @return the ConnectorGraphicsInfo, or null
     */
    public static ConnectorGraphicsInfo getConnectorGraphicsInfo(GraphicalConnector connector)
    {
        return getConnectorGraphicsInfo(connector, null);
    }

    /**
     * Get the ConnectorGraphicsInfo for a specific tool suffix.
     *
     * @param connector   the graphical connector (Transition, Association, or MessageFlow)
     * @param toolIdSuffix the tool ID suffix (e.g., "LiteralPoints"), or null for default
     * @return the ConnectorGraphicsInfo, or null
     */
    public static ConnectorGraphicsInfo getConnectorGraphicsInfo(GraphicalConnector connector,
            String toolIdSuffix)
    {
        if (connector == null)
        {
            return null;
        }
        if (toolIdSuffix != null)
        {
            return connector.getConnectorGraphicsInfoForTool(
                    STUDIO_SPECIFIC_TOOL_ID + "." + toolIdSuffix);
        }
        else
        {
            ConnectorGraphicsInfo cgi = connector.getConnectorGraphicsInfoForTool(
                    STUDIO_SPECIFIC_TOOL_ID + "." + CONNECTION_INFO_IDSUFFIX);
            if (cgi != null)
            {
                return cgi;
            }
            return connector.getConnectorGraphicsInfoForTool(STUDIO_SPECIFIC_TOOL_ID);
        }
    }

    /**
     * Check if an activity is an event subprocess (via xpdExtension attribute).
     *
     * @param activity the activity to check
     * @return true if it is an event subprocess
     */
    public static boolean isEventSubProcess(Activity activity)
    {
        if (activity == null || activity.getBlockActivity() == null)
        {
            return false;
        }
        Object isEventSubProc = getOtherAttribute(activity,
                XpdExtensionPackage.eINSTANCE.getDocumentRoot_IsEventSubProcess());
        return Boolean.TRUE.equals(isEventSubProc);
    }

    /**
     * Get the ActivitySet for an embedded sub-process activity.
     *
     * @param embeddedSubProcAct the embedded sub-process activity
     * @return the ActivitySet for the sub-process content, or null
     */
    public static ActivitySet getEmbeddedSubProcessActivitySet(Activity embeddedSubProcAct)
    {
        if (embeddedSubProcAct == null)
        {
            return null;
        }
        Process process = embeddedSubProcAct.getProcess();
        if (process != null)
        {
            com.tibco.xpd.xpdl2.BlockActivity ba = embeddedSubProcAct.getBlockActivity();
            if (ba != null && ba.getActivitySetId() != null)
            {
                return process.getActivitySet(ba.getActivitySetId());
            }
        }
        return null;
    }

    /**
     * Get all artifacts belonging to an embedded sub-process activity's content.
     * Artifacts are stored at Package level with laneId matching the ActivitySet ID.
     * INTENTIONALLY excludes recursion into child sub-processes.
     *
     * @param embeddedSubProc the embedded sub-process activity
     * @return list of artifacts in the sub-process
     */
    public static List<Artifact> getArtifactsInEmbeddedSubProc(Activity embeddedSubProc)
    {
        List<Artifact> artifactsInEmbeddedSubProcAct = new ArrayList<>();

        ActivitySet actSet = getEmbeddedSubProcessActivitySet(embeddedSubProc);

        if (actSet != null)
        {
            String actSetId = actSet.getId();

            com.tibco.xpd.xpdl2.Package pkg = getPackage(embeddedSubProc);

            if (pkg != null)
            {
                EList<Artifact> artifacts = pkg.getArtifacts();
                if (artifacts != null)
                {
                    for (Artifact art : artifacts)
                    {
                        NodeGraphicsInfo gi = getNodeGraphicsInfo(art);
                        if (gi != null && actSetId.equals(gi.getLaneId()))
                        {
                            artifactsInEmbeddedSubProcAct.add(art);
                        }
                    }
                }
            }
        }
        return artifactsInEmbeddedSubProcAct;
    }
}

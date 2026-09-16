/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.util;

import org.eclipse.emf.ecore.EObject;

import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.Implementation;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.Task;

/**
 * Extracted utility methods from {@code com.tibco.xpd.analyst.resources.xpdl2.utils.DecisionFlowUtil}.
 *
 * <p>Contains only the method used by GAT generation to identify decision table tasks.
 * Pure EMF model navigation with no Eclipse Platform dependency.</p>
 *
 * @see Section 5.4 of the design document
 */
public final class GatDecisionFlowUtil
{
    private static final String DECISION_SERVICE_IMPL_TYPE = "DecisionService";

    private GatDecisionFlowUtil()
    {
        // Static utility class
    }

    /**
     * Check if an activity is a decision table task. This requires both that the
     * TaskService has ImplementationType "DecisionService" AND that the activity
     * is inside a Decision Flow process (not a regular BPM process).
     *
     * @param activity the activity to check
     * @return true if this is a decision table task
     */
    public static boolean isDecisionTableTask(Activity activity)
    {
        if (activity == null)
        {
            return false;
        }
        if (isDecisionServiceTaskImplementation(activity))
        {
            return isDecisionsContent(activity);
        }
        return false;
    }

    private static boolean isDecisionServiceTaskImplementation(Activity activity)
    {
        Implementation impl = activity.getImplementation();
        if (impl instanceof Task)
        {
            Task task = (Task) impl;
            if (task.getTaskService() != null)
            {
                String extId = (String) GatModelUtil.getOtherAttribute(
                        task.getTaskService(),
                        XpdExtensionPackage.eINSTANCE
                                .getDocumentRoot_ImplementationType());
                if (DECISION_SERVICE_IMPL_TYPE.equals(extId))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isDecisionsContent(EObject eObject)
    {
        EObject toTest = eObject;
        while (toTest != null)
        {
            if (toTest instanceof Process)
            {
                return isDecisionFlow((Process) toTest);
            }
            toTest = toTest.eContainer();
        }
        return false;
    }

    private static boolean isDecisionFlow(Process process)
    {
        if (process == null)
        {
            return false;
        }
        Object modelType = GatModelUtil.getOtherAttribute(process,
                XpdExtensionPackage.eINSTANCE.getDocumentRoot_XpdModelType());
        return modelType != null && "DecisionFlow".equals(modelType.toString());
    }
}

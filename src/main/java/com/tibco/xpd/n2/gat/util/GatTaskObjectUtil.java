/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.util;

import com.tibco.xpd.n2.gat.adapters.TaskType;
import org.eclipse.emf.ecore.EObject;

import com.tibco.xpd.xpdExtension.XpdExtensionPackage;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.ActivitySet;
import com.tibco.xpd.xpdl2.BlockActivity;
import com.tibco.xpd.xpdl2.Implementation;
import com.tibco.xpd.xpdl2.Process;
import com.tibco.xpd.xpdl2.SubFlow;
import com.tibco.xpd.xpdl2.Task;
import com.tibco.xpd.xpdl2.TaskReceive;
import com.tibco.xpd.xpdl2.TaskSend;
import com.tibco.xpd.xpdl2.TaskService;

/**
 * Extracted utility methods from {@code com.tibco.xpd.processeditor.xpdl2.util.TaskObjectUtil}.
 *
 * <p>Contains only the methods used by GAT generation code. All methods are
 * pure EMF model navigation with no Eclipse Platform dependency.</p>
 *
 * <p><b>NOTE:</b> The {@code getSubProcessOrInterface()} method is a simplified
 * version of the original that resolves only same-package references. Cross-package
 * references (those requiring {@code WorkingCopyUtil} / Eclipse workspace) return
 * null.</p>
 *
 * @see Section 5.4 and Appendix C of the design document
 */
public final class GatTaskObjectUtil
{
    private GatTaskObjectUtil()
    {
        // Static utility class
    }

    /**
     * Determines the TaskType for an activity by inspecting its Implementation element.
     *
     * <p>Checks in order:</p>
     * <ol>
     *   <li>Task -> inspects subtypes (TaskManual, TaskReceive, TaskScript, etc.)</li>
     *   <li>SubFlow -> SUBPROCESS</li>
     *   <li>BlockActivity -> EVENT_SUBPROCESS or EMBEDDED_SUBPROCESS</li>
     *   <li>Reference -> REFERENCE</li>
     *   <li>No Implementation (not Route, not Event) -> NONE</li>
     * </ol>
     *
     * @param activity the activity to inspect
     * @return the TaskType, or null if the activity is a Route or Event
     */
    public static TaskType getTaskTypeStrict(Activity activity)
    {
        if (activity == null)
        {
            return null;
        }

        // If it has a Route or Event, it's not a task
        if (activity.getRoute() != null || activity.getEvent() != null)
        {
            return null;
        }

        Implementation impl = activity.getImplementation();
        if (impl instanceof Task)
        {
            Task task = (Task) impl;
            if (task.getTaskManual() != null)
            {
                return TaskType.MANUAL_LITERAL;
            }
            else if (task.getTaskReceive() != null)
            {
                return TaskType.RECEIVE_LITERAL;
            }
            else if (task.getTaskScript() != null)
            {
                return TaskType.SCRIPT_LITERAL;
            }
            else if (task.getTaskSend() != null)
            {
                return TaskType.SEND_LITERAL;
            }
            else if (task.getTaskUser() != null)
            {
                return TaskType.USER_LITERAL;
            }
            else if (task.getTaskService() != null)
            {
                // Check if it's a decision table task
                if (GatDecisionFlowUtil.isDecisionTableTask(activity))
                {
                    return TaskType.DTABLE_LITERAL;
                }
                return TaskType.SERVICE_LITERAL;
            }
            return TaskType.NONE_LITERAL;
        }
        else if (impl instanceof SubFlow)
        {
            return TaskType.SUBPROCESS_LITERAL;
        }
        else if (activity.getBlockActivity() != null)
        {
            if (GatModelUtil.isEventSubProcess(activity))
            {
                return TaskType.EVENT_SUBPROCESS_LITERAL;
            }
            return TaskType.EMBEDDED_SUBPROCESS_LITERAL;
        }

        return TaskType.NONE_LITERAL;
    }

    /**
     * Get the task implementation extension ID for an activity.
     *
     * <p>This is the xpdExtension "ImplementationType" value that identifies
     * the specific implementation kind (e.g., "EmailService", "RestService",
     * "DatabaseService", "GlobalData", "BusinessRules").</p>
     *
     * @param activity the activity to inspect
     * @return the implementation extension ID, or null
     */
    public static String getTaskImplementationExtensionId(Activity activity)
    {
        if (activity == null)
        {
            return null;
        }

        Implementation implementation = activity.getImplementation();

        if (implementation instanceof Task)
        {
            TaskService service = ((Task) implementation).getTaskService();
            if (service != null)
            {
                return (String) GatModelUtil.getOtherAttribute(service,
                        XpdExtensionPackage.eINSTANCE
                                .getDocumentRoot_ImplementationType());
            }

            TaskSend send = ((Task) implementation).getTaskSend();
            if (send != null)
            {
                return (String) GatModelUtil.getOtherAttribute(send,
                        XpdExtensionPackage.eINSTANCE
                                .getDocumentRoot_ImplementationType());
            }

            TaskReceive receive = ((Task) implementation).getTaskReceive();
            if (receive != null)
            {
                return (String) GatModelUtil.getOtherAttribute(receive,
                        XpdExtensionPackage.eINSTANCE
                                .getDocumentRoot_ImplementationType());
            }
        }
        return null;
    }

    /**
     * Get the ActivitySet referenced by a BlockActivity.
     *
     * @param activity the activity containing a BlockActivity
     * @return the referenced ActivitySet, or null
     */
    public static ActivitySet getActivitySet(Activity activity)
    {
        if (activity == null)
        {
            return null;
        }
        BlockActivity blockActivity = activity.getBlockActivity();
        if (blockActivity == null)
        {
            return null;
        }
        String actSetId = blockActivity.getActivitySetId();
        if (actSetId == null)
        {
            return null;
        }
        EObject current = activity;
        while (current != null && !(current instanceof Process))
        {
            current = current.eContainer();
        }
        if (current instanceof Process)
        {
            Process process = (Process) current;
            if (process.getActivitySets() != null)
            {
                for (ActivitySet actSet : process.getActivitySets())
                {
                    if (actSetId.equals(actSet.getId()))
                    {
                        return actSet;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the sub-process or process interface referenced by a call activity.
     *
     * <p>This is a simplified version of the original
     * {@code TaskObjectUtil.getSubProcessOrInterface()} that resolves references
     * within the same XPDL package only. Cross-package references (where the
     * SubFlow's packageRefId points to an external package) cannot be resolved
     * without the Eclipse workspace and will return null.</p>
     *
     * @param activity the call activity (must have a SubFlow implementation)
     * @return the referenced Process or ProcessInterface, or null if unresolvable
     */
    public static EObject getSubProcessOrInterface(Activity activity)
    {
        if (activity == null)
        {
            return null;
        }

        Implementation impl = activity.getImplementation();
        if (!(impl instanceof SubFlow))
        {
            return null;
        }

        SubFlow subFlow = (SubFlow) impl;
        String processId = subFlow.getProcessId();
        if (processId == null || processId.isEmpty())
        {
            return null;
        }

        // Resolve within the current package only
        Process parentProcess = activity.getProcess();
        if (parentProcess == null)
        {
            return null;
        }

        com.tibco.xpd.xpdl2.Package pkg = parentProcess.getPackage();
        if (pkg == null)
        {
            return null;
        }

        // Look up the referenced process by ID within the same package
        Process referencedProcess = pkg.getProcess(processId);
        if (referencedProcess != null)
        {
            return referencedProcess;
        }

        // Cross-package references and ProcessInterface lookups cannot be
        // resolved without Eclipse workspace / ProcessInterfaceUtil
        return null;
    }
}

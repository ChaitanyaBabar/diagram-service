/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibco.xpd.n2.gat.adapters.TaskType;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.util.GatTaskObjectUtil;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.EndEvent;
import com.tibco.xpd.xpdl2.Event;
import com.tibco.xpd.xpdl2.IntermediateEvent;
import com.tibco.xpd.xpdl2.Route;
import com.tibco.xpd.xpdl2.StartEvent;

/**
 * Factory for creating Graphical Audit Trail model generators for GAT {@link GatFlowNodeElement} derived elements
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
@SuppressWarnings("nls")
public class GatFlowNodeGeneratorFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(GatFlowNodeGeneratorFactory.class);

	static GatFlowNodeGenerator createGenerator(BaseGatGenerator parentGatGenerator, Activity xpdlActivity)
	{
		GatFlowNodeGenerator generator = null;

		TaskType taskType = GatTaskObjectUtil.getTaskTypeStrict(xpdlActivity);

		if (taskType != null)
		{
			generator = createTaskGenerator(parentGatGenerator, xpdlActivity, taskType);
		}
		else if (xpdlActivity.getEvent() != null)
		{
			generator = createEventGenerator(parentGatGenerator, xpdlActivity, xpdlActivity.getEvent());
		}
		else if (xpdlActivity.getRoute() != null)
		{
			generator = createGatewayGenerator(parentGatGenerator, xpdlActivity, xpdlActivity.getRoute());
		}

		if (generator == null)
		{
			LOG.error("Unhandled activity type: " + xpdlActivity.getId() + "  (generating empty-task instead)");

			generator = new GatEmptyTaskGenerator(parentGatGenerator, xpdlActivity);
		}

		return generator;

	}

	private static GatFlowNodeGenerator createTaskGenerator(BaseGatGenerator parentGatGenerator, Activity xpdlActivity,
			TaskType taskType)
	{
		if (TaskType.NONE_LITERAL.equals(taskType))
		{
			return new GatEmptyTaskGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.USER_LITERAL.equals(taskType))
		{
			return new GatUserTaskGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.SUBPROCESS_LITERAL.equals(taskType))
		{
			return new GatCallActivityGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.SCRIPT_LITERAL.equals(taskType))
		{
			return new GatScriptTaskGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.SERVICE_LITERAL.equals(taskType))
		{
			String implementationExtensionId = GatTaskObjectUtil.getTaskImplementationExtensionId(xpdlActivity);

			if ("EmailService".equals(implementationExtensionId))
			{
				return new GatEmailTaskGenerator(parentGatGenerator, xpdlActivity);
			}
			else if ("RestService".equals(implementationExtensionId))
			{
				return new GatServiceInvokeTaskGenerator(parentGatGenerator, xpdlActivity);
			}
			else if ("GlobalData".equals(implementationExtensionId))
			{
				return new GatCaseTaskGenerator(parentGatGenerator, xpdlActivity);
			}
			else if ("DatabaseService".equals(implementationExtensionId))
			{
				return new GatDatabaseTaskGenerator(parentGatGenerator, xpdlActivity);
			}
			else if ("BusinessRules".equals(implementationExtensionId))
			{
				return new GatBusinessRuleTaskGenerator(parentGatGenerator, xpdlActivity);
			}
		}
		else if (TaskType.SEND_LITERAL.equals(taskType))
		{
			return new GatSendTaskGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.RECEIVE_LITERAL.equals(taskType))
		{
			return new GatReceiveTaskGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.EMBEDDED_SUBPROCESS_LITERAL.equals(taskType))
		{
			return new GatSubProcessGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (TaskType.EVENT_SUBPROCESS_LITERAL.equals(taskType))
		{
			return new GatSubProcessGenerator(parentGatGenerator, xpdlActivity);
		}

		return null;
	}

	private static GatFlowNodeGenerator createEventGenerator(BaseGatGenerator parentGatGenerator, Activity xpdlActivity,
			Event event)
	{

		if (event instanceof StartEvent)
		{
			return new GatStartEventGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (event instanceof EndEvent)
		{
			return new GatEndEventGenerator(parentGatGenerator, xpdlActivity);
		}
		else if (event instanceof IntermediateEvent)
		{
			return new GatIntermediateEventGenerator(parentGatGenerator, xpdlActivity);
		}

		return null;
	}

	private static GatFlowNodeGenerator createGatewayGenerator(BaseGatGenerator parentGatGenerator,
			Activity xpdlActivity, Route route)
	{
		return new GatGatewayGenerator(parentGatGenerator, xpdlActivity);
	}

}

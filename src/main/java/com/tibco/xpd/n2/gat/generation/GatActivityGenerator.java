/**
 * Copyright (c) 2026. Cloud Software Group, Inc. All Rights Reserved. Confidential & Proprietary
 */

package com.tibco.xpd.n2.gat.generation;

import com.tibco.xpd.n2.gat.model.GatActivityElement;
import com.tibco.xpd.n2.gat.model.GatFlowNodeElement;
import com.tibco.xpd.n2.gat.model.GatMultiInstanceLoopCharacteristicsElement;
import com.tibco.xpd.n2.gat.model.GatStandardLoopCharacteristicsElement;
import com.tibco.xpd.xpdl2.Activity;
import com.tibco.xpd.xpdl2.Loop;
import com.tibco.xpd.xpdl2.LoopMultiInstance;
import com.tibco.xpd.xpdl2.LoopType;
import com.tibco.xpd.xpdl2.MIOrderingType;

/**
 * Base class for all Graphical Audit Trail Activity generators.
 *
 * @author Sid Allway
 * @since 18 Dec 2025
 */
public abstract class GatActivityGenerator extends GatFlowNodeGenerator
{
	/**
	 * Constructor
	 *
	 * @param parent
	 */
	public GatActivityGenerator(BaseGatGenerator parent, Activity xpdlActivity)
	{
		super(parent, xpdlActivity);
	}

	/**
	 * Generate the appropriate Graphical Audit Trail Activity-derived model element for the xpdlActivity;
	 *
	 * @param xpdlActivity
	 * @return
	 */
	protected abstract GatActivityElement generateActivity(Activity xpdlActivity);

	/**
	 * @see com.tibco.xpd.n2.gat.generation.GatFlowNodeGenerator#generateFlowNode(com.tibco.xpd.xpdl2.Activity)
	 *
	 * @param xpdlActivity
	 * @return
	 */
	@Override
	public final GatFlowNodeElement generateFlowNode(Activity xpdlActivity)
	{
		/*
		 * Generate the Activity model element
		 */
		GatActivityElement gatActivityElement = generateActivity(xpdlActivity);

		/*
		 * Perform any model generation that applies to GAT Activity-derived elements
		 */
		/* Loop characteristics */
		Loop loop = xpdlActivity.getLoop();

		if (loop != null)
		{
			if (LoopType.MULTI_INSTANCE_LITERAL.equals(loop.getLoopType()))
			{
				/*
				 * Multi-instance loop (parallel and sequential)
				 */
				GatMultiInstanceLoopCharacteristicsElement gatMultiInstanceLoop = new GatMultiInstanceLoopCharacteristicsElement();

				gatActivityElement.setMultiInstanceLoopCharacteristics(gatMultiInstanceLoop);

				LoopMultiInstance loopMultiInstance = loop.getLoopMultiInstance();

				if (loopMultiInstance != null
						&& MIOrderingType.SEQUENTIAL_LITERAL.equals(loopMultiInstance.getMIOrdering()))
				{
					gatMultiInstanceLoop.setIsSequential(Boolean.TRUE);
				}
				else
				{
					gatMultiInstanceLoop.setIsSequential(Boolean.FALSE);
				}

			}
			else
			{
				/*
				 * Standard loop
				 */
				GatStandardLoopCharacteristicsElement gatStandardLoop = new GatStandardLoopCharacteristicsElement();

				gatActivityElement.setStandardLoopCharacteristics(gatStandardLoop);
			}
		}

		return gatActivityElement;
	}
}

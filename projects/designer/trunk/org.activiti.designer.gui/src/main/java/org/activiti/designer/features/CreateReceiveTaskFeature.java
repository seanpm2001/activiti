package org.activiti.designer.features;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.bpmn2.model.ReceiveTask;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateReceiveTaskFeature extends AbstractCreateFastBPMNFeature {
	
	public static final String FEATURE_ID_KEY = "receivetask";

	public CreateReceiveTaskFeature(IFeatureProvider fp) {
		super(fp, "ReceiveTask", "Add receive task");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
	  Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    return (context.getTargetContainer() instanceof Diagram || parentObject instanceof SubProcess);
	}

	@Override
	public Object[] create(ICreateContext context) {
		ReceiveTask newReceiveTask = new ReceiveTask();
		newReceiveTask.setId(getNextId());
		setName("Receive Task", newReceiveTask, context);
		
		Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof SubProcess) {
      ((SubProcess) parentObject).getFlowElements().add(newReceiveTask);
    } else {
    	ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).addFlowElement(newReceiveTask);
    }
		
    addGraphicalContent(newReceiveTask, context);
		return new Object[] { newReceiveTask };
	}
	
	@Override
	public String getCreateImageId() {
		return ActivitiImageProvider.IMG_RECEIVETASK;
	}

	@Override
	protected String getFeatureIdKey() {
		return FEATURE_ID_KEY;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeatureClass() {
		return new ReceiveTask().getClass();
	}

}

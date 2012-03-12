package org.activiti.designer.property;

import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoMailTask;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class PropertyMailTaskFilter extends AbstractPropertySectionFilter {
	
	@Override
	protected boolean accept(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		if (bo instanceof MailTask && bo instanceof AlfrescoMailTask == false) {
			return true;
		}
		return false;
	}

}

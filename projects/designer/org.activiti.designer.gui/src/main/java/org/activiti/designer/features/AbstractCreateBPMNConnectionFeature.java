/**
 * 
 */
package org.activiti.designer.features;

import org.activiti.designer.util.ActivitiUiUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;

/**
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.0
 * 
 */
public abstract class AbstractCreateBPMNConnectionFeature extends AbstractCreateConnectionFeature {

  public AbstractCreateBPMNConnectionFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  protected abstract String getFeatureIdKey();

  @SuppressWarnings("rawtypes")
  protected abstract Class getFeatureClass();

  protected String getNextId() {
    return ActivitiUiUtil.getNextId(getFeatureClass(), getFeatureIdKey(), getDiagram());
  }

}

package org.activiti.cycle.impl.mimetype;

import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;

/**
 * Text Mimetype
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class JavaScriptMimeType extends AbstractMimeType {

  public String getName() {
    return "Java Script";
  }
  
  public String getContentType() {
    return "application/javascript";
  }
  
  public String[] getCommonFileExtensions() {
    return new String[] { "js" };
  }

}

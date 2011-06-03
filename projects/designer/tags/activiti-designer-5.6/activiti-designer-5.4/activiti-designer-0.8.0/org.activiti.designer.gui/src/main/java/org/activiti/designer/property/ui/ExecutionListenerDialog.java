package org.activiti.designer.property.ui;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class ExecutionListenerDialog extends AbstractListenerDialog {
	
	private boolean isSequenceFlow;

	public ExecutionListenerDialog(Shell parent, TableItem[] fieldList, boolean isSequenceFlow) {
    super(parent, fieldList);
    this.isSequenceFlow = isSequenceFlow;
  }

  public ExecutionListenerDialog(Shell parent, TableItem[] fieldList, boolean isSequenceFlow, 
          String savedImplementationType, String savedImplementation, String savedEventName, String savedFields) {
    
    super(parent, fieldList, savedImplementationType, savedImplementation, savedEventName, savedFields);
    this.isSequenceFlow = isSequenceFlow;
  }

  @Override
  protected String[] getEventList() {
    if(isSequenceFlow) {
      return new String[] {"take"};
    } else {
      return new String[] {"start", "end"};
    }
  }

  @Override
  protected String getDefaultEvent() {
    if(isSequenceFlow) {
      return "take";
    } else {
      return "start";
    }
  }

}

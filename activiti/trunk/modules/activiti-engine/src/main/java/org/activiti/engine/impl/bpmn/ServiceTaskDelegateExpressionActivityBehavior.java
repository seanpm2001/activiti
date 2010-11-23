/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.bpmn;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.JavaDelegation;
import org.activiti.engine.impl.el.Expression;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;


/**
 * @author Joram Barrez
 */
public class ServiceTaskDelegateExpressionActivityBehavior extends AbstractBpmnActivity {
  
  protected Expression expression;
  
  public ServiceTaskDelegateExpressionActivityBehavior(Expression expression) {
    this.expression = expression;
  }
  
  public void execute(ActivityExecution execution) throws Exception {
    
    // Note: we can't cache the result of the expression, because the
    // execution can change: eg. delegateExpression='${mySpringBeanFactory.randomSpringBean()}'
    Object delegate = expression.getValue(execution);
    
    if (delegate instanceof ActivityBehavior) {
      ((ActivityBehavior) delegate).execute(execution);
    } else if (delegate instanceof JavaDelegation) {
      ((JavaDelegation) delegate).execute(execution);
    } else {
      throw new ActivitiException("Delegate expression " + expression 
              + " did not resolve to an implementation of " + ActivityBehavior.class 
              + " nor " + JavaDelegation.class);
    }
    
    leave(execution);
  }

}

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
package org.activiti.engine.impl.pvm.runtime;

import java.util.logging.Logger;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.pvm.PvmException;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.runtime.MessageEntity;


/**
 * @author Tom Baeyens
 */
public class AtomicOperationActivityExecute implements AtomicOperation {
  
  private static Logger log = Logger.getLogger(AtomicOperationActivityExecute.class.getName());
  
  public void execute(InterpretableExecution execution) {
    ActivityImpl activity = (ActivityImpl) execution.getActivity();
    
    if ("async".equals(activity.getContinuation())) {
        MessageEntity me = new MessageEntity();
        me.setExecutionId(execution.getId());
        //TODO RKU me.setQueue((String)activity.getProperty("name"));
        me.setProcessInstanceId(execution.getId());
        me.setJobHandlerType("async-cont");
        CommandContext
        .getCurrent()
        .getDbSqlSession()
        .insert(me);
    } else {
    
      ActivityBehavior activityBehavior = activity.getActivityBehavior();
      if (activityBehavior==null) {
        throw new PvmException("no behavior specified in "+activity);
      }

      log.fine(execution+" executes "+activity+": "+activityBehavior.getClass().getName());
    
      try {
        activityBehavior.execute(execution);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new PvmException("couldn't execute activity <"+activity.getProperty("type")+" id=\""+activity.getId()+"\" ...>: "+e.getMessage(), e);
      }
    }
  }
}

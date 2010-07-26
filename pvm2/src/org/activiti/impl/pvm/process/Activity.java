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

package org.activiti.impl.pvm.process;

import java.util.ArrayList;
import java.util.List;

import org.activiti.impl.pvm.activity.ActivityBehaviour;



/**
 * @author Tom Baeyens
 */
public class Activity extends Scope {

  protected List<Transition> outgoingTransitions = new ArrayList<Transition>();
  protected ActivityBehaviour activityBehaviour;
  protected Scope parent;
  
  public List<Transition> getOutgoingTransitions() {
    return outgoingTransitions;
  }

  public void setOutgoingTransitions(List<Transition> outgoingTransitions) {
    this.outgoingTransitions = outgoingTransitions;
  }
  
  public ActivityBehaviour getActivityBehaviour() {
    return activityBehaviour;
  }

  public void setActivityBehaviour(ActivityBehaviour activityBehaviour) {
    this.activityBehaviour = activityBehaviour;
  }

  public Scope getParent() {
    return parent;
  }

  public void setParent(Scope parent) {
    this.parent = parent;
  }
}

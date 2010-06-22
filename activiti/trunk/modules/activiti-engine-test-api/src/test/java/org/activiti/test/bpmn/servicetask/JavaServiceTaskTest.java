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
package org.activiti.test.bpmn.servicetask;

import static org.junit.Assert.assertEquals;

import org.activiti.test.ActivitiTestCase;
import org.activiti.test.ProcessDeclared;
import org.junit.Test;

/**
 * @author Joram Barrez
 */
public class JavaServiceTaskTest extends ActivitiTestCase {

  @Test
  @ProcessDeclared
  public void testJavaServiceNoParamsOrResult() {
    processEngineBuilder.getProcessService().startProcessInstanceByKey("javaServiceNoParamsOrResult");
    assertEquals(1, Counter.COUNTER);
  }

}

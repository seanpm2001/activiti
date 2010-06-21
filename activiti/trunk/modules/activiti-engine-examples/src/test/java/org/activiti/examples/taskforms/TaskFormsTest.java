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
package org.activiti.examples.taskforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.Deployment;
import org.activiti.Task;
import org.activiti.test.ActivitiTestCase;
import org.activiti.test.ProcessDeclared;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class TaskFormsTest extends ActivitiTestCase {

  @Before
  public void setUp() throws Exception {
    identityService.saveUser(identityService.newUser("fozzie"));
    identityService.saveGroup(identityService.newGroup("management"));
    identityService.createMembership("fozzie", "management");
  }

  @After
  public void tearDown() throws Exception {
    identityService.deleteGroup("management");
    identityService.deleteUser("fozzie");
  }

  private void deployVacationRequestProcess() {
    Deployment deployment = processService.createDeployment().addClasspathResource("org/activiti/examples/taskforms/VacationRequest.bpmn20.xml")
            .addClasspathResource("org/activiti/examples/taskforms/approve.form").addClasspathResource("org/activiti/examples/taskforms/request.form")
            .addClasspathResource("org/activiti/examples/taskforms/adjustRequest.form").deploy();

    registerDeployment(deployment.getId());
  }

  @Test
  public void testTaskFormsWithVacationRequestProcess() {
    deployVacationRequestProcess();

    // Get start form
    Object startForm = processService.getStartFormByKey("vacationRequest");
    assertNotNull(startForm);

    // Define variables that would be filled in through the form
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("employeeName", "kermit");
    parameters.put("numberOfDays", "4");
    parameters.put("vacationMotivation", "I'm tired");
    processService.startProcessInstanceByKey("vacationRequest", parameters);

    // Management should now have a task assigned to them
    Task task = taskService.createTaskQuery().candidateGroup("management").singleResult();
    assertEquals("Vacation request by kermit", task.getDescription());
    Object taskForm = taskService.getTaskForm(task.getId());
    assertNotNull(taskForm);

  }

  @Test
  @ProcessDeclared
  public void testTaskFormUnavailable() {
    assertNull(processService.getStartFormByKey("noStartOrTaskForm"));

    processService.startProcessInstanceByKey("noStartOrTaskForm");
    Task task = taskService.createTaskQuery().singleResult();
    assertNull(taskService.getTaskForm(task.getId()));
  }

}

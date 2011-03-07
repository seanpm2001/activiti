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

package org.activiti.engine.test.history;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;


/**
 * @author Tom Baeyens
 * @author Frederik Heremans
 */
public class HistoricTaskInstanceTest extends PluggableActivitiTestCase {

  @Deployment
  public void testHistoricTaskInstance() {
    String processInstanceId = runtimeService.startProcessInstanceByKey("HistoricTaskInstanceTest").getId();
    
    // Set priority to non-default value
    Task runtimeTask = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    runtimeTask.setPriority(1234);
    taskService.saveTask(runtimeTask);
    
    String taskId = runtimeTask.getId();
    String taskDefinitionKey = runtimeTask.getTaskDefinitionKey();
    
    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().singleResult();
    assertEquals(taskId, historicTaskInstance.getId());
    assertEquals(1234, historicTaskInstance.getPriority());
    assertEquals("Clean up", historicTaskInstance.getName());
    assertEquals("Schedule an engineering meeting for next week with the new hire.", historicTaskInstance.getDescription());
    assertEquals("kermit", historicTaskInstance.getAssignee());
    assertEquals(taskDefinitionKey, historicTaskInstance.getTaskDefinitionKey());
    assertNull(historicTaskInstance.getEndTime());
    assertNull(historicTaskInstance.getDurationInMillis());
    
    runtimeService.setVariable(processInstanceId, "deadline", "yesterday");
    
    taskService.complete(taskId);
    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().count());

    historicTaskInstance = historyService.createHistoricTaskInstanceQuery().singleResult();
    assertEquals(taskId, historicTaskInstance.getId());
    assertEquals(1234, historicTaskInstance.getPriority());
    assertEquals("Clean up", historicTaskInstance.getName());
    assertEquals("Schedule an engineering meeting for next week with the new hire.", historicTaskInstance.getDescription());
    assertEquals("kermit", historicTaskInstance.getAssignee());
    assertEquals(TaskEntity.DELETE_REASON_COMPLETED, historicTaskInstance.getDeleteReason());
    assertEquals(taskDefinitionKey, historicTaskInstance.getTaskDefinitionKey());
    assertNotNull(historicTaskInstance.getEndTime());
    assertNotNull(historicTaskInstance.getDurationInMillis());
    
    historyService.deleteHistoricTaskInstance(taskId);

    assertEquals(0, historyService.createHistoricTaskInstanceQuery().count());
  }
  
  public void testDeleteHistoricTaskInstance() throws Exception {
    try {
      historyService.deleteHistoricTaskInstance("unexistingId");
      fail("Excpetion expected when deleting unexisting historic task");
    } catch(ActivitiException ae) {
      // Expected exception
    }
  }
  
  @Deployment
  public void testHistoricTaskInstanceQuery() {
    // First instance is finished
    ProcessInstance finishedInstance = runtimeService.startProcessInstanceByKey("HistoricTaskQueryTest");
    
    // Set priority to non-default value
    Task task = taskService.createTaskQuery().processInstanceId(finishedInstance.getId()).singleResult();
    task.setPriority(1234);
    taskService.saveTask(task);
    
    // Complete the task
    String taskId = task.getId();
    taskService.complete(taskId);
    
    // Task id
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskId(taskId).count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskId("unexistingtaskid").count());
    
    // Name
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskName("Clean up").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskName("unexistingname").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskNameLike("Clean u%").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskNameLike("%lean up").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskNameLike("%lean u%").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskNameLike("%unexistingname%").count());
    
    
    // Description
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskDescription("Historic task description").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskDescription("unexistingdescription").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskDescriptionLike("%task description").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskDescriptionLike("Historic task %").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskDescriptionLike("%task%").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskDescriptionLike("%unexistingdescripton%").count());
    
    // Execution id
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().executionId(finishedInstance.getId()).count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().executionId("unexistingexecution").count());
    
    // Process instance id
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().processInstanceId(finishedInstance.getId()).count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().processInstanceId("unexistingid").count());
    
    // Process definition id
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().processDefinitionId(finishedInstance.getProcessDefinitionId()).count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().processDefinitionId("unexistingdefinitionid").count());
    
    // Assignee
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskAssignee("kermit").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskAssignee("johndoe").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%ermit").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("kermi%").count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%ermi%").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%johndoe%").count());
    
    // Delete reason
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskDeleteReason(TaskEntity.DELETE_REASON_COMPLETED).count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskDeleteReason("deleted").count());
    
    // Task definition ID
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskDefinitionKey("task").count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskDefinitionKey("unexistingkey").count());
    
    // Task priority
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().taskPriority(1234).count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().taskPriority(5678).count());
    
    // Finished and Unfinished - Add anther other instance that has a running task (unfinished)
    runtimeService.startProcessInstanceByKey("HistoricTaskQueryTest");
    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().finished().count());
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().unfinished().count());
  }
  
  @Deployment
  public void testHistoricTaskInstanceQueryProcessFinished() {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("TwoTaskHistoricTaskQueryTest");
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    
    // Running task on running process should be available
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().processUnfinished().count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().processFinished().count());
    
    // Finished and running task on running process should be available
    taskService.complete(task.getId());
    assertEquals(2, historyService.createHistoricTaskInstanceQuery().processUnfinished().count());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().processFinished().count());
    
    // 2 finished tasks are found for finished process after completing last task of process
    task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
    taskService.complete(task.getId());
    assertEquals(0, historyService.createHistoricTaskInstanceQuery().processUnfinished().count());
    assertEquals(2, historyService.createHistoricTaskInstanceQuery().processFinished().count());
  }
  
  @Deployment
  public void testHistoricTaskInstanceQuerySorting() {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("HistoricTaskQueryTest");
    
    String taskId = taskService.createTaskQuery().processInstanceId(instance.getId()).singleResult().getId();
    taskService.complete(taskId);
    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByDeleteReason().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByExecutionId().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByHistoricActivityInstanceId().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByHistoricActivityInstanceStartTime().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByProcessDefinitionId().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByProcessInstanceId().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskDescription().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskName().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskDefinitionKey().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskPriority().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskAssignee().asc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskId().asc().count());    
    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByDeleteReason().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByExecutionId().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByHistoricActivityInstanceId().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByHistoricActivityInstanceStartTime().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByProcessDefinitionId().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByProcessInstanceId().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskDescription().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskName().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskDefinitionKey().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskPriority().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskAssignee().desc().count());    
    assertEquals(1, historyService.createHistoricTaskInstanceQuery().orderByTaskId().desc().count());    
  }
  
  public void testInvalidSorting() {
    try {
      historyService.createHistoricTaskInstanceQuery().asc();
      fail();
    } catch (ActivitiException e) {
      
    }
    
    try {
      historyService.createHistoricTaskInstanceQuery().desc();
      fail();
    } catch (ActivitiException e) {
      
    }
    
    try {
      historyService.createHistoricTaskInstanceQuery().orderByProcessInstanceId().list();
      fail();
    } catch (ActivitiException e) {
      
    }
  }
}

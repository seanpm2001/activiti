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
package org.activiti.engine.test.bpmn.event.timer;

import org.activiti.engine.impl.cmd.DeleteJobsCmd;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.repository.DeploymentEntity;
import org.activiti.engine.impl.runtime.JobEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.test.Deployment;

import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;


public class StartTimerEventTest extends PluggableActivitiTestCase {

  @Deployment
  public void testDurationStartTimerEvent() throws Exception {

    // Set the clock fixed
    Date startTime = new Date();

    // After process start, there should be timer created
    JobQuery jobQuery = managementService.createJobQuery();
    assertEquals(1, jobQuery.count());

    // After setting the clock to time '50minutes and 5 seconds', the second timer should fire
    ClockUtil.setCurrentTime(new Date(startTime.getTime() + ((50 * 60 * 1000) + 5000)));
    waitForJobExecutorToProcessAllJobs(5000L, 25L);

    List<ProcessInstance> pi = runtimeService.createProcessInstanceQuery().processDefinitionKey("startTimerEventExample")
        .list();
    assertEquals(1, pi.size());

    assertEquals(0, jobQuery.count());


  }


  @Deployment
  public void testFixedDateStartTimerEvent() throws Exception {

    // After process start, there should be timer created
    JobQuery jobQuery = managementService.createJobQuery();
    assertEquals(1, jobQuery.count());

    ClockUtil.setCurrentTime(DatatypeFactory.newInstance().newXMLGregorianCalendar("2100-11-14T11:12:30").toGregorianCalendar().getTime());
    waitForJobExecutorToProcessAllJobs(5000L, 25L);

    List<ProcessInstance> pi = runtimeService.createProcessInstanceQuery().processDefinitionKey("startTimerEventExample")
        .list();
    assertEquals(1, pi.size());

    assertEquals(0, jobQuery.count());


  }


  @Deployment
  public void testCycleDateStartTimerEvent() throws Exception {
    ClockUtil.setCurrentTime(new Date());

    // After process start, there should be timer created
    JobQuery jobQuery = managementService.createJobQuery();
    assertEquals(1, jobQuery.count());

    moveByMinutes(5);
    ProcessInstanceQuery piq = runtimeService.createProcessInstanceQuery().processDefinitionKey("startTimerEventExample");
    assertEquals(1, piq.count());
    assertEquals(1, jobQuery.count());

    moveByMinutes(5);
    assertEquals(2, piq.count());
    assertEquals(1, jobQuery.count());
    //have to manually delete pending timer
    cleanDB();

  }


  @Deployment
  public void testCycleWithLimitStartTimerEvent() throws Exception {
    ClockUtil.setCurrentTime(new Date());

    // After process start, there should be timer created
    JobQuery jobQuery = managementService.createJobQuery();
    assertEquals(1, jobQuery.count());

    moveByMinutes(5);
    ProcessInstanceQuery piq = runtimeService.createProcessInstanceQuery().processDefinitionKey("startTimerEventExampleCycle");
    assertEquals(1, piq.count());
    assertEquals(1, jobQuery.count());

    moveByMinutes(5);
    assertEquals(2, piq.count());
    assertEquals(0, jobQuery.count());

  }


  //we cannot use waitForExecutor... method since there will always be one job left
  private void moveByMinutes(int minutes) throws Exception {
    ClockUtil.setCurrentTime(new Date(ClockUtil.getCurrentTime().getTime() + ((minutes * 60 * 1000) + 5000)));
    JobExecutor jobExecutor = processEngineConfiguration.getJobExecutor();
    jobExecutor.start();
    Thread.sleep(1000);

    jobExecutor.shutdown();
  }

  private void cleanDB() {
    String jobId = managementService.createJobQuery().singleResult().getId();
    CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
    commandExecutor.execute(new DeleteJobsCmd(jobId));
  }

  @Deployment
  public void testVersionUpgradeShouldCancelJobs() throws Exception {
    ClockUtil.setCurrentTime(new Date());

    // After process start, there should be timer created
    JobQuery jobQuery = managementService.createJobQuery();
    assertEquals(1, jobQuery.count());

    //we deploy new process version, with some small change
    String process = new String(IoUtil.readInputStream(getClass().getResourceAsStream("StartTimerEventTest.testVersionUpgradeShouldCancelJobs.bpmn20.xml"), "")).replaceAll("beforeChange","changed");
    String id = repositoryService.createDeployment().addInputStream("StartTimerEventTest.testVersionUpgradeShouldCancelJobs.bpmn20.xml",
        new ByteArrayInputStream(process.getBytes())).deploy().getId();

    assertEquals(1, jobQuery.count());

    moveByMinutes(5);

    //we check that correct version was started
    String pi = runtimeService.createProcessInstanceQuery().processDefinitionKey("startTimerEventExample").singleResult().getProcessInstanceId();
    assertEquals("changed", runtimeService.getActiveActivityIds(pi).get(0));
    assertEquals(1, jobQuery.count());

    cleanDB();
    repositoryService.deleteDeployment(id, true);
  }

}
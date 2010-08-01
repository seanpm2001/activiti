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
package org.activiti.engine.impl.persistence.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.Job;
import org.activiti.engine.Page;
import org.activiti.engine.ProcessInstance;
import org.activiti.engine.impl.cfg.RuntimeSession;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.runtime.ActivityInstanceEntity;
import org.activiti.engine.impl.persistence.runtime.ByteArrayEntity;
import org.activiti.engine.impl.persistence.runtime.JobImpl;
import org.activiti.engine.impl.persistence.runtime.ProcessInstanceEntity;
import org.activiti.engine.impl.persistence.runtime.TimerImpl;
import org.activiti.engine.impl.persistence.runtime.VariableInstanceEntity;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.pvm.impl.process.ProcessDefinitionImpl;
import org.apache.ibatis.session.RowBounds;

/**
 * @author Joram Barrez
 * @author Tom Baeyens
 */
public class DbRuntimeSession implements Session, RuntimeSession {

  protected DbSqlSession dbSqlSession;

  public DbRuntimeSession() {
    this.dbSqlSession = CommandContext.getCurrentSession(DbSqlSession.class);
  }

  public void insertProcessInstance(ProcessInstanceEntity processInstance) {
    dbSqlSession.insert(processInstance);
  }

  public void deleteProcessInstance(ProcessInstanceEntity processInstance) {
    dbSqlSession.delete(processInstance);
  }

  public void insertActivityInstance(ActivityInstanceEntity activityInstance) {
    dbSqlSession.insert(activityInstance);
  }

  public void deleteActivityInstance(ActivityInstanceEntity activityInstance) {
    dbSqlSession.delete(activityInstance);
  }

  // executions ///////////////////////////////////////////////////////////////

  public ActivityInstanceEntity findActivityInstanceById(String activityInstanceId) {
    return (ActivityInstanceEntity) dbSqlSession.selectOne("selectActivityInsatnceById", activityInstanceId);
  }

  @SuppressWarnings("unchecked")
  public List<ActivityInstanceEntity> findActivityInstancesByParentActivityInstanceId(String parentActivityInstanceId) {
    return dbSqlSession.selectList("ActivityInstancesByParentActivityInstanceId", parentActivityInstanceId);
  }

  @SuppressWarnings("unchecked")
  public List<ProcessInstanceEntity> findProcessInstancesByProcessDefintionId(String processDefinitionId) {
    return dbSqlSession.selectList("selectRootExecutionsForProcessDefinition", processDefinitionId);
  }

  public ProcessInstanceEntity findSubProcessInstance(String superExecutionId) {
    return (ProcessInstanceEntity) dbSqlSession.selectOne("selectSubProcessInstanceBySuperExecutionId", superExecutionId);
  }
  
  public long findProcessInstanceCountByDynamicCriteria(Map<String, Object> params) {
    return (Long) dbSqlSession.selectOne("selectProcessInstanceCountByDynamicCriteria", params);
  }

  @SuppressWarnings("unchecked")
  public List<ProcessInstance> findProcessInstancesByDynamicCriteria(Map<String, Object> params) {
    return dbSqlSession.selectList("selectProcessInstanceByDynamicCriteria", params);
  }

  // variables ////////////////////////////////////////////////////////////////

  public List<VariableInstanceEntity> findVariableInstancessByExecutionId(String executionId) {
    return dbSqlSession.selectList("selectVariablesByExecutionId", executionId);
  }

  public List<VariableInstanceEntity> findVariablesByTaskId(String taskId) {
    return dbSqlSession.selectList("selectVariablesByTaskId", taskId);
  }
  
  @SuppressWarnings("unchecked")
  public byte[] getByteArrayBytes(String byteArrayId) {
   Map<String, Object> temp = (Map) dbSqlSession.selectOne("selectBytesOfByteArray", byteArrayId);
   return (byte[]) temp.get("BYTES_");
  }

  public ByteArrayEntity findByteArrayById(String byteArrayId) {
    return (ByteArrayEntity) dbSqlSession.selectOne("selectByteArrayById", byteArrayId);
  }


  public ProcessDefinitionImpl findProcessDefinitionByDeploymentAndKey(String deploymentId, String processDefinitionKey) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("deploymentId", deploymentId);
    parameters.put("processDefinitionKey", processDefinitionKey);
    return (ProcessDefinitionImpl) dbSqlSession.selectOne("selectProcessDefinitionByDeploymentAndKey", parameters);
  }


  // job /////////////////////////////////////////////////////////////////////

  public JobImpl findJobById(String jobId) {
    return (JobImpl) dbSqlSession.selectOne("selectJob", jobId);
  }

  public List<JobImpl> findJobs() {
    return dbSqlSession.selectList("selectJobs");
  }
  
  public List<JobImpl> findNextJobsToExecute(int maxNrOfJobs) {
    Date now = ClockUtil.getCurrentTime();
    RowBounds rowBounds = new RowBounds(0, maxNrOfJobs);
    return dbSqlSession.selectList("selectNextJobsToExecute", now, rowBounds);
  }

  @SuppressWarnings("unchecked")
  public List<JobImpl> findLockedJobs() {
    return dbSqlSession.selectList("selectLockedJobs");
  }
  
  @SuppressWarnings("unchecked")
  public List<TimerImpl> findUnlockedTimersByDuedate(Date duedate, int nrOfTimers) {
	final String query = "selectUnlockedTimersByDuedate";
	if (nrOfTimers > 0) {
		RowBounds rowBounds = new RowBounds(0,nrOfTimers);
		return dbSqlSession.selectList(statement(query), duedate, rowBounds);
	} else {
		return dbSqlSession.selectList(statement(query), duedate);
	}
  }

  @SuppressWarnings("unchecked")
  public List<TimerImpl> findTimersByExecutionId(String executionId) {
    return dbSqlSession.selectList("selectTimersByExecutionId", executionId);
  }

  @SuppressWarnings("unchecked")
  public List<Job> dynamicFindJobs(Map<String, Object> params, Page page) {
    final String query = "org.activiti.persistence.selectJobByDynamicCriteria";
    if (page == null) {
      return dbSqlSession.selectList(query, params);
    } else {
      return dbSqlSession.selectList(query, params, new RowBounds(page.getOffset(), page.getMaxResults()));
    }
  }

  public long dynamicJobCount(Map<String, Object> params) {
    return (Long) dbSqlSession.selectOne("org.activiti.persistence.selectJobCountByDynamicCriteria", params);
  }

  public void close() {
  }

  public void flush() {
  }

}

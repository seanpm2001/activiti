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

package org.activiti.engine.impl.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.context.Context;


/**
 * @author Tom Baeyens
 */
public class DeploymentCache {

  protected Map<String, ProcessDefinitionEntity> processDefinitionCache = new HashMap<String, ProcessDefinitionEntity>(); 
  protected Map<String, Object> knowledgeBaseCache = new HashMap<String, Object>(); 
  protected List<Deployer> deployers;
  
  public void deploy(DeploymentEntity deployment) {
    for (Deployer deployer: deployers) {
      deployer.deploy(deployment);
    }
  }

  public ProcessDefinitionEntity findDeployedLatestProcessDefinitionByKey(String processDefinitionKey) {
    ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) Context
      .getCommandContext()
      .getProcessDefinitionManager()
      .findLatestProcessDefinitionByKey(processDefinitionKey);
    if (processDefinition==null) {
      throw new ActivitiException("no processes deployed with key '"+processDefinitionKey+"'");
    }
    processDefinition = resolveProcessDefinition(processDefinition);
    return processDefinition;
  }

  protected ProcessDefinitionEntity resolveProcessDefinition(ProcessDefinitionEntity processDefinition) {
    String processDefinitionId = processDefinition.getId();
    String deploymentId = processDefinition.getDeploymentId();
    processDefinition = processDefinitionCache.get(processDefinitionId);
    if (processDefinition==null) {
      DeploymentEntity deployment = Context
        .getCommandContext()
        .getDeploymentManager()
        .findDeploymentById(deploymentId);
      deployment.setNew(false);
      deploy(deployment);
      processDefinition = processDefinitionCache.get(processDefinitionId);
      
      if (processDefinition==null) {
        throw new ActivitiException("deploying "+deploymentId+" didn't put process definition "+processDefinitionId+" in the cache");
      }
    }
    return processDefinition;
  }

  public void addDeployedProcessDefinition(ProcessDefinitionEntity processDefinition) {
    processDefinitionCache.put(processDefinition.getId(), processDefinition);
  }

  // getters and setters //////////////////////////////////////////////////////

  public Map<String, ProcessDefinitionEntity> getProcessDefinitionCache() {
    return processDefinitionCache;
  }
  
  public void setProcessDefinitionCache(Map<String, ProcessDefinitionEntity> processDefinitionCache) {
    this.processDefinitionCache = processDefinitionCache;
  }
  
  public Map<String, Object> getKnowledgeBaseCache() {
    return knowledgeBaseCache;
  }
  
  public void setKnowledgeBaseCache(Map<String, Object> knowledgeBaseCache) {
    this.knowledgeBaseCache = knowledgeBaseCache;
  }
  
  public List<Deployer> getDeployers() {
    return deployers;
  }
  
  public void setDeployers(List<Deployer> deployers) {
    this.deployers = deployers;
  }
}

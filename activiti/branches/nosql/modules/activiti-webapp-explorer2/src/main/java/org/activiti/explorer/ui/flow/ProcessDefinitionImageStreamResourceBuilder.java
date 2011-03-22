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

package org.activiti.explorer.ui.flow;

import java.io.InputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.explorer.Constants;
import org.activiti.explorer.ExplorerApplication;
import org.activiti.explorer.ui.util.InputStreamStreamSource;

import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;



/**
 * Builder that is capable of creating a {@link StreamResource} for a given
 * process-definition, containing the diagram image.
 * 
 * @author Frederik Heremans
 */
public class ProcessDefinitionImageStreamResourceBuilder {
  
  public StreamResource buildStreamResource(ProcessDefinition processDefinition, RepositoryService repositoryService) {
    
    StreamResource imageResource = null;
    
    if(processDefinition.getDiagramResourceName() != null) {
      final InputStream definitionImageStream = repositoryService.getResourceAsStream(
        processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
      
      StreamSource streamSource = new InputStreamStreamSource(definitionImageStream);
      
      // Creating image name based on process-definition ID is fine, since the diagram image cannot
      // be altered once deployed.
      String imageExtension = extractImageExtension(processDefinition.getDiagramResourceName());
      String fileName = processDefinition.getId() + "." + imageExtension;
      
      imageResource = new StreamResource(streamSource, fileName, ExplorerApplication.getCurrent());
    }
    
    return imageResource;
  }

  protected String extractImageExtension(String diagramResourceName) {
    String[] parts = diagramResourceName.split(".");
    if(parts.length > 1) {
      return parts[parts.length - 1];
    }
    return Constants.DEFAULT_DIAGRAM_IMAGE_EXTENSION;
  }
}
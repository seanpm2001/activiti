package org.activiti.rest.api.cycle;

import java.util.Map;

import org.activiti.cycle.impl.processsolution.event.ImplementationDoneEvent;
import org.activiti.cycle.impl.processsolution.event.SpecificationDoneEvent;
import org.activiti.cycle.processsolution.ProcessSolution;
import org.activiti.cycle.processsolution.ProcessSolutionState;
import org.activiti.rest.util.ActivitiRequest;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;

/**
 * Webscript for updating a process solution
 * 
 * @author daniel.meyer@camunda.com
 */
public class ProcessSolutionPut extends ActivitiCycleWebScript {

  @Override
  void execute(ActivitiRequest req, Status status, Cache cache, Map<String, Object> model) {
    String processSolutionId = req.getMandatoryString("processSolutionId");
    String state = req.getMandatoryString("state");

    try {
      ProcessSolution processSolution = processSolutionService.getProcessSolutionById(processSolutionId);
      if (processSolution == null) {
        throw new RuntimeException("ProcessSolution with id '" + processSolutionId + "' not found.");
      }

      ProcessSolutionState newState = ProcessSolutionState.valueOf(state);
      if (!newState.equals(processSolution.getState())) {
        switch (newState) {
        case IN_IMPLEMENTATION:
          eventService.fireEvent(new SpecificationDoneEvent(processSolution));
          break;
        case IN_TESTING:
          eventService.fireEvent(new ImplementationDoneEvent(processSolution));
          break;
        }
      }
      model.put("result", "success");
    } catch (Exception e) {
      throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not update ProcessSolution. " + e.getMessage(), e);
    }
  }
}

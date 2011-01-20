package org.activiti.rest.builder.process;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.rest.builder.BaseJSONObjectBuilder;
import org.activiti.rest.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ProcessInstancesObjectBuilder extends BaseJSONObjectBuilder {

  private ProcessInstanceJSONConverter converter = new ProcessInstanceJSONConverter();

  @SuppressWarnings("unchecked")
  public JSONObject createJsonObject(Object modelObject) throws JSONException {
    Map<String, Object> model = getModelAsMap(modelObject);

    JSONObject result = new JSONObject();
    JSONUtil.putPagingInfo(result, model);

    List<ProcessInstance> definitions = (List<ProcessInstance>) model.get("processInstances");
    JSONArray dataArray = JSONUtil.putNewArray(result, "data");
    for (ProcessInstance processDefinition : definitions) {
      JSONObject jsonTask = converter.getJSONObject(processDefinition);
      dataArray.put(jsonTask);
    }
    return result;
  }
}

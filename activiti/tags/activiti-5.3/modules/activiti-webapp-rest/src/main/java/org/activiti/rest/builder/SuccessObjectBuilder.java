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

package org.activiti.rest.builder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creates an object representing a successful operation, regardless of the
 * content of the model. If the builder is called, there was no exception during
 * the execution of the webscript.
 * 
 * @author Frederik Heremans
 */
public class SuccessObjectBuilder extends BaseJSONObjectBuilder {

  @Override
  public JSONObject createJsonObject(Object model) throws JSONException {
    JSONObject result = new JSONObject();
    result.put("success", true);
    return result;
  }

}

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

package org.activiti.service.impl.rest.impl;

import java.util.Map;


/**
 * @author Tom Baeyens
 */
public abstract class UrlPartMatcher {

  public static UrlPartMatcher createUrlPartMatcher(String urlPart) {
    if (urlPart.startsWith("{") && urlPart.endsWith("}")) {
      return new UrlPartMatcherVariable(urlPart.substring(1, urlPart.length()-1));
    } 
    return new UrlPartMatcherStatic(urlPart);
  }

  public abstract boolean matches(String requestPiece, Map<String, String> urlVariables);
}

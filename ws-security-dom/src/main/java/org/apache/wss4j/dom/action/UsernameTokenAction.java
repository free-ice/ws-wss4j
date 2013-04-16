/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.wss4j.dom.action;

import javax.security.auth.callback.CallbackHandler;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandler;
import org.apache.wss4j.dom.message.WSSecUsernameToken;
import org.w3c.dom.Document;

public class UsernameTokenAction implements Action {
    
    public void execute(WSHandler handler, int actionToDo, Document doc, RequestData reqData)
        throws WSSecurityException {
        String username = reqData.getUsername();
        String password = null;
        if (reqData.getPwType() != null) {
            CallbackHandler callbackHandler = 
                handler.getPasswordCallbackHandler(reqData);
            WSPasswordCallback passwordCallback = 
                handler.getPasswordCB(reqData.getUsername(), actionToDo, callbackHandler, reqData);
            username = passwordCallback.getIdentifier();
            password = passwordCallback.getPassword();
        }

        WSSecUsernameToken builder = new WSSecUsernameToken(reqData.getWssConfig());
        builder.setPasswordType(reqData.getPwType());
        builder.setPasswordsAreEncoded(reqData.getWssConfig().getPasswordsAreEncoded());
        builder.setUserInfo(username, password);

        if (reqData.isAddUsernameTokenNonce()) {
            builder.addNonce();
        }
        
        if (reqData.isAddUsernameTokenCreated()) {
            builder.addCreated();
        }

        builder.build(doc, reqData.getSecHeader());        
    }
}

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

package org.apache.ws.security.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.common.CustomHandler;
import org.apache.ws.security.common.SOAPUtil;
import org.apache.ws.security.common.UsernamePasswordCallbackHandler;
import org.w3c.dom.Document;

import javax.security.auth.callback.CallbackHandler;

/**
 * WS-Security Test Case for the getPassword method in WSHandler.
 * <p/>
 */
public class WSHandlerGetPasswordTest extends org.junit.Assert {
    private static final Log LOG = LogFactory.getLog(WSHandlerGetPasswordTest.class);
    private static final String SOAPMSG = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
        + "<SOAP-ENV:Envelope "
        +   "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        +   "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        +   "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
        +   "<SOAP-ENV:Body>" 
        +       "<add xmlns=\"http://ws.apache.org/counter/counter_port_type\">" 
        +           "<value xmlns=\"\">15</value>" 
        +       "</add>" 
        +   "</SOAP-ENV:Body>" 
        + "</SOAP-ENV:Envelope>";
    
    private CallbackHandler callbackHandler = new UsernamePasswordCallbackHandler();

    /**
     * A unit test for {@link WSHandler#getPassword(String, int, String, String, RequestData)},
     * where the password is obtained from the Message Context.
     */
    @org.junit.Test
    public void
    testGetPasswordRequestContextUnit() throws Exception {
        
        final WSSConfig cfg = WSSConfig.getNewInstance();
        final RequestData reqData = new RequestData();
        reqData.setWssConfig(cfg);
        java.util.Map<String, Object> messageContext = new java.util.TreeMap<String, Object>();
        messageContext.put("password", "securityPassword");
        reqData.setMsgContext(messageContext);
        
        WSHandler handler = new CustomHandler();
        WSPasswordCallback callback = 
            handler.getPassword(
                "alice", 
                WSConstants.UT, 
                "SomeCallbackTag", 
                "SomeCallbackRef",
                reqData
            );
        assertTrue("alice".equals(callback.getIdentifier()));
        assertTrue("securityPassword".equals(callback.getPassword()));
        assertTrue(WSPasswordCallback.USERNAME_TOKEN == callback.getUsage());
    }
    
    /**
     * A WSHandler test for {@link WSHandler#getPassword(String, int, String, String, RequestData)},
     * where the password is obtained from the Message Context.
     */
    @org.junit.Test
    public void
    testGetPasswordRequestContext() throws Exception {
        
        final WSSConfig cfg = WSSConfig.getNewInstance();
        final RequestData reqData = new RequestData();
        reqData.setWssConfig(cfg);
        reqData.setUsername("alice");
        reqData.setPwType(WSConstants.PASSWORD_TEXT);
        java.util.Map<String, Object> messageContext = new java.util.TreeMap<String, Object>();
        messageContext.put("password", "securityPassword");
        reqData.setMsgContext(messageContext);
        
        final java.util.List<Integer> actions = new java.util.ArrayList<Integer>();
        actions.add(new Integer(WSConstants.UT));
        Document doc = SOAPUtil.toSOAPPart(SOAPMSG);
        CustomHandler handler = new CustomHandler();
        handler.send(
            WSConstants.UT, 
            doc, 
            reqData, 
            actions,
            true
        );
        
        String outputString = 
            org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc);
        if (LOG.isDebugEnabled()) {
            LOG.debug(outputString);
        }
        assertTrue(outputString.indexOf("alice") != -1);
        assertTrue(outputString.indexOf("securityPassword") != -1);
    }
    
    /**
     * A test for {@link WSHandler#getPassword(String, int, String, String, RequestData)},
     * where the password is obtained from a Callback Handler, which is placed on the 
     * Message Context using a reference.
     */
    @org.junit.Test
    public void
    testGetPasswordCallbackHandlerRef() throws Exception {
        
        final WSSConfig cfg = WSSConfig.getNewInstance();
        final RequestData reqData = new RequestData();
        reqData.setWssConfig(cfg);
        reqData.setUsername("alice");
        reqData.setPwType(WSConstants.PASSWORD_TEXT);
        java.util.Map<String, Object> messageContext = new java.util.TreeMap<String, Object>();
        messageContext.put(
            WSHandlerConstants.PW_CALLBACK_REF, 
            callbackHandler
        );
        reqData.setMsgContext(messageContext);
        
        final java.util.List<Integer> actions = new java.util.ArrayList<Integer>();
        actions.add(new Integer(WSConstants.UT));
        Document doc = SOAPUtil.toSOAPPart(SOAPMSG);
        CustomHandler handler = new CustomHandler();
        handler.send(
            WSConstants.UT, 
            doc, 
            reqData, 
            actions,
            true
        );
        
        String outputString = 
            org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc);
        if (LOG.isDebugEnabled()) {
            LOG.debug(outputString);
        }
        assertTrue(outputString.indexOf("alice") != -1);
        assertTrue(outputString.indexOf("securityPassword") != -1);
    }
    
}

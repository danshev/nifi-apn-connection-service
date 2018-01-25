/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.apnsconnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnDisabled;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.reporting.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;

import io.netty.util.internal.StringUtil;

@Tags({ "example"})
@CapabilityDescription("Example ControllerService implementation of MyService.")
public class APNSConnectionControllerService extends AbstractControllerService implements APNSConnectionService {
	 private static final Logger log = LoggerFactory.getLogger(APNSConnectionControllerService.class);

    public static final PropertyDescriptor APNS_SERVER = new PropertyDescriptor
            .Builder().name("APNS_SERVER")
            .displayName("APNs Server Endpoint")
            .defaultValue("Development")
            .expressionLanguageSupported(false)
            .allowableValues("Production", "Development")
            .required(true)
            .build();

    public static final PropertyDescriptor APNS_NAME = new PropertyDescriptor
            .Builder().name("APNS_NAME")
            .displayName("Apple Identifier")
            .expressionLanguageSupported(false)
            .description("The unique identifier registered with Apple, typically in reverse DNS format (ex: com.example.app)")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(true)
            .build();

    public static final PropertyDescriptor CERT_FILE = new PropertyDescriptor
            .Builder().name("CERT_FILE")
            .displayName("Certificate File")
            .description("The filepath to your .p12 file (created from the .cert downloaded from Apple)")
            .expressionLanguageSupported(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(true)
            .build();


    public static final PropertyDescriptor CERT_PASSWORD = new PropertyDescriptor
            .Builder().name("CERT_PASSWORD")
            .displayName("Certificate File Password")
            .description("If necessary, the password for the Certificate File")
            .expressionLanguageSupported(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(false)
            .sensitive(true)
            .build();
    
    private static String apns_server = "";
    private static String cert_file = "";
    private static String cert_password = "";
    private static String hostname = "";

    private static final List<PropertyDescriptor> properties;

    static {
        final List<PropertyDescriptor> props = new ArrayList<>();
        props.add(APNS_SERVER);
        props.add(APNS_NAME);
        props.add(CERT_FILE);
        props.add(CERT_PASSWORD);
        properties = Collections.unmodifiableList(props);
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return properties;
    }

    /**
     * @param context
     *            the configuration context
     * @throws InitializationException
     *             if unable to create a database connection
     */
    @OnEnabled
    public void onEnabled(final ConfigurationContext context) throws InitializationException {

        apns_server = context.getProperty(APNS_SERVER).getValue();
        cert_file = context.getProperty(CERT_FILE).getValue();
        cert_password = context.getProperty(CERT_PASSWORD).getValue();
        

        if (StringUtil.isNullOrEmpty(cert_password)) {
        	cert_password = "";
        }
        
        if (apns_server.equals("Production")) {
                hostname = "api.push.apple.com";
        }
        else {
                hostname = "api.development.push.apple.com";
        }
        
        ApnsClient apnsClient = getConnection(); // Try the connection.
        if (apnsClient == null) {
        	log.error("Error: Couldn't connect to APNs.");
        }
    }

    @OnDisabled
    public void shutdown() {

    }

	@Override
	public ApnsClient getConnection() throws ProcessException {
        try {
            final ApnsClient apnsClient = new ApnsClientBuilder()
                    .setClientCredentials(new File(cert_file), cert_password)
                    .setApnsServer(hostname, 443)
                    .build();

            return apnsClient;

        }
        catch (Exception e) {
        	log.error("Error: " + e.getMessage());
        	e.printStackTrace();
        	return null;
        }
	}

}

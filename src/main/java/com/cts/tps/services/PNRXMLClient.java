/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cts.tps.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

/**
 *
 * @author DILIP
 */
public class PNRXMLClient {
    
   

    public static void main(String[] args) throws FileNotFoundException {
       // long startTime = System.currentTimeMillis();

        final Client client = ClientBuilder.newClient(new ClientConfig());
        final String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "\n"
                + "<!--\n"
                + "Licensed to the Apache Software Foundation (ASF) under one\n"
                + "or more contributor license agreements.  See the NOTICE file\n"
                + "distributed with this work for additional information\n"
                + "regarding copyright ownership.  The ASF licenses this file\n"
                + "to you under the Apache License, Version 2.0 (the\n"
                + "\"License\"); you may not use this file except in compliance\n"
                + "with the License.  You may obtain a copy of the License at\n"
                + "\n"
                + "    http://www.apache.org/licenses/LICENSE-2.0\n"
                + "\n"
                + "Unless required by applicable law or agreed to in writing,\n"
                + "software distributed under the License is distributed on an\n"
                + "\"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n"
                + "KIND, either express or implied.  See the License for the\n"
                + "specific language governing permissions and limitations\n"
                + "under the License.";

        //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Properties props = new Properties();
        try {
            InputStream inputs = classLoader.getResourceAsStream("config.properties");          
            props.load(inputs);        
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        int noOfFile=Integer.parseInt(props.getProperty("nooffile"));
        int noOfthreads=Integer.parseInt(props.getProperty("noOfThreads"));
        
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(noOfthreads);

        for (int i = 0; i <= noOfFile; i++) {

           executor.execute(new Runnable() {
                
               @Override
                public void run() {
                    File f = new File(props.getProperty("filename"));
                    FileInputStream fs = null;     
                    try {
                        fs = new FileInputStream(f);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    WebTarget webTarget = client.target(props.getProperty("restpostservice"));
                    Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN);
                    Response response = invocationBuilder.post(Entity.entity(fs, MediaType.MULTIPART_FORM_DATA));
                    System.out.println(response.getStatus());
                }
            });

        }
        
        executor.shutdown();
        
//        long endTime = System.currentTimeMillis();
//     long totalTime = endTime - startTime;
//     long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTime);
//     System.out.println("totalTime.." + timeSeconds);

    }
     

}

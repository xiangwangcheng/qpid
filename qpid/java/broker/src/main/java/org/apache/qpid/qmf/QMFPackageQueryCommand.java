/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.qpid.qmf;

import org.apache.log4j.Logger;

import org.apache.qpid.AMQException;
import org.apache.qpid.server.exchange.Exchange;
import org.apache.qpid.server.message.ServerMessage;
import org.apache.qpid.server.queue.BaseQueue;
import org.apache.qpid.server.registry.IApplicationRegistry;
import org.apache.qpid.server.virtualhost.VirtualHost;
import org.apache.qpid.transport.codec.BBDecoder;

import java.util.Collection;
import java.util.List;

public class QMFPackageQueryCommand extends QMFCommand
{

    private static final Logger _qmfLogger = Logger.getLogger("qpid.qmf");

    public QMFPackageQueryCommand(QMFCommandHeader header, BBDecoder decoder)
    {
        super(header);
    }

    public void process(VirtualHost virtualHost, ServerMessage message)
    {
        String exchangeName = message.getMessageHeader().getReplyToExchange();
        String routingKey = message.getMessageHeader().getReplyToRoutingKey();


        IApplicationRegistry appRegistry = virtualHost.getApplicationRegistry();
        QMFService service = appRegistry.getQMFService();

        Collection<QMFPackage> supportedSchemas = service.getSupportedSchemas();
        
        QMFCommand[] commands = new QMFCommand[ supportedSchemas.size() + 1 ];

        _qmfLogger.debug("Exectuting " + this);
        
        int i = 0;
        for(QMFPackage p : supportedSchemas)
        {
            commands[i++] = new QMFPackageIndicationCommand(this, p.getName());
        }
        commands[ commands.length - 1 ] = new QMFCommandCompletionCommand(this);


        for(QMFCommand cmd : commands)
        {

            QMFMessage responseMessage = new QMFMessage(routingKey, cmd);

            Exchange exchange = virtualHost.getExchangeRegistry().getExchange(exchangeName);

            List<? extends BaseQueue> queues = exchange.route(responseMessage);


            for(BaseQueue q : queues)
            {
                try
                {
                    q.enqueue(responseMessage);
                }
                catch (AMQException e)
                {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
    
    public String toString()
    {
        return "QMFPackageQueryCommand";
    }
}

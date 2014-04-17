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
package org.apache.qpid.server.queue;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.qpid.server.virtualhost.VirtualHostImpl;

public class DefaultQueueRegistry implements QueueRegistry
{
    private ConcurrentMap<String, AMQQueue<?>> _queueMap = new ConcurrentHashMap<String, AMQQueue<?>>();

    private final VirtualHostImpl _virtualHost;

    public DefaultQueueRegistry(VirtualHostImpl virtualHost)
    {
        _virtualHost = virtualHost;
    }

    public VirtualHostImpl getVirtualHost()
    {
        return _virtualHost;
    }

    public void registerQueue(AMQQueue queue)
    {
        _queueMap.put(queue.getName(), queue);
    }

    public void unregisterQueue(String name)
    {
        AMQQueue q = _queueMap.remove(name);
    }


    public Collection<AMQQueue<?>> getQueues()
    {
        return _queueMap.values();
    }

    public AMQQueue getQueue(String queue)
    {
        return queue == null ? null : _queueMap.get(queue);
    }

    @Override
    public void close()
    {
        for (final AMQQueue queue : getQueues())
        {
            queue.stop();
        }
        _queueMap.clear();
    }

    @Override
    public synchronized AMQQueue getQueue(UUID queueId)
    {
        Collection<AMQQueue<?>> queues = _queueMap.values();
        for (AMQQueue<?> queue : queues)
        {
            if (queue.getId().equals(queueId))
            {
                return queue;
            }
        }
        return null;
    }
}

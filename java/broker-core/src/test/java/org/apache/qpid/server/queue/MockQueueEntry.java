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

import org.apache.qpid.AMQException;
import org.apache.qpid.server.filter.Filterable;
import org.apache.qpid.server.message.AMQMessageHeader;
import org.apache.qpid.server.message.InstanceProperties;
import org.apache.qpid.server.message.ServerMessage;
import org.apache.qpid.server.subscription.Subscription;
import org.apache.qpid.server.txn.ServerTransaction;
import org.apache.qpid.server.util.Action;
import org.apache.qpid.server.util.StateChangeListener;

public class MockQueueEntry implements QueueEntry
{

    private ServerMessage _message;

    public boolean acquire()
    {
        return false;
    }

    public boolean acquire(Subscription sub)
    {
        return false;
    }

    public boolean acquiredBySubscription()
    {
        return false;
    }

    public boolean isAcquiredBy(Subscription subscription)
    {
        return false;
    }

    public void addStateChangeListener(StateChangeListener<QueueEntry, State> listener)
    {

    }

    public void delete()
    {

    }

    public int routeToAlternate(final Action<QueueEntry> action, final ServerTransaction txn)
    {
        return 0;
    }

    public boolean expired() throws AMQException
    {
        return false;
    }

    public boolean isAvailable()
    {
        return false;
    }

    public Subscription getDeliveredSubscription()
    {
        return null;
    }

    public boolean getDeliveredToConsumer()
    {
        return false;
    }

    public ServerMessage getMessage()
    {
        return _message;
    }

    public AMQQueue getQueue()
    {
        return null;
    }

    public long getSize()
    {
        return 0;
    }

    public boolean isAcquired()
    {
        return false;
    }


    public boolean isQueueDeleted()
    {

        return false;
    }


    public boolean isRejectedBy(long subscriptionId)
    {

        return false;
    }


    public void reject()
    {


    }


    public void release()
    {


    }


    public boolean removeStateChangeListener(StateChangeListener<QueueEntry, State> listener)
    {

        return false;
    }

    public void setRedelivered()
    {


    }

    public AMQMessageHeader getMessageHeader()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isPersistent()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRedelivered()
    {
        return false;
    }

    public int compareTo(QueueEntry o)
    {

        return 0;
    }

    public void setMessage(ServerMessage msg)
    {
        _message = msg;
    }

    public boolean isDeleted()
    {
        return false;
    }

    public QueueEntry getNextNode()
    {
        return null;
    }

    public QueueEntry getNextValidEntry()
    {
        return null;
    }

    @Override
    public int getDeliveryCount()
    {
        return 0;
    }

    @Override
    public void incrementDeliveryCount()
    {
    }

    @Override
    public void decrementDeliveryCount()
    {
    }

    @Override
    public Filterable asFilterable()
    {
        return Filterable.Factory.newInstance(_message, getInstanceProperties());
    }

    @Override
    public InstanceProperties getInstanceProperties()
    {
        return InstanceProperties.EMPTY;
    }
}

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

package org.apache.qpid.server.subscription;

import org.apache.qpid.AMQException;
import org.apache.qpid.protocol.AMQConstant;
import org.apache.qpid.server.logging.LogActor;
import org.apache.qpid.server.logging.LogSubject;
import org.apache.qpid.server.model.Port;
import org.apache.qpid.server.model.Transport;
import org.apache.qpid.server.protocol.AMQConnectionModel;
import org.apache.qpid.server.protocol.AMQSessionModel;
import org.apache.qpid.server.queue.AMQQueue;
import org.apache.qpid.server.queue.QueueEntry;
import org.apache.qpid.server.queue.QueueEntry.SubscriptionAcquiredState;
import org.apache.qpid.server.stats.StatisticsCounter;
import org.apache.qpid.server.util.StateChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MockSubscription implements Subscription
{

    private boolean _closed = false;
    private String tag = "mocktag";
    private AMQQueue queue = null;
    private StateChangeListener<Subscription, State> _listener = null;
    private volatile AMQQueue.Context _queueContext = null;
    private State _state = State.ACTIVE;
    private ArrayList<QueueEntry> messages = new ArrayList<QueueEntry>();
    private final Lock _stateChangeLock = new ReentrantLock();
    private List<QueueEntry> _acceptEntries = null;

    private final QueueEntry.SubscriptionAcquiredState _owningState = new QueueEntry.SubscriptionAcquiredState(this);

    private static final AtomicLong idGenerator = new AtomicLong(0);
    // Create a simple ID that increments for ever new Subscription
    private final long _subscriptionID = idGenerator.getAndIncrement();
    private boolean _isActive = true;

    public MockSubscription()
    {
    }

    public MockSubscription(List<QueueEntry> acceptEntries)
    {
        _acceptEntries = acceptEntries;
    }

    public void close()
    {
        _closed = true;
        if (_listener != null)
        {
            _listener.stateChanged(this, _state, State.CLOSED);
        }
        _state = State.CLOSED;
    }

    public String getName()
    {
        return tag;
    }

    @Override
    public void flush() throws AMQException
    {

    }

    public long getSubscriptionID()
    {
        return _subscriptionID;
    }

    public AMQQueue.Context getQueueContext()
    {
        return _queueContext;
    }

    public SubscriptionAcquiredState getOwningState()
    {
        return _owningState;
    }

    public LogActor getLogActor()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isTransient()
    {
        return false;
    }

    public long getBytesOut()
    {
        return 0;  // TODO - Implement
    }

    public long getMessagesOut()
    {
        return 0;  // TODO - Implement
    }

    public long getUnacknowledgedBytes()
    {
        return 0;  // TODO - Implement
    }

    public long getUnacknowledgedMessages()
    {
        return 0;  // TODO - Implement
    }

    public AMQQueue getQueue()
    {
        return queue;
    }

    public AMQSessionModel getSessionModel()
    {
        return new MockSessionModel();
    }

    public boolean trySendLock()
    {
        return _stateChangeLock.tryLock();
    }


    public void getSendLock()
    {
        _stateChangeLock.lock();
    }

    public boolean hasInterest(QueueEntry entry)
    {
        if(_acceptEntries != null)
        {
            //simulate selector behaviour, only signal
            //interest in the dictated queue entries
            return _acceptEntries.contains(entry);
        }

        return true;
    }

    public boolean isActive()
    {
        return _isActive ;
    }

    public void set(String key, Object value)
    {
    }

    public Object get(String key)
    {
        return null;
    }

    public boolean isAutoClose()
    {
        return false;
    }

    public boolean isClosed()
    {
        return _closed;
    }

    public boolean acquires()
    {
        return true;
    }

    public boolean seesRequeues()
    {
        return true;
    }

    public boolean isSuspended()
    {
        return false;
    }

    public void queueDeleted()
    {
    }

    public void releaseSendLock()
    {
        _stateChangeLock.unlock();
    }

    public void restoreCredit(QueueEntry queueEntry)
    {
    }

    public void send(QueueEntry entry, boolean batch) throws AMQException
    {
        if (messages.contains(entry))
        {
            entry.setRedelivered();
        }
        messages.add(entry);
    }

    public void flushBatched()
    {

    }

    public void setQueueContext(AMQQueue.Context queueContext)
    {
        _queueContext = queueContext;
    }

    public void setQueue(AMQQueue queue, boolean exclusive)
    {
        this.queue = queue;
    }

    public void setNoLocal(boolean noLocal)
    {
    }

    public void setStateListener(StateChangeListener<Subscription, State> listener)
    {
        this._listener = listener;
    }

    public State getState()
    {
        return _state;
    }

    public boolean wouldSuspend(QueueEntry msg)
    {
        return false;
    }

    public ArrayList<QueueEntry> getMessages()
    {
        return messages;
    }

    public boolean isSessionTransactional()
    {
        return false;
    }

    public void queueEmpty() throws AMQException
    {
    }

    public void setActive(final boolean isActive)
    {
        _isActive = isActive;
    }

    private static class MockSessionModel implements AMQSessionModel
    {
        private final UUID _id = UUID.randomUUID();

        @Override
        public UUID getId()
        {
            return _id;
        }

        @Override
        public AMQConnectionModel getConnectionModel()
        {
            return new MockConnectionModel();
        }

        @Override
        public String getClientID()
        {
            return null;
        }

        @Override
        public void close() throws AMQException
        {
        }

        @Override
        public LogSubject getLogSubject()
        {
            return null;
        }

        @Override
        public void checkTransactionStatus(long openWarn, long openClose,
                long idleWarn, long idleClose) throws AMQException
        {
        }

        @Override
        public void block(AMQQueue queue)
        {
        }

        @Override
        public void unblock(AMQQueue queue)
        {
        }

        @Override
        public void block()
        {
        }

        @Override
        public void unblock()
        {
        }

        @Override
        public boolean getBlocking()
        {
            return false;
        }

        @Override
        public Object getConnectionReference()
        {
            return this;
        }

        @Override
        public int getUnacknowledgedMessageCount()
        {
            return 0;
        }

        @Override
        public Long getTxnCount()
        {
            return null;
        }

        @Override
        public Long getTxnStart()
        {
            return null;
        }

        @Override
        public Long getTxnCommits()
        {
            return null;
        }

        @Override
        public Long getTxnRejects()
        {
            return null;
        }

        @Override
        public int getChannelId()
        {
            return 0;
        }

        @Override
        public int getConsumerCount()
        {
            return 0;
        }

        @Override
        public int compareTo(AMQSessionModel o)
        {
            return getId().compareTo(o.getId());
        }

        @Override
        public void close(AMQConstant cause, String message) throws AMQException
        {
        }
    }

    private static class MockConnectionModel implements AMQConnectionModel
    {
        @Override
        public void initialiseStatistics()
        {
        }

        @Override
        public void registerMessageReceived(long messageSize, long timestamp)
        {
        }

        @Override
        public void registerMessageDelivered(long messageSize)
        {
        }

        @Override
        public StatisticsCounter getMessageDeliveryStatistics()
        {
            return null;
        }

        @Override
        public StatisticsCounter getMessageReceiptStatistics()
        {
            return null;
        }

        @Override
        public StatisticsCounter getDataDeliveryStatistics()
        {
            return null;
        }

        @Override
        public StatisticsCounter getDataReceiptStatistics()
        {
            return null;
        }

        @Override
        public void resetStatistics()
        {

        }

        @Override
        public void close(AMQConstant cause, String message)
                throws AMQException
        {
        }

        @Override
        public void closeSession(AMQSessionModel session, AMQConstant cause,
                String message) throws AMQException
        {
        }

        @Override
        public long getConnectionId()
        {
            return 0;
        }

        @Override
        public List<AMQSessionModel> getSessionModels()
        {
            return null;
        }

        @Override
        public void block()
        {
        }

        @Override
        public void unblock()
        {
        }

        @Override
        public LogSubject getLogSubject()
        {
            return null;
        }

        @Override
        public String getUserName()
        {
            return null;
        }

        @Override
        public boolean isSessionNameUnique(byte[] name)
        {
            return false;
        }

        @Override
        public String getRemoteAddressString()
        {
            return "remoteAddress:1234";
        }

        @Override
        public String getClientId()
        {
            return null;
        }

        @Override
        public String getClientVersion()
        {
            return null;
        }

        @Override
        public String getClientProduct()
        {
            return null;
        }

        @Override
        public String getPrincipalAsString()
        {
            return null;
        }

        @Override
        public long getSessionCountLimit()
        {
            return 0;
        }

        @Override
        public long getLastIoTime()
        {
            return 0;
        }

        @Override
        public Port getPort()
        {
            return null;
        }

        @Override
        public Transport getTransport()
        {
            return null;
        }

        @Override
        public void stop()
        {
        }

        @Override
        public boolean isStopped()
        {
            return false;
        }

        @Override
        public String getVirtualHostName()
        {
            return null;
        }
    }
}

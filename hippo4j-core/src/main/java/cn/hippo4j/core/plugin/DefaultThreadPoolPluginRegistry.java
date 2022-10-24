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

package cn.hippo4j.core.plugin;

import cn.hippo4j.common.toolkit.Assert;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The default implementation of {@link ThreadPoolPluginRegistry}.
 *
 * @author huangchengxing
 */
public class DefaultThreadPoolPluginRegistry implements ThreadPoolPluginRegistry {

    /**
     * lock of this instance
     */
    private final ReadWriteLock instanceLock = new ReentrantReadWriteLock();

    /**
     * Registered {@link ThreadPoolPlugin}.
     */
    private final Map<String, ThreadPoolPlugin> registeredPlugins = new HashMap<>(16);

    /**
     * Registered {@link TaskAwarePlugin}.
     */
    private final List<TaskAwarePlugin> taskAwarePluginList = new ArrayList<>();

    /**
     * Registered {@link ExecuteAwarePlugin}.
     */
    private final List<ExecuteAwarePlugin> executeAwarePluginList = new ArrayList<>();

    /**
     * Registered {@link RejectedAwarePlugin}.
     */
    private final List<RejectedAwarePlugin> rejectedAwarePluginList = new ArrayList<>();

    /**
     * Registered {@link ShutdownAwarePlugin}.
     */
    private final List<ShutdownAwarePlugin> shutdownAwarePluginList = new ArrayList<>();

    /**
     * Clear all.
     */
    @Override
    public synchronized void clear() {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            registeredPlugins.clear();
            taskAwarePluginList.clear();
            executeAwarePluginList.clear();
            rejectedAwarePluginList.clear();
            shutdownAwarePluginList.clear();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param aware aware
     * @throws IllegalArgumentException thrown when a plugin with the same {@link ThreadPoolPlugin#getId()} already exists in the registry
     * @see ThreadPoolPlugin#getId()
     */
    @Override
    public void register(@NonNull ThreadPoolPlugin aware) {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            String id = aware.getId();
            Assert.isTrue(!isRegistered(id), "The plug-in with id [" + id + "] has been registered");

            // register aware
            registeredPlugins.put(id, aware);
            // quick index
            if (aware instanceof TaskAwarePlugin) {
                taskAwarePluginList.add((TaskAwarePlugin) aware);
            }
            if (aware instanceof ExecuteAwarePlugin) {
                executeAwarePluginList.add((ExecuteAwarePlugin) aware);
            }
            if (aware instanceof RejectedAwarePlugin) {
                rejectedAwarePluginList.add((RejectedAwarePlugin) aware);
            }
            if (aware instanceof ShutdownAwarePlugin) {
                shutdownAwarePluginList.add((ShutdownAwarePlugin) aware);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param id name
     */
    @Override
    public void unregister(String id) {
        Lock writeLock = instanceLock.writeLock();
        writeLock.lock();
        try {
            Optional.ofNullable(id)
                .map(registeredPlugins::remove)
                .ifPresent(old -> {
                    // remove quick index if necessary
                    if (old instanceof TaskAwarePlugin) {
                        taskAwarePluginList.remove(old);
                    }
                    if (old instanceof ExecuteAwarePlugin) {
                        executeAwarePluginList.remove(old);
                    }
                    if (old instanceof RejectedAwarePlugin) {
                        rejectedAwarePluginList.remove(old);
                    }
                    if (old instanceof ShutdownAwarePlugin) {
                        shutdownAwarePluginList.remove(old);
                    }
                });
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param id name
     * @return ture if target has been registered, false otherwise
     */
    @Override
    public boolean isRegistered(String id) {
        Lock readLock = instanceLock.readLock();
        try {
            return registeredPlugins.containsKey(id);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param id target name
     * @param <A> target aware type
     * @return {@link ThreadPoolPlugin}, null if unregister
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends ThreadPoolPlugin> A getAware(String id) {
        Lock readLock = instanceLock.readLock();
        try {
            return (A) registeredPlugins.get(id);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get execute aware list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    public Collection<ExecuteAwarePlugin> getExecuteAwareList() {
        Lock readLock = instanceLock.readLock();
        try {
            return executeAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get rejected aware list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    @Override
    public Collection<RejectedAwarePlugin> getRejectedAwareList() {
        Lock readLock = instanceLock.readLock();
        try {
            return rejectedAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    public Collection<ShutdownAwarePlugin> getShutdownAwareList() {
        Lock readLock = instanceLock.readLock();
        try {
            return shutdownAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    public Collection<TaskAwarePlugin> getTaskAwareList() {
        Lock readLock = instanceLock.readLock();
        try {
            return taskAwarePluginList;
        } finally {
            readLock.unlock();
        }
    }

}

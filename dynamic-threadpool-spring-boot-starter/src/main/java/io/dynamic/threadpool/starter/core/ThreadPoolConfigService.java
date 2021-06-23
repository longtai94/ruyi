package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.starter.config.DynamicThreadPoolProperties;
import io.dynamic.threadpool.starter.remote.HttpAgent;
import io.dynamic.threadpool.starter.remote.ServerHttpAgent;
import io.dynamic.threadpool.starter.listener.ClientWorker;
import io.dynamic.threadpool.starter.listener.Listener;

import java.util.Arrays;

/**
 * 线程池配置服务
 *
 * @author chen.ma
 * @date 2021/6/21 21:50
 */
public class ThreadPoolConfigService implements ConfigService {

    private final HttpAgent httpAgent;

    private final ClientWorker clientWorker;

    public ThreadPoolConfigService(DynamicThreadPoolProperties properties) {
        httpAgent = new ServerHttpAgent(properties);
        clientWorker = new ClientWorker(httpAgent);
    }

    @Override
    public void addListener(String namespace, String itemId, String tpId, Listener listener) {
        clientWorker.addTenantListeners(namespace, itemId, tpId, Arrays.asList(listener));
    }

    @Override
    public String getServerStatus() {
        if (clientWorker.isHealthServer()) {
            return "UP";
        } else {
            return "DOWN";
        }
    }
}

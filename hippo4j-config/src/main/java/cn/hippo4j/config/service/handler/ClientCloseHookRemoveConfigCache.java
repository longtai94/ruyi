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

package cn.hippo4j.config.service.handler;

import cn.hippo4j.common.api.ClientCloseHookExecute;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.service.ConfigCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Client close hook remove config cache.
 */
@Slf4j
@Component
public class ClientCloseHookRemoveConfigCache implements ClientCloseHookExecute {

    @Override
    public void closeHook(ClientCloseHookReq requestParam) {
        log.info("Remove Config Cache, Execute client hook function. Request: {}", JSONUtil.toJSONString(requestParam));
        try {
            String groupKey = requestParam.getGroupKey();
            if (StringUtil.isNotBlank(groupKey)) {
                ConfigCacheService.removeConfigCache(groupKey);
            }
        } catch (Exception ex) {
            log.error("Failed to remove config cache hook.", ex);
        }
    }
}

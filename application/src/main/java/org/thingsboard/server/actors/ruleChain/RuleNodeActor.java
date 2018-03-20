/**
 * Copyright © 2016-2018 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.actors.ruleChain;

import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.service.ComponentActor;
import org.thingsboard.server.actors.service.ContextBasedCreator;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.TbActorMsg;

public class RuleNodeActor extends ComponentActor<RuleNodeId, RuleNodeActorMessageProcessor> {

    private final RuleChainId ruleChainId;

    private RuleNodeActor(ActorSystemContext systemContext, TenantId tenantId, RuleChainId ruleChainId, RuleNodeId ruleNodeId) {
        super(systemContext, tenantId, ruleNodeId);
        this.ruleChainId = ruleChainId;
        setProcessor(new RuleNodeActorMessageProcessor(tenantId, ruleChainId, ruleNodeId, systemContext,
                logger, context().parent(), context().self()));
    }

    @Override
    protected void process(TbActorMsg msg) {
        switch (msg.getMsgType()) {
            case RULE_CHAIN_TO_RULE_MSG:
                processor.onRuleChainToRuleNodeMsg((RuleChainToRuleNodeMsg) msg);
                break;
        }
    }

    public static class ActorCreator extends ContextBasedCreator<RuleNodeActor> {
        private static final long serialVersionUID = 1L;

        private final TenantId tenantId;
        private final RuleChainId ruleChainId;
        private final RuleNodeId ruleNodeId;

        public ActorCreator(ActorSystemContext context, TenantId tenantId, RuleChainId ruleChainId, RuleNodeId ruleNodeId) {
            super(context);
            this.tenantId = tenantId;
            this.ruleChainId = ruleChainId;
            this.ruleNodeId = ruleNodeId;

        }

        @Override
        public RuleNodeActor create() throws Exception {
            return new RuleNodeActor(context, tenantId, ruleChainId, ruleNodeId);
        }
    }

    @Override
    protected long getErrorPersistFrequency() {
        return systemContext.getRuleNodeErrorPersistFrequency();
    }

}
package io.muenchendigital.digiwf.integration.zms.api;

import io.muenchendigital.digiwf.integration.zms.service.CreateZmsEntryEvent;
import io.muenchendigital.digiwf.integration.zms.service.ZmsService;
import io.muenchendigital.digiwf.spring.cloudstream.utils.api.streaming.message.service.CorrelateMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProcessor {

    private final ZmsService zmsService;
    private final CorrelateMessageService correlateMessageService;

    /**
     * Create a consumer for the createZmsEntry type
     *
     * @return consumer
     */
    @Bean
    public Consumer<Message<CreateZmsEntryEvent>> createZmsEntry() {
        return message -> {
            log.info("Processing create zms entry request from eventbus");
            final String id = this.zmsService.createEntry(message.getPayload());
            this.emitResponse(message.getHeaders(), id);
        };
    }

    /**
     * Function to emit a reponse using the correlateMessageService of digiwf-spring-cloudstream-utils
     *
     * @param messageHeaders The MessageHeaders of the incoming message you want to correlate your answer to
     * @param zmsId          id of the zms entry
     */
    public void emitResponse(final MessageHeaders messageHeaders, final String zmsId) {
        final Map<String, Object> correlatePayload = new HashMap<>();
        correlatePayload.put("zmsId", zmsId);
        this.correlateMessageService.sendCorrelateMessage(messageHeaders, correlatePayload);
    }
}

package no.fint.personnel.service;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.adapter.event.EventResponseService;
import no.fint.adapter.event.EventStatusService;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.resource.FintLinks;
import no.fint.personnel.SupportedActions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class EventHandlerService {

    @Autowired
    private EventResponseService eventResponseService;

    @Autowired
    private EventStatusService eventStatusService;

    @Autowired
    private SupportedActions supportedActions;

    @Autowired
    private Collection<Handler> handlers;

    @Getter
    private ImmutableMap<String, Handler> actionsHandlerMap;

    @Autowired
    @Qualifier("responseHandler")
    private Executor executor;

    public void handleEvent(String component, Event event) {
        if (event.isHealthCheck()) {
            postHealthCheckResponse(component, event);
        } else {
            if (eventStatusService.verifyEvent(component, event)) {
                executor.execute(() ->
                        handleResponse(component, event.getAction(), new Event<>(event)));
            }
        }
    }

    private void handleResponse(String component, String action, Event<FintLinks> response) {
        try {
            actionsHandlerMap.getOrDefault(action, e -> {
                log.warn("No handler found for {}", action);
                e.setStatus(Status.ADAPTER_REJECTED);
                e.setResponseStatus(ResponseStatus.REJECTED);
                e.setMessage("Unsupported action: " + action);
            }).accept(response);
        } catch (Exception e) {
            response.setResponseStatus(ResponseStatus.ERROR);
            response.setMessage(ExceptionUtils.getStackTrace(e));
        } finally {
            if (response.getData() != null) {
                log.info("{}: Response for {}: {}, {} items", component, response.getAction(), response.getResponseStatus(), response.getData().size());
                log.trace("Event data: {}", response.getData());
            } else {
                log.info("{}: Response for {}: {}", component, response.getAction(), response.getResponseStatus());
            }
            eventResponseService.postResponse(component, response);
        }
    }

    public void postHealthCheckResponse(String component, Event event) {
        Event<Health> healthCheckEvent = new Event<>(event);
        healthCheckEvent.setStatus(Status.TEMP_UPSTREAM_QUEUE);

        if (healthCheck()) {
            healthCheckEvent.addData(new Health("adapter", HealthStatus.APPLICATION_HEALTHY));
        } else {
            healthCheckEvent.addData(new Health("adapter", HealthStatus.APPLICATION_UNHEALTHY));
            healthCheckEvent.setMessage("The adapter is unable to communicate with the application.");
        }

        eventResponseService.postResponse(component, healthCheckEvent);
    }

    private boolean healthCheck() {
        return handlers.stream().allMatch(Handler::health);
    }

    @PostConstruct
    void init() {
        final ImmutableMap.Builder<String, Handler> builder = ImmutableMap.builder();
        handlers.forEach(h -> h.actions().forEach(a -> builder.put(a, h)));
        actionsHandlerMap = builder.build();
        supportedActions.getActions().addAll(actionsHandlerMap.keySet());
        log.info("Registered {} handlers, supporting actions: {}", actionsHandlerMap.size(), supportedActions.getActions());
    }
}

package no.fint.personnel.behaviour;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.resource.administrasjon.personal.FastlonnResource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class RejectFastlonnWithoutBeskjeftigelse implements Behaviour<FastlonnResource> {
    @Override
    public void accept(Event event, FastlonnResource fastlonn) {
        if (Objects.isNull(fastlonn.getProsent())||fastlonn.getProsent() < 0L) {
            event.setResponseStatus(ResponseStatus.REJECTED);
            event.setMessage("Fastlønn må ha prosent > 0");
            event.setStatusCode("FLX002");
            addProblem(event, "prosent", "Fastlønn må ha prosent > 0");
            log.info("Rejecting {}", event);
        }
    }
}

package no.fint.personnel.behaviour;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.resource.administrasjon.personal.FravarResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RejectFravarWithoutReason implements Behaviour<FravarResource> {
    @Override
    public void accept(Event event, FravarResource fravar) {
        if (fravar.getFravarsgrunn().isEmpty()) {
            event.setResponseStatus(ResponseStatus.REJECTED);
            event.setMessage("Fravær må ha fraværsgrunn");
            event.setStatusCode("FVX003");
            addProblem(event, "fravarsgrunn", "Fravær må ha fraværsgrunn");
            log.info("Rejecting {}", event);
        }
    }
}

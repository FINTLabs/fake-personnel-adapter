package no.fint.personnel.behaviour;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.resource.administrasjon.personal.VariabellonnResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RejectLessThanOneHour implements Behaviour<VariabellonnResource> {
    @Override
    public void accept(Event event, VariabellonnResource variabellonn) {
        if (variabellonn.getAntall() < 100L) {
            event.setResponseStatus(ResponseStatus.REJECTED);
            event.setMessage("Antall kan ikke være under 100");
            event.setStatusCode("VLX004");
            addProblem(event, "antall", "Antall kan ikke være under 100");
            log.info("Rejecting {}", event);
        }
    }

}

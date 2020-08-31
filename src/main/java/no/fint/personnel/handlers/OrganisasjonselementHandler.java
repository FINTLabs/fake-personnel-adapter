package no.fint.personnel.handlers;

import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.administrasjon.organisasjon.OrganisasjonActions;
import no.fint.model.resource.FintLinks;
import no.fint.personnel.data.FakeData;
import no.fint.personnel.service.Handler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class OrganisasjonselementHandler implements Handler {
    private final FakeData fakeData;

    public OrganisasjonselementHandler(FakeData fakeData) {
        this.fakeData = fakeData;
    }

    @Override
    public void accept(Event<FintLinks> fintLinksEvent) {
        fakeData.getOrganisasjonselementer().forEach(fintLinksEvent::addData);
        fintLinksEvent.setResponseStatus(ResponseStatus.ACCEPTED);
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(OrganisasjonActions.GET_ALL_ORGANISASJONSELEMENT.name());
    }
}

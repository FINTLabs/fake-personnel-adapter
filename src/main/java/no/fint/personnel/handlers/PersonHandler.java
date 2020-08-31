package no.fint.personnel.handlers;

import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.felles.FellesActions;
import no.fint.model.resource.FintLinks;
import no.fint.personnel.data.FakeData;
import no.fint.personnel.service.Handler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class PersonHandler implements Handler {
    private final FakeData fakeData;

    public PersonHandler(FakeData fakeData) {
        this.fakeData = fakeData;
    }

    @Override
    public void accept(Event<FintLinks> fintLinksEvent) {
        fakeData.getPersoner().forEach(fintLinksEvent::addData);
        fintLinksEvent.setResponseStatus(ResponseStatus.ACCEPTED);
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(FellesActions.GET_ALL_PERSON.name());
    }
}

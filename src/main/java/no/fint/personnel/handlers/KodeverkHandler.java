package no.fint.personnel.handlers;

import com.google.common.collect.ImmutableMap;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.administrasjon.kodeverk.KodeverkActions;
import no.fint.model.felles.basisklasser.Begrep;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.administrasjon.kodeverk.PersonalressurskategoriResource;
import no.fint.personnel.service.Handler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class KodeverkHandler implements Handler {

    private final ImmutableMap<String, Consumer<Event<FintLinks>>> handlers
            = ImmutableMap.<String, Consumer<Event<FintLinks>>>builder()
            .put(KodeverkActions.GET_ALL_PERSONALRESSURSKATEGORI.name(), producer(PersonalressurskategoriResource::new, "F", "M"))
            .build();

    private <T extends Begrep & FintLinks> Consumer<Event<FintLinks>> producer(Supplier<T> supplier, String... values) {
        return event ->
                Arrays.stream(values)
                        .map(it -> {
                            T r = supplier.get();
                            r.setKode(it);
                            r.setNavn(it);
                            r.setSystemId(createIdentifikator(it));
                            return r;
                        })
                        .forEach(event::addData);
    }

    private Identifikator createIdentifikator(String value) {
        Identifikator id = new Identifikator();
        id.setIdentifikatorverdi(value);
        return id;
    }

    @Override
    public void accept(Event<FintLinks> event) {
        event.setResponseStatus(ResponseStatus.ACCEPTED);
        handlers.getOrDefault(event.getAction(), e -> e.setResponseStatus(ResponseStatus.REJECTED)).accept(event);
    }

    @Override
    public Set<String> actions() {
        return handlers.keySet();
    }

    @Override
    public boolean health() {
        return !handlers.isEmpty();
    }
}

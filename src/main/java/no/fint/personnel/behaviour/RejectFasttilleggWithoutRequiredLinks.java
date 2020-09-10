package no.fint.personnel.behaviour;

import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.resource.administrasjon.personal.FasttilleggResource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RejectFasttilleggWithoutRequiredLinks implements Behaviour<FasttilleggResource> {
    @Override
    public void accept(Event event, FasttilleggResource resource) {
        if (empty(resource.getArbeidsforhold())) {
            addProblem(event, "arbeidsforhold", "Mangler relasjon til Arbeidsforhold");
        }
        if (empty(resource.getAttestant())) {
            addProblem(event, "attestant", "Mangler relasjon til Attestant");
        }
        if (empty(resource.getAnviser())) {
            addProblem(event, "anviser", "Mangler relasjon til Anviser");
        }

        if (empty(resource.getLonnsart())) {
            addProblem(event, "lonnsart", "Mangler relasjon til Lønnsart");
        }

        if (Objects.isNull(resource.getKontostreng())) {
            addProblem(event, "kontostreng", "Mangler Kontostreng");
        } else {
            if (empty(resource.getKontostreng().getArt())) {
                addProblem(event, "kontostreng.art", "Mangler relasjon til Art");
            }
            if (empty(resource.getKontostreng().getAnsvar())) {
                addProblem(event, "kontostreng.ansvar", "Mangler relasjon til Ansvar");
            }
            if (empty(resource.getKontostreng().getFunksjon())) {
                addProblem(event, "kontostreng.funksjon", "Mangler relasjon til Funksjon");
            }

        }

        if (!empty(event.getProblems())) {
            event.setResponseStatus(ResponseStatus.REJECTED);
            event.setStatusCode(getClass().getSimpleName());
        }
    }

}

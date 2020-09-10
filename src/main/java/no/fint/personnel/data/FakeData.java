package no.fint.personnel.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.fake.person.PersonGenerator;
import no.fint.model.administrasjon.kodeverk.Personalressurskategori;
import no.fint.model.administrasjon.organisasjon.Organisasjonselement;
import no.fint.model.administrasjon.personal.Personalressurs;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.felles.kompleksedatatyper.Personnavn;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class FakeData {

    @Value("${fint.adapter.fake.employees:100}")
    private int antallPersoner;

    @Value("${fint.adapter.organizations}")
    private String orgId;

    @Getter
    private List<PersonResource> personer;

    @Getter
    private List<PersonalressursResource> ansatte;

    @Getter
    private List<ArbeidsforholdResource> arbeidsforhold;

    @Getter
    private List<OrganisasjonselementResource> organisasjonselementer;

    @Autowired
    private PersonGenerator personGenerator;

    private String[] avdelinger = {"BA", "DH", "EL", "HS", "ID", "KD", "MD", "ME", "MK", "NA", "PB", "RM", "SS", "ST", "TP"};

    @PostConstruct
    public void init() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        organisasjonselementer = new LinkedList<>();
        OrganisasjonselementResource organisasjonselementResource = new OrganisasjonselementResource();

        organisasjonselementResource.setOrganisasjonsId(personGenerator.identifikator("42"));
        organisasjonselementResource.setOrganisasjonsKode(personGenerator.identifikator("42"));
        organisasjonselementResource.setNavn("FINTLABS");
        organisasjonselementResource.setOrganisasjonsnummer(personGenerator.identifikator("999999999"));
        organisasjonselementResource.addOverordnet(Link.with(Organisasjonselement.class, "organisasjonskode", "42"));
        organisasjonselementer.add(organisasjonselementResource);

        personer = new ArrayList<>(antallPersoner);
        ansatte = new ArrayList<>(antallPersoner);
        arbeidsforhold = new ArrayList<>(antallPersoner);
        for (int i = 0; i < antallPersoner; i++) {
            String systemID = Integer.toString(50000 + i);
            PersonResource personResource = personGenerator.generatePerson(2020-70, 2020-18);
            personResource.addPersonalressurs(Link.with(PersonalressursResource.class, "systemid", systemID));

            PersonalressursResource personalressursResource = new PersonalressursResource();
            Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
            kontaktinformasjon.setEpostadresse(String.format("%s@%s", systemID, orgId));
            personalressursResource.setKontaktinformasjon(kontaktinformasjon);
            personalressursResource.setAnsattnummer(personGenerator.identifikator(systemID));
            personalressursResource.setSystemId(personGenerator.identifikator(systemID));
            personalressursResource.setBrukernavn(personGenerator.identifikator(String.format("%s@%s", systemID, orgId)));
            personalressursResource.addPerson(Link.with(PersonResource.class, "fodselsnummer", personResource.getFodselsnummer().getIdentifikatorverdi()));
            personalressursResource.addArbeidsforhold(Link.with(ArbeidsforholdResource.class, "systemid", systemID));
            personalressursResource.addPersonalressurskategori(Link.with(Personalressurskategori.class, "systemid", "F"));

            ArbeidsforholdResource arbeidsforholdResource = new ArbeidsforholdResource();
            arbeidsforholdResource.addPersonalressurs(Link.with(PersonalressursResource.class, "systemid", systemID));
            arbeidsforholdResource.setSystemId(personGenerator.identifikator(systemID));
            arbeidsforholdResource.setAnsettelsesprosent(random.nextLong(50 * 100, 100 * 100));
            arbeidsforholdResource.setLonnsprosent(random.nextLong(0, arbeidsforholdResource.getAnsettelsesprosent()));
            arbeidsforholdResource.setTilstedeprosent(random.nextLong(0, arbeidsforholdResource.getAnsettelsesprosent()));
            arbeidsforholdResource.setArslonn(random.nextLong(100000000));
            arbeidsforholdResource.setHovedstilling(true);
            arbeidsforholdResource.setStillingsnummer("1");

            arbeidsforhold.add(i, arbeidsforholdResource);
            ansatte.add(i, personalressursResource);
            personer.add(i, personResource);

            Periode periode = new Periode();
            periode.setStart(Date.from(LocalDate.now().minusWeeks(random.nextLong(200)).atStartOfDay(ZoneId.of("UTC")).toInstant()));
            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    periode.setSlutt(Date.from(LocalDate.now().plusDays(random.nextLong(400)).atStartOfDay(ZoneId.of("UTC")).toInstant()));
                } else {
                    periode.setSlutt(Date.from(LocalDate.now().minusDays(random.nextLong(400)).atStartOfDay(ZoneId.of("UTC")).toInstant()));
                }
            }

            personalressursResource.setAnsettelsesperiode(periode);
            arbeidsforholdResource.setArbeidsforholdsperiode(periode);
            arbeidsforholdResource.setGyldighetsperiode(periode);
        }

        //personer.stream().map(PersonResource::getNavn).map(PersonGenerator::getPersonnavnAsString).forEach(System.out::println);

        Arrays.stream(avdelinger).map(
                it -> {
                    OrganisasjonselementResource avdeling = new OrganisasjonselementResource();
                    avdeling.setOrganisasjonsKode(personGenerator.identifikator(it));
                    avdeling.setOrganisasjonsId(personGenerator.identifikator(it));
                    avdeling.setNavn("Avdeling " + it);
                    avdeling.addOverordnet(Link.with(OrganisasjonselementResource.class, "organisasjonsnummer", "999999999"));
                    organisasjonselementResource.addUnderordnet(Link.with(OrganisasjonselementResource.class, "organisasjonskode", it));
                    avdeling.addLeder(Link.with(Personalressurs.class, "ansattnummer", sample(ansatte, random).getAnsattnummer().getIdentifikatorverdi()));
                    return avdeling;
                }
        ).forEach(organisasjonselementer::add);

        organisasjonselementResource.addLeder(Link.with(Personalressurs.class, "ansattnummer", sample(ansatte, random).getAnsattnummer().getIdentifikatorverdi()));

        arbeidsforhold.forEach(e -> {
            OrganisasjonselementResource b = sample(organisasjonselementer, random);
            e.addArbeidssted(Link.with(b.getClass(), "organisasjonskode", b.getOrganisasjonsKode().getIdentifikatorverdi()));
            b.addArbeidsforhold(Link.with(e.getClass(), "systemid", e.getSystemId().getIdentifikatorverdi()));
        });

    }

    private static <T> T sample(List<T> collection, ThreadLocalRandom random) {
        return collection.get(random.nextInt(collection.size()));
    }

    @Scheduled(initialDelay = 100000, fixedDelay = 60000)
    public void mutate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Personnavn navn = sample(personer, random).getNavn();
        navn.setFornavn(navn.getFornavn() + " Frank");
        log.info(navn.getFornavn());
    }

}

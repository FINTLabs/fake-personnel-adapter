package no.fint.adapter.event


import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import no.fint.event.model.Status
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class EventStatusServiceSpec extends Specification {
    private EventStatusService eventStatusService
    private no.fint.adapter.FintAdapterProps fintAdapterProps
    private no.fint.personnel.SupportedActions supportedActions
    private RestTemplate restTemplate

    void setup() {
        restTemplate = Mock(RestTemplate)
        fintAdapterProps = Mock(no.fint.adapter.FintAdapterProps)
        supportedActions = new no.fint.personnel.SupportedActions()
        eventStatusService = new EventStatusService(props: fintAdapterProps, restTemplate: restTemplate, supportedActions: supportedActions)
    }

    def "Verify event and POST event status"() {
        given:
        def event = new Event(orgId: 'rogfk.no', action: DefaultActions.HEALTH.name())

        when:
        def verifiedEvent = eventStatusService.verifyEvent(event)

        then:
        1 * fintAdapterProps.getStatusEndpoint() >> 'http://localhost'
        1 * restTemplate.exchange(_ as String, _ as HttpMethod, _ as HttpEntity, _ as Class) >> ResponseEntity.ok().build()
        verifiedEvent.status == Status.ADAPTER_ACCEPTED
    }
}

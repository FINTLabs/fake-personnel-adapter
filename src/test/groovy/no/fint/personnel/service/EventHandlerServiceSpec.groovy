package no.fint.personnel.service


import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import spock.lang.Specification

class EventHandlerServiceSpec extends Specification {
    private EventHandlerService eventHandlerService
    private no.fint.adapter.event.EventStatusService eventStatusService
    private no.fint.adapter.event.EventResponseService eventResponseService

    void setup() {
        eventStatusService = Mock(no.fint.adapter.event.EventStatusService)
        eventResponseService = Mock(no.fint.adapter.event.EventResponseService)
        eventHandlerService = new EventHandlerService(eventStatusService: eventStatusService, eventResponseService: eventResponseService)
    }

    def "Post response on health check"() {
        given:
        def event = new Event('rogfk.no', 'test', DefaultActions.HEALTH, 'test')

        when:
        eventHandlerService.handleEvent(event)

        then:
        1 * eventResponseService.postResponse(_ as Event)
    }

}

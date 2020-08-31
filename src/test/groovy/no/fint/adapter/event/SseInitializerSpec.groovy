package no.fint.adapter.event


import no.fint.sse.FintSse
import spock.lang.Specification

class SseInitializerSpec extends Specification {
    private no.fint.adapter.sse.SseInitializer sseInitializer
    private no.fint.adapter.FintAdapterProps props
    private FintSse fintSse

    void setup() {
        props = Mock(no.fint.adapter.FintAdapterProps) {
            getOrganizations() >> ['rogfk.no', 'hfk.no', 'vaf.no']
            getSseEndpoint() >> 'http://localhost'
        }
        fintSse = Mock(FintSse)
    }

    def "Register and close SSE client for organizations"() {
        given:
        sseInitializer = new no.fint.adapter.sse.SseInitializer(props: props)

        when:
        sseInitializer.init()

        then:
        sseInitializer.sseClients.size() == 3
    }

    def "Check SSE connection"() {
        given:
        sseInitializer = new no.fint.adapter.sse.SseInitializer(props: props, sseClients: [fintSse])

        when:
        sseInitializer.checkSseConnection()

        then:
        1 * fintSse.verifyConnection() >> true
    }

    def "Close SSE connection"() {
        given:
        sseInitializer = new no.fint.adapter.sse.SseInitializer(props: props, sseClients: [fintSse])

        when:
        sseInitializer.cleanup()

        then:
        1 * fintSse.close()
    }
}

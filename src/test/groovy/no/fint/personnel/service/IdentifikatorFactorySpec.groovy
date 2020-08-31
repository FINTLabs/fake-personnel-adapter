package no.fint.personnel.service


import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

class IdentifikatorFactorySpec extends Specification {


    def "Creates valid identifikator"() {
        given:
        def identifikatorFactory = new IdentifikatorFactory(atomicInteger: new AtomicInteger())

        when:
        def id = identifikatorFactory.create()

        then:
        id.getIdentifikatorverdi()
    }

    def "Creates distinct identifikator values"() {
        given:
        def identifikatorFactory = new IdentifikatorFactory(atomicInteger: new AtomicInteger())

        when:
        def id1 = identifikatorFactory.create()
        def id2 = identifikatorFactory.create()

        then:
        id1 != id2
        id1.identifikatorverdi != id2.identifikatorverdi
    }
}

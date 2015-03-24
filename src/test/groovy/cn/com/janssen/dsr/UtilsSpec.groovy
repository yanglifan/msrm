package cn.com.janssen.dsr

import org.joda.time.DateTime
import spock.lang.Specification

class UtilsSpec extends Specification {
    def "Calculate the begin of the last week"() {
        given:
        def time = new DateTime(2014, 5, 6, 0, 0)

        when:
        def result = Utils.startPointOfLastWeek(time)

        then:
        result.toDate().format("yyyy-MM-dd HH:mm:ss") == "2014-04-28 00:00:00"
    }
}

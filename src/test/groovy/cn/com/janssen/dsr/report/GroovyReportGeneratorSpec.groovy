package cn.com.janssen.dsr.report

import cn.com.janssen.dsr.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("dev")
class GroovyReportGeneratorSpec extends Specification {
    @Autowired
    GroovyReportGenerator groovyReportGenerator

    def "GenerateDailyReport"() {
        groovyReportGenerator.generateDailyReport()

        expect:
        true
    }
}

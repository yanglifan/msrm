package cn.com.janssen.dsr.repository

import cn.com.janssen.dsr.Application
import cn.com.janssen.dsr.DevelopmentBootStrap
import cn.com.janssen.dsr.domain.MedicineComment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("dev")
class MedicineCommentRepositorySpec extends Specification {
    @Autowired
    MedicineCommentRepository medicineCommentRepository

    @Autowired
    MedicineRepository medicineRepository

    @Autowired
    VisitRecordRepository visitRecordRepository

    @Autowired
    DevelopmentBootStrap bootStrap

    def setup() {
        medicineCommentRepository.deleteAll()
        visitRecordRepository.deleteAll()
        bootStrap.init()
    }

    def "Save and load a medicine comment"() {
        given:
        def visitRecord = visitRecordRepository.findAll().iterator().next()
        def medicine = medicineRepository.findByName("Sibelium")
        def medicineComment = new MedicineComment(visitRecord: visitRecord, medicine: medicine, comment: "Good")

        when:
        medicineCommentRepository.save(medicineComment)
        medicineComment = medicineCommentRepository.findOne(medicineComment.id)

        then:
        medicineCommentRepository.count() == 61
        medicineComment.medicine.name == "Sibelium"
    }

    def "Update a medicine comment"() {
        given:
        def visitRecord = visitRecordRepository.findAll().iterator().next()
        def medicine = medicineRepository.findByName("Sibelium")
        def medicineComment = new MedicineComment(visitRecord: visitRecord, medicine: medicine, comment: "Good")
        medicineCommentRepository.save(medicineComment)
        medicineComment = medicineCommentRepository.findOne(medicineComment.id)
        medicineComment.medicine = medicineRepository.findByName("Itraconazole")

        when:
        medicineCommentRepository.save(medicineComment)
        medicineComment = medicineCommentRepository.findOne(medicineComment.id)

        then:
        medicineComment.medicine.name == "Itraconazole"
    }
}

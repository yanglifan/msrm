package cn.com.janssen.dsr.report

import cn.com.janssen.dsr.domain.Medicine
import cn.com.janssen.dsr.domain.VisitRecord
import cn.com.janssen.dsr.repository.MedicineCommentRepository
import cn.com.janssen.dsr.repository.MedicineRepository
import cn.com.janssen.dsr.repository.VisitRecordRepository
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.atomic.AtomicInteger

@Component
class GroovyReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyReportGenerator.class)

    private enum DailyReportHeader {
        DSR_NAME("dsr.displayName", 0),
        VISIT_AT_DATE("visitAt", 1),
        VISIT_AT_TIME("", 2),
        HOSPITAL_SN("doctor.hospital.serialNumber", 3),
        HOSPITAL_NAME("doctor.hospital.name", 4),
        DEPARTMENT("doctor.department", 5),
        DOCTOR_SN("doctor.serialNumber", 6),
        DOCTOR_NAME("doctor.name", 7),
        DOCTOR_EMAIL("doctor.email", 8),
        DOCTOR_IS_DXY_MEMBER("doctor.isDxyMember", 9)

        String propertyPath
        int columnIndex

        DailyReportHeader(String propertyPath, int columnIndex) {
            this.propertyPath = propertyPath
            this.columnIndex = columnIndex
        }
    }

    @Autowired
    VisitRecordRepository visitRecordRepository

    @Autowired
    private MedicineRepository medicineRepository

    @Autowired
    private MedicineCommentRepository medicineCommentRepository

//    @Transactional(readOnly = true)
    public void generateDailyReport() throws Exception {
        FileInputStream file = new FileInputStream(new File("daily-report-template.xlsx"))
        XSSFWorkbook workbook = new XSSFWorkbook(file)
        XSSFSheet sheet = workbook.getSheetAt(0)

        Map<String, MedicineHolder> medicines = loadAllMedicines()

        AtomicInteger rowNumber = new AtomicInteger(2)
        for (VisitRecord visitRecord : visitRecordRepository.findAll()) {
            LOGGER.debug('Generate Excel row for the visit record {}', visitRecord.getId())

            Row row = sheet.createRow(rowNumber.getAndIncrement())

            fillMainInfo(row, visitRecord)

            medicineCommentRepository.findAllByVisitRecord(visitRecord).each({
                Cell commentCell = row.createCell(medicines.get(it.medicine.name).columnIndex)
                commentCell.setCellValue(it.getComment())
            })
        }

        file.close()

        generateExcelFile(workbook, "daily_report.xlsx")
    }

    private void fillMainInfo(XSSFRow row, VisitRecord visitRecord) {
        for (DailyReportHeader header : DailyReportHeader.values()) {
            LOGGER.debug('Fill the data for {}', header.name())
            Cell cell = row.getCell(header.columnIndex)
            if (cell == null) cell = row.createCell(header.columnIndex)

            Object bean = visitRecord

            if (!header.propertyPath) continue

            String[] properties = header.propertyPath.split("\\.")
            for (int i = 0; i < properties.length; i++) {
                try {
                    bean = bean."${properties[i]}"
                } catch (MissingPropertyException e) {
                    LOGGER.warn(e.getMessage())
                    break
                }

                if (i == properties.length - 1) {
                    LOGGER.debug('The value is {}', bean)
                    cell.setCellValue((String) bean)
                }
            }
        }
    }

    private static void generateExcelFile(XSSFWorkbook workbook, String filePath) {
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    private Map<String, MedicineHolder> loadAllMedicines() {
        Iterator<Medicine> medicineIterator = medicineRepository.findAll().iterator();
        Map<String, MedicineHolder> medicineHolderMap = new LinkedHashMap<>();
        int index = DailyReportHeader.values().size() - 1;
        while (medicineIterator.hasNext()) {
            Medicine medicine = medicineIterator.next();
            medicineHolderMap.put(medicine.getName(), new MedicineHolder(index++, medicine.getName()));
        }
        return medicineHolderMap;
    }

    private class MedicineHolder {
        int columnIndex;
        String name;

        private MedicineHolder(int columnIndex, String name) {
            this.columnIndex = columnIndex;
            this.name = name;
        }
    }
}

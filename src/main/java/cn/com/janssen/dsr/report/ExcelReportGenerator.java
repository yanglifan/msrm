package cn.com.janssen.dsr.report;

import cn.com.janssen.dsr.Utils;
import cn.com.janssen.dsr.domain.Medicine;
import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.domain.VisitRecord;
import cn.com.janssen.dsr.repository.MedicineCommentRepository;
import cn.com.janssen.dsr.repository.MedicineRepository;
import cn.com.janssen.dsr.repository.UserRepository;
import cn.com.janssen.dsr.repository.VisitRecordRepository;
import cn.com.janssen.dsr.report.processor.DsrCollector;
import cn.com.janssen.dsr.report.processor.VisitDaysNumberCalculator;
import cn.com.janssen.dsr.report.processor.VisitRecordProcessor;
import cn.com.janssen.dsr.report.processor.VisitedDoctorNumberCalculator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ExcelReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReportGenerator.class);

    private enum DailyReportHeader {
        HOSPITAL_SN("doctor.hospital.serialNumber", 3);
        int columnIndex;
        String propertyPath;

        DailyReportHeader(String propertyPath, int columnIndex) {
            this.propertyPath = propertyPath;
            this.columnIndex = columnIndex;
        }
    }

    private static final Map<DayOfWeek, Integer> DAY_OF_WEEK_DISTANCE = new HashMap<>();

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineCommentRepository medicineCommentRepository;

    @Autowired
    private UserRepository userRepository;


    @PostConstruct
    public void init() {
        initDayOfWeekDistance();
    }

    private void initDayOfWeekDistance() {

    }

    @Scheduled(cron = "1 * * * * *")
    public void schedulingTest() {
    }

    public void generateDailyReport() throws Exception {
        FileInputStream file = new FileInputStream(new File("daily-report-template.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Map<String, MedicineHolder> medicines = loadAllMedicines(2);

        AtomicInteger rowNumber = new AtomicInteger(2);
        for (VisitRecord visitRecord : visitRecordRepository.findAll()) {
            LOGGER.debug("Generate Excel row for the visit record {}", visitRecord.getId());

            Row row = sheet.createRow(rowNumber.getAndIncrement());

            Cell repCell = row.createCell(0);
            repCell.setCellValue(visitRecord.getDsr().getUsername());

            for (DailyReportHeader header : DailyReportHeader.values()) {
                LOGGER.debug("Fill the data for {}", header.name());
                Cell cell = row.createCell(header.columnIndex);
                Object bean = visitRecord;
                String[] properties = header.propertyPath.split("\\.");
                for (int i = 0; i < properties.length; i++) {
                    bean = FieldUtils.readField(bean, properties[i]);

                    if (i == properties.length - 1) {
                        LOGGER.debug("Fill in {}", bean);
                        cell.setCellValue((String) bean);
                    }
                }
            }

            medicineCommentRepository.findAllByVisitRecord(visitRecord).forEach(comment -> {
                Cell commentCell = row.createCell(medicines.get(comment.getMedicineName()).columnIndex);
                commentCell.setCellValue(comment.getComment());
            });
        }

        generateExcelFile(workbook, "daily_report.xlsx");
    }

    private void generateExcelFile(XSSFWorkbook workbook, String filePath) {
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(filePath));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Assume that this day to trigger this method is Monday, so the way of calculate the last Monday is easier.
     * TODO: Should be able to calculate the date of the last Monday smarter.
     */
    @SuppressWarnings("unchecked")
    public void generateWeeklyReport(User manager) throws Exception {
        DateTime now = DateTime.now();
        Date beginOfLastWeek = Utils.startPointOfLastWeek(now).toDate();
        Date endOfLastWeek = Utils.endPointOfLastWeek(now).toDate();

        Map<WeeklyReportTitle, VisitRecordProcessor> weeklyReportProcessors = initWeeklyReportProcessors();

        // query according to the manager, start of the day and the end of the day.
        List<VisitRecord> visitRecords = visitRecordRepository.findAllByManagerAndVisitAtBetween(manager, beginOfLastWeek, endOfLastWeek);

        visitRecords.forEach(visitRecord -> {
            for (WeeklyReportTitle weeklyReportTitle : WeeklyReportTitle.values()) {
                VisitRecordProcessor processor = weeklyReportProcessors.get(weeklyReportTitle);
                if (processor != null)
                    processor.accept(visitRecord);
            }
        });

        FileInputStream file = new FileInputStream(new File("weekly-report-template.xlsx"));

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Set<User> dsrSet = ((DsrCollector) weeklyReportProcessors.get(WeeklyReportTitle.DSR)).getAll();
        AtomicInteger rowIndex = new AtomicInteger(2);
        dsrSet.forEach(dsr -> {
            Row row = createRowIfAbsent(sheet, rowIndex);

            for (WeeklyReportTitle weeklyReportTitle : WeeklyReportTitle.values()) {
                VisitRecordProcessor<String, String> processor = weeklyReportProcessors.get(weeklyReportTitle);
                if (processor != null) {
                    Cell cell = createCellIfAbsent(row, weeklyReportTitle);

                    String value = processor.getResult(dsr.getUsername());
                    LOGGER.debug("Set cell [Row: {}, Col: {}], the value is {}", cell.getRowIndex(),
                            cell.getColumnIndex(), value);
                    cell.setCellValue(processor.getResult(dsr.getUsername()));
                }
            }
        });

        file.close();

        generateExcelFile(workbook, "weekly_report.xlsx");
    }

    private Cell createCellIfAbsent(Row row, WeeklyReportTitle weeklyReportTitle) {
        Cell cell = row.getCell(weeklyReportTitle.columnIndex);
        if (cell == null) {
            cell = row.createCell(weeklyReportTitle.columnIndex);
        }
        return cell;
    }

    private Row createRowIfAbsent(XSSFSheet sheet, AtomicInteger rowIndex) {
        Row row = sheet.getRow(rowIndex.getAndIncrement());
        if (row == null) {
            row = sheet.createRow(rowIndex.getAndIncrement());
        }
        return row;
    }

    private Map<WeeklyReportTitle, VisitRecordProcessor> initWeeklyReportProcessors() {
        Map<WeeklyReportTitle, VisitRecordProcessor> map = new HashMap<>();
        map.put(WeeklyReportTitle.DSR, new DsrCollector());
        map.put(WeeklyReportTitle.REAL_VISIT_DAYS, new VisitDaysNumberCalculator());
        map.put(WeeklyReportTitle.DOCTOR_VISIT_NUMBER, new VisitedDoctorNumberCalculator());
        return map;
    }

    private Set<Object> aggregate(Map<AggregateItemKey, Set<Object>> subAggrMap, AggregateItemKey aggregateKey,
                                  Object aggregateItem) {
        Set<Object> visitedHospitals = subAggrMap.get(aggregateKey);
        if (visitedHospitals == null) {
            visitedHospitals = new HashSet<>();
            subAggrMap.put(aggregateKey, visitedHospitals);
        }
        visitedHospitals.add(aggregateItem);
        return visitedHospitals;
    }


    private Map<String, MedicineHolder> loadAllMedicines(int startIndex) {
        Iterator<Medicine> medicineIterator = medicineRepository.findAll().iterator();
        Map<String, MedicineHolder> medicineHolderMap = new LinkedHashMap<>();
        int index = startIndex;
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

    /**
     * Created by Ali on 2014/6/12.
     */
    public static enum AggregateItemKey {
        HOSPITAL("hospitalName"), DOCTOR("doctorName");

        String fieldName;

        AggregateItemKey(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}

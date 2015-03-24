package cn.com.janssen.dsr.report;

public enum WeeklyReportTitle {
    PROVINCE(0, "Province"),
    DSR(1, "DSR"),
    REAL_VISIT_DAYS(2, "Number of Real Visit Days"),
    DOCTOR_VISIT_NUMBER(3, "Number of Visited Doctor"),
    HOSPITAL_VISIT_NUMBER(4, "Number of Visited Hospital");

    int columnIndex;
    String titleName;

    WeeklyReportTitle(int columnIndex, String titleName) {
        this.columnIndex = columnIndex;
        this.titleName = titleName;
    }
}

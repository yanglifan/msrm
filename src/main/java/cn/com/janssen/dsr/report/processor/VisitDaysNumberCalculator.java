package cn.com.janssen.dsr.report.processor;

import cn.com.janssen.dsr.domain.VisitRecord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VisitDaysNumberCalculator implements VisitRecordProcessor<String, String> {
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    private Map<String, Set<Long>> visitDayMap = new HashMap<>();

    @Override
    public String getResult(String key) {
        return visitDayMap.getOrDefault(key, new HashSet<>()).size() + "";
    }

    @Override
    public void accept(VisitRecord visitRecord) {
        String dsrName = visitRecord.getDsr().getUsername();
        Set<Long> visitDaySet = visitDayMap.get(dsrName);
        if (visitDaySet == null) {
            visitDaySet = new HashSet<>();
            visitDayMap.put(dsrName, visitDaySet);
        }

        long timeStampPerDay = visitRecord.getVisitAt().getTime() / MILLIS_PER_DAY;
        visitDaySet.add(timeStampPerDay);
    }
}

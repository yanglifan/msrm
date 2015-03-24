package cn.com.janssen.dsr.report.processor;

import cn.com.janssen.dsr.domain.VisitRecord;

import java.util.function.Consumer;

public interface VisitRecordProcessor<K, V> extends Consumer<VisitRecord> {
    V getResult(K key);
}

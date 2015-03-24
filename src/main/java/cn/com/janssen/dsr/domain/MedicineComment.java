package cn.com.janssen.dsr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class MedicineComment implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private Medicine medicine;

    private String medicineName;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    private VisitRecord visitRecord;

    public MedicineComment() {
    }

    public MedicineComment(String medicineName, String comment) {
        this.medicineName = medicineName;
        this.comment = comment;
    }

    public MedicineComment(VisitRecord visitRecord, Medicine medicine, String comment) {
        this.medicine = medicine;
        this.comment = comment;
        this.visitRecord = visitRecord;
    }

    public MedicineComment(VisitRecord visitRecord, String medicineName, String comment) {
        this(medicineName, comment);
        this.visitRecord = visitRecord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonIgnore
    public VisitRecord getVisitRecord() {
        return visitRecord;
    }

    @JsonIgnore
    public void setVisitRecord(VisitRecord visitRecord) {
        this.visitRecord = visitRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedicineComment that = (MedicineComment) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

package cn.com.janssen.dsr.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import cn.com.janssen.dsr.util.DateJsonDeserializer;
import cn.com.janssen.dsr.util.DateJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class VisitRecord implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User dsr;

    @ManyToOne
    private Doctor doctor;

    @Column(nullable = false)
    private Date visitAt;

    @OneToMany(mappedBy = "visitRecord")
    private List<MedicineComment> medicineComments = new ArrayList<>();

    public VisitRecord() {
    }

    public VisitRecord(VisitRecord visitRecord, Doctor doctor, Hospital hospital) {
        this.id = visitRecord.id;
        this.dsr = visitRecord.dsr;
        this.visitAt = visitRecord.visitAt;
        this.medicineComments = visitRecord.medicineComments;

        this.doctor = doctor;
        this.doctor.setHospital(hospital);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDsr() {
        return dsr;
    }

    public void setDsr(User dsr) {
        this.dsr = dsr;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @JsonSerialize(using = DateJsonSerializer.class)
    public Date getVisitAt() {
        return visitAt;
    }

    @JsonDeserialize(using = DateJsonDeserializer.class)
    public void setVisitAt(Date visitAt) {
        this.visitAt = visitAt;
    }

    public List<MedicineComment> getMedicineComments() {
        return medicineComments;
    }

    public void setMedicineComments(List<MedicineComment> medicineComments) {
        this.medicineComments = medicineComments;
    }
}

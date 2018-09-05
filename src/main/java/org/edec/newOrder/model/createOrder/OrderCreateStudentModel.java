package org.edec.newOrder.model.createOrder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
public class OrderCreateStudentModel {
    private Long id;
    private Long idStudentcard;

    private String groupname;
    private String prevOrderNumber;
    private String fio;
    private String family;
    private String name;

    private Boolean sirota, invalid, transfer, nextGovernmentFinanced, governmentFinanced, getSocialPrev, deductedCurSem;

    private Integer semesternumber;
    private Integer typeInvalid;
    private Integer sessionResult;
    private Integer sessionResultPrev;
    private Integer qualification;

    private Date dateCompleteSession;
    private Date dateOfEndSession;
    private Date dateNextEndOfSession;
    private Date dateOfEndEducation;
    private Date dateOfEndElimination;
    private Date prolongationEndDate;
    private Date firstDate;
    private Date secondDate;
    private Date birthDate;
    private Date prevOrderDateSign;
    private Date prevOrderTransferTo;
    private Date prevOrderTransferToProl;
    private Date dateOfBeginSchoolYear;

    private Long idLastDicAction;

    private Double periodOfStudy;

    @Override
    public boolean equals (Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OrderCreateStudentModel)) {
            return false;
        }

        return ((OrderCreateStudentModel) obj).id.equals(this.id);
    }

    @Override
    public int hashCode () {
        return this.id.hashCode();
    }
}

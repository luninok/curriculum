package org.edec.teacher.model.commission;

/**
 * @author Max Dimukhametov
 */
public class StudentModel {
    private Integer rating;

    private Long idSRH;

    private String fio;
    private String groupname;
    private String ratingStr;

    public StudentModel () {
    }

    public Integer getRating () {
        return rating;
    }

    public void setRating (Integer rating) {
        this.rating = rating;
    }

    public Long getIdSRH () {
        return idSRH;
    }

    public void setIdSRH (Long idSRH) {
        this.idSRH = idSRH;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public String getRatingStr () {
        return ratingStr;
    }

    public void setRatingStr (String ratingStr) {
        this.ratingStr = ratingStr;
    }
}

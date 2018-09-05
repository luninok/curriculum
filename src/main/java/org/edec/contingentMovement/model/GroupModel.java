package org.edec.contingentMovement.model;

/**
 * Created by dmmax
 */
public class GroupModel {
    private int semester;

    private Long idDG;
    private Long idLgs;
    private Long otherdbid;

    private String groupname;

    private Integer nullcount;

    public Integer getNullcount () {
        return nullcount;
    }

    public void setNullcount (Integer nullcount) {
        this.nullcount = nullcount;
    }

    public GroupModel () {
    }

    public Long getIdDG () {
        return idDG;
    }

    public void setIdDG (Long idDG) {
        this.idDG = idDG;
    }

    public String getGroupname () {
        return groupname;
    }

    public void setGroupname (String groupname) {
        this.groupname = groupname;
    }

    public Long getIdLgs () {
        return idLgs;
    }

    public void setIdLgs (Long idLgs) {
        this.idLgs = idLgs;
    }

    public Long getOtherdbid () {
        return otherdbid;
    }

    public void setOtherdbid (Long otherdbid) {
        this.otherdbid = otherdbid;
    }

    public int getSemester () {
        return semester;
    }

    public void setSemester (int semester) {
        this.semester = semester;
    }
}

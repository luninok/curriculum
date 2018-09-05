package org.edec.main.model;

import java.util.ArrayList;
import java.util.List;


public class ModuleModel {
    private boolean readonly;
    private boolean selected;

    private Integer formofstudy;
    private Integer type;

    private Long idModule;

    private String imagePath;
    private String name;
    private String url;

    private RoleModel role;
    private List<DepartmentModel> departments = new ArrayList<>();

    public ModuleModel () {
    }

    public boolean isReadonly () {
        return readonly;
    }

    public void setReadonly (boolean readonly) {
        this.readonly = readonly;
    }

    public Integer getFormofstudy () {
        return formofstudy;
    }

    public void setFormofstudy (Integer formofstudy) {
        this.formofstudy = formofstudy;
    }

    public String getImagePath () {
        return imagePath;
    }

    public void setImagePath (String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public List<DepartmentModel> getDepartments () {
        return departments;
    }

    public void setDepartments (List<DepartmentModel> departments) {
        this.departments = departments;
    }

    public RoleModel getRole () {
        return role;
    }

    public void setRole (RoleModel role) {
        this.role = role;
    }

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

    public boolean isSelected () {
        return selected;
    }

    public void setSelected (boolean selected) {
        this.selected = selected;
    }

    public Long getIdModule () {
        return idModule;
    }

    public void setIdModule (Long idModule) {
        this.idModule = idModule;
    }
}

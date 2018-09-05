package org.edec.newOrder.model;

import org.edec.newOrder.model.enums.ComponentEnum;

public class OrderVisualParamModel {
    private String name;
    private ComponentEnum editComponent;

    public OrderVisualParamModel () { }

    public OrderVisualParamModel (String name, ComponentEnum editComponent) {
        this.name = name;
        this.editComponent = editComponent;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public ComponentEnum getEditComponent () {
        return editComponent;
    }

    public void setEditComponent (ComponentEnum editComponent) {
        this.editComponent = editComponent;
    }
}

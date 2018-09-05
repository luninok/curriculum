package org.edec.newOrder.service;

import org.edec.newOrder.model.enums.ComponentEnum;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

public class ComponentProvider {
    private final String ATR_TYPE = "type";

    public XulElement provideComponent (ComponentEnum componentEnum) {
        if (componentEnum == null) {
            return null;
        }

        XulElement el = null;

        switch (componentEnum) {
            case DATEBOX:
                el = new Datebox();
                break;
            case CHECKBOX:
                el = new Checkbox();
                break;
            case TEXTBOX:
                el = new Textbox();
                break;
        }

        el.setAttribute(ATR_TYPE, componentEnum);

        return el;
    }

    public Object getValueComponent (XulElement element) {
        if (element.getAttribute(ATR_TYPE) == null) {
            return null;
        }

        switch ((ComponentEnum) element.getAttribute(ATR_TYPE)) {
            case CHECKBOX:
                return ((Checkbox) element).isChecked();
            case DATEBOX:
                return ((Datebox) element).getValue();
            case TEXTBOX:
                return ((Textbox) element).getValue();
        }

        return null;
    }

    public Listheader createListheader (Listhead lh, String name, String sclass, String style) {
        Listheader lhr = new Listheader();
        Label label = new Label(name);
        label.setParent(lhr);
        label.setSclass(sclass);
        lhr.setParent(lh);
        lhr.setStyle(style);

        return lhr;
    }

    public Listcell createListcell (Listitem li, String name, String style) {
        final Listcell lc = new Listcell(name);
        lc.setParent(li);
        lc.setStyle(style);

        return lc;
    }

    public Listcell createListcell (Listitem li, XulElement component, String style) {
        final Listcell lc = new Listcell();
        lc.appendChild(component);
        lc.setParent(li);
        lc.setStyle(style);

        return lc;
    }
}

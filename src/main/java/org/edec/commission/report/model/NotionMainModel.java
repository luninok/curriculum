package org.edec.commission.report.model;

import java.util.List;

/**
 * Created by apple on 16.10.17.
 */
public class NotionMainModel {
    List<NotionModel> notions;

    public List<NotionModel> getNotions () {
        return notions;
    }

    public void setNotions (List<NotionModel> notions) {
        this.notions = notions;
    }
}

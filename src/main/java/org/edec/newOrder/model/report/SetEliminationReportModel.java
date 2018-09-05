package org.edec.newOrder.model.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class SetEliminationReportModel {
    private Integer course;

    private String description;
    private String fio;
    private String foundation;
    private String groupname;
    private String recordbook;

    private Date beginYear, endYear;

    private Date firstDateStudent;

    private Boolean governmentFinanced;
}

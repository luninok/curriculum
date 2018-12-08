package org.edec.studyLoad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmploymentModel {
    private String shorttitle;
    private String byworker;
    private String rolename;
    private double wagerate;
    private double time_wagerate;
    private double deviation;
    private double maximum_load;
}

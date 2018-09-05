package org.edec.schedule.model.synch;

import lombok.Data;

/**
 * @author Max Dimukhametov
 */
@Data
public class EfficiencyModel {

    private Integer performance;

    private Long lessonCount;
    private Long visitCount;

    private Long idEsoCourse;
    private Long idEsoStudent;
    private Long idSR;
    private Long idSRefficiency;
    private Long idSSS;

    private String fio;
    private String jsonGrades;
}

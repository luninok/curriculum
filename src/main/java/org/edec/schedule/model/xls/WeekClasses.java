package org.edec.schedule.model.xls;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;

/**
 * @author Max Dimukhametov
 */
@Data
public class WeekClasses {
    private Cell week;

    private Integer roomIndex;
    private Integer subjectIndex;
    private Integer teacherIndex;
}

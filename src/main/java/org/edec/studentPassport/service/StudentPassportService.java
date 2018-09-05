package org.edec.studentPassport.service;

import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.component.model.RatingModel;

import java.util.List;


public interface StudentPassportService {
    List<StudentStatusModel> getStudentsByFilter (String fio, String recordbook, String groupname);
    boolean saveStudentInfo (StudentStatusModel studentStatusModel);
    List<RatingModel> getRatingByHumAndDG (Long idHum, Long idDG, boolean debt);
}

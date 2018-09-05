package org.edec.register.service;

import org.edec.register.model.RegisterModel;
import org.edec.register.model.StudentModel;
import org.edec.register.model.SubjectModel;
import org.edec.register.model.mine.StudentMineModel;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by antonskripacev on 10.06.17.
 */
public interface RegisterService {
    int OPENED = 0;
    int SIGNED = 1;
    int OUT_OF_DATE = 2;
    int MAIN = 3;
    int MAIN_RETAKE = 4;
    int INDIVIDUAL_RETAKE = 5;
    int COMMISSION = 6;
    int BACHELOR = 7;
    int MASTER = 8;
    int SPECIALITY = 9;
    int WITH_NUMBER = 10;

    List<RegisterModel> getRetakesByFilter (Long idInstitute, int formOfStudy, Long idSemester, String groupname, String fioStudent,
                                            String fioTeacher, boolean[] checkBoxes);
    void updateDatesRegister (Date dateBegin, Date dateEnd, Long idSRH);
    boolean cancelRegister (RegisterModel model);
    File getFileRegister (String registerUrl, Long idRegister);
    boolean openRetake (SubjectModel subjectModel, int typeRetake, Date dateOfBegin, Date dateOfEnd, String userFio);
    boolean removeIndivRetake (RegisterModel registerModel, String fioUser);
    List<StudentMineModel> getFilteredStudentsForRegisterFromMine(RegisterModel register);
}

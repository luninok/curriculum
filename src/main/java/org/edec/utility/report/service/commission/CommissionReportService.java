package org.edec.utility.report.service.commission;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.edec.teacher.manager.EntityManagerCompletion;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.commission.EmployeeModel;
import org.edec.utility.report.manager.CommissionOrderDAO;
import org.edec.utility.report.model.commission.ListProtocolModel;
import org.edec.utility.report.model.commission.ProtocolModel;
import org.edec.utility.report.model.commission.ScheduleChairModel;
import org.edec.utility.report.model.commission.ScheduleSubjectModel;
import org.edec.utility.report.model.commission.dao.CommissionScheduleEso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Dimukhametov
 */
public class CommissionReportService {
    private EntityManagerCompletion emCompletion = new EntityManagerCompletion();
    private CommissionOrderDAO commissionOrderDAO = new CommissionOrderDAO();

    public JRBeanCollectionDataSource getListProtocol (Long idCommissionRegister) {
        List<EmployeeModel> employees = emCompletion.getEmployeeByCommission(idCommissionRegister);
        List<EmployeeModel> commission = new ArrayList<>();
        String fioChairman = null;
        for (EmployeeModel employeeModel : employees) {
            if (employeeModel.getChairman()) {
                fioChairman = employeeModel.getFio();
            } else {
                commission.add(employeeModel);
            }
        }

        List<ProtocolModel> list = commissionOrderDAO.getProtocols(idCommissionRegister);

        for (ProtocolModel protocol : list) {
            protocol.setChairman(fioChairman);
            protocol.setCommission(commission);
            protocol.setFullCommission(employees);
        }

        List<ListProtocolModel> data = new ArrayList<>();
        ListProtocolModel listProtocolModel = new ListProtocolModel();
        listProtocolModel.setProtocols(list);
        data.add(listProtocolModel);
        return new JRBeanCollectionDataSource(data);
    }

    public JRBeanCollectionDataSource getRegister (CommissionModel commission) {
        List<CommissionModel> data = new ArrayList<>();
        data.add(commission);
        return new JRBeanCollectionDataSource(data);
    }

    public JRBeanCollectionDataSource getSchedule (Integer formOfStudy, Long idChair) {
        List<CommissionScheduleEso> modelList = commissionOrderDAO.getScheduleModel(formOfStudy, idChair);

        List<ScheduleChairModel> chairs = new ArrayList<>();
        for (CommissionScheduleEso model : modelList) {
            boolean addChair = true;
            for (ScheduleChairModel chair : chairs) {
                if (chair.getFulltitle().equals(model.getFulltitle())) {
                    ScheduleSubjectModel subject = new ScheduleSubjectModel();
                    subject.setClassroom(model.getClassroom());
                    subject.setDatecommission(model.getDatecommission());
                    subject.setSubjectname(model.getSubjectname());
                    chair.getSubjects().add(subject);
                    addChair = false;
                    break;
                }
            }
            if (addChair) {
                ScheduleChairModel chair = new ScheduleChairModel();
                chair.setFulltitle(model.getFulltitle());
                ScheduleSubjectModel subject = new ScheduleSubjectModel();
                subject.setClassroom(model.getClassroom());
                subject.setDatecommission(model.getDatecommission());
                subject.setSubjectname(model.getSubjectname());
                chair.getSubjects().add(subject);
                chairs.add(chair);
            }
        }
        return new JRBeanCollectionDataSource(chairs);
    }
}

package org.edec.passportGroup.service.impl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.service.ReportsService;
import org.edec.utility.fileManager.FilePath;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.workflow.service.impl.WorkflowServiceEnsembleImpl;
import org.json.JSONObject;
import org.zkoss.zul.Listitem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Roman Assaulyanov on 09.06.2017.
 */
public class ReportsServiceESO implements ReportsService {

    private static final String URL_GET_REPORT = "report.getReport";

    private static final Logger log = Logger.getLogger(WorkflowServiceEnsembleImpl.class.getName());
    private Properties properties;
    private UserModel currentUser;

    public ReportsServiceESO () {
        currentUser = new TemplatePageCtrl().getCurrentUser();
        try {
            properties = new FilePath().getDataServiceProp();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
        }
    }

    @Override
    public String getGoncharicReport (int InstId, String TextInst, long Sem, String SemText, int FoSId) {
        String Inst = "ИКИТ";
        if (InstId > 0) {
            Inst = TextInst;
        }
        int FoS = 1;
        if (FoSId > 0) {
            FoS = FoSId;
        }
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Inst", Inst));
        params.add(new BasicNameValuePair("Sem", Long.toString(Sem)));
        params.add(new BasicNameValuePair("SemText", SemText));
        params.add(new BasicNameValuePair("FoS", Integer.toString(FoS)));

        try {
            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(URL_GET_REPORT) + "report", HttpClient.POST, params, null));
            if (!jsonObject.has("children")) {
                return "";
            }

            return jsonObject.getString("children");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
            return "";
        }
    }

    @Override
    public String getDebtorsReport (int TypeOfSemester, long idInstitute, int formOfStudy, int Course, String group, boolean isBachelor,
                                    boolean isMaster, boolean isEngineer, String TextFoS) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("ToS", Integer.toString(TypeOfSemester - 1)));
        params.add(new BasicNameValuePair("idInst", Long.toString(idInstitute)));
        params.add(new BasicNameValuePair("TextFoS", TextFoS));
        params.add(new BasicNameValuePair("FoS", Integer.toString(formOfStudy)));
        params.add(new BasicNameValuePair("Course", Integer.toString(Course)));
        params.add(new BasicNameValuePair("Group", group));
        String qualification = (isBachelor ? "1," : "") + (isMaster ? "2," : "") + (isEngineer ? "3," : "");
        qualification = qualification.substring(0, qualification.length() - 1);
        params.add(new BasicNameValuePair("Qual", qualification));
        try {
            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(URL_GET_REPORT) + "report2", HttpClient.POST, params, null));
            if (!jsonObject.has("children")) {
                return "";
            }
            String res = jsonObject.getString("children");

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
            return "";
        }
    }

    @Override
    public String getReportDeb (Integer semester, Integer gov, Integer type, List<Listitem> grouplist) {
        Listitem firstItem = grouplist.get(0);
        StringBuilder grps = new StringBuilder("'" + ((GroupModel) firstItem.getValue()).getGroupName() + "'");
        for (Listitem listitem : grouplist) {
            grps.append(",'").append(((GroupModel) listitem.getValue()).getGroupName()).append("'");
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Sem", semester.toString()));
        params.add(new BasicNameValuePair("GovFinanced", gov.toString()));
        params.add(new BasicNameValuePair("Typ", type.toString()));
        params.add(new BasicNameValuePair("Groups", grps.toString()));

        try {
            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(URL_GET_REPORT) + "debtor", HttpClient.POST, params, null));
            if (!jsonObject.has("children")) {
                return "";
            }

            return jsonObject.getString("children");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getFormControlReport (long Sem) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Sem", Long.toString(Sem)));
        try {
            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(URL_GET_REPORT) + "formcontrol", HttpClient.POST, params, null));
            if (!jsonObject.has("children")) {
                return "";
            }
            String res = jsonObject.getString("children");

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(currentUser.getFio(), e);
            return "";
        }
    }

    @Override
    public String getDecanReport (List<Listitem> grouplist, int size) {

        Listitem firstItem = grouplist.get(0);
        String grps = "'" + ((GroupModel) firstItem.getValue()).getGroupName() + "'";
        for (Listitem ignored : grouplist) {
            grps += ",'" + ((GroupModel) firstItem.getValue()).getGroupName() + "'";
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Groups", grps));

        try {

            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(URL_GET_REPORT) + "report3", HttpClient.POST, params, null));

            if (!jsonObject.has("children")) {
                return "";
            }
            String res = jsonObject.getString("children");

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getStudentlistReport (List<Listitem> grouplist, int sem) {
        Listitem firstItem = grouplist.get(0);
        String grps = "'" + ((GroupModel) firstItem.getValue()).getGroupName() + "'";
        for (Listitem listitem : grouplist) {
            grps += ",'" + ((GroupModel) listitem.getValue()).getGroupName() + "'";
        }
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Sem", sem + ""));
        params.add(new BasicNameValuePair("Groups", grps));
        try {

            JSONObject jsonObject = new JSONObject(
                    HttpClient.makeHttpRequest(properties.getProperty(URL_GET_REPORT) + "studentlist", HttpClient.POST, params, null));

            if (!jsonObject.has("children")) {
                return "";
            }

            return jsonObject.getString("children");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

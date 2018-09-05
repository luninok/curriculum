package org.edec.profile.manager;

import org.edec.dao.DAO;
import org.edec.profile.model.ProfileModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import java.util.Date;
import java.util.List;


public class ProfileManager extends DAO {
    public ProfileModel getProfileByHumId (Long idHum) {
        String query = "SELECT family||' '||name||' '||patronymic AS fio, birthday AS birthDay, \n" +
                       "\temail, get_notification AS getNotification\n" + "FROM humanface\n" + "WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query)
                              .addScalar("fio")
                              .addScalar("birthDay")
                              .addScalar("email")
                              .addScalar("getNotification")
                              .setResultTransformer(Transformers.aliasToBean(ProfileModel.class));
        q.setLong("idHum", idHum);
        List<?> list = getList(q);
        return list.size() == 0 ? null : (ProfileModel) list.get(0);
    }

    public boolean updateBirthDay (Long idHum, Date dateBirthDay) {
        String query = "UPDATE humanface SET birthday = :birthday WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idHum", idHum).setDate("birthday", dateBirthDay);
        return executeUpdate(q);
    }

    public boolean updateEmail (Long idHum, String email) {
        String query = "UPDATE humanface SET email = :email WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idHum", idHum).setParameter("email", email, StringType.INSTANCE);
        return executeUpdate(q);
    }

    public boolean updateGetNotification (Long idHum, Boolean getNotification) {
        String query = "UPDATE humanface SET get_notification = :getNotification WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setLong("idHum", idHum).setParameter("getNotification", getNotification);
        return executeUpdate(q);
    }

    public boolean updateStartPage (String path, Long idHum) {
        String query = "UPDATE humanface SET start_page = :path WHERE id_humanface = :idHum";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("path", path, StringType.INSTANCE).setLong("idHum", idHum);
        return executeUpdate(q);
    }

    public boolean updateStartPageForParent (String path, Long idParent) {
        String query = "UPDATE parent SET start_page = :path WHERE id_parent = :idParent";
        Query q = getSession().createSQLQuery(query);
        q.setParameter("path", path, StringType.INSTANCE).setLong("idParent", idParent);
        return executeUpdate(q);
    }
}

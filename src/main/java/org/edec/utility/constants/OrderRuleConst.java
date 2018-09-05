package org.edec.utility.constants;

/**
 * Created by antonskripacev on 05.03.17.
 */
public enum OrderRuleConst {
    ACADEMIC_NOT_IN_SESSION("Академическая стипендия(не в сессию)", 19L), ACADEMIC_IN_SESSION(
            "Академическая стипендия(сессия)", 8L), DEDUCTION_INITIATIVE("Отчисление по собственному желанию", 13L), SOCIAL_IN_SESSION(
            "Социальный приказ(сессия)", 20L), SOCIAL_NEW_REFERENCE("Социальный приказ(новые справки)", 21L), SOCIAL_INCREASED_IN_SESSION(
            "Социальный повышенный приказ(сессия)", 22L), SOCIAL_INCREASED_NEW_REFERENCE(
            "Социальный повышенный приказ(новые справки)", 23L), TRANSFER(
            "О переводе на следующий курс", 24L), TRANSFER_CONDITIONALLY(
            "О переводе на следующий курс(условно)", 25L), TRANSFER_AFTER_TRANSFER_CONDITIONALLY_RESPECTFUL(
            "Перевод ранее условно переведённых студентов(уваж. причины)", 29L), TRANSFER_AFTER_TRANSFER_CONDITIONALLY_NOT_RESPECTFUL(
            "Перевод ранее условно переведённых студентов(не уваж. причины)", 30L), TRANSFER_PROLONGATION(
            "Продление сроков ликвидации", 31L), SET_ELIMINATION_RESPECTFUL(
            "Установление сроков ЛАЗ(уваж.)", 32L), SET_ELIMINATION_NOT_RESPECTFUL(
            "Установление сроков ЛАЗ(не уваж.)", 33L), PROLONGATION_ELIMINATION_WINTER(
            "Продление срок ЛАЗ(по результатам зим. сессии)", 35L), CANCEL_ACADEMICAL_SCHOLARSHIP_IN_SESSION(
            "Отмена гос. академ. стипендии(сессия)", 42L), TRANSFER_CONDITIONALLY_RESPECTFUL(
            "О переводе на следующий курс(условно, уваж.)", 45L);

    private Long id;
    private String name;

    public String getName () {
        return name;
    }

    public Long getId () {
        return id;
    }

    OrderRuleConst (String name, long id) {
        this.name = name;
        this.id = id;
    }

    public static OrderRuleConst getByName (String name) {
        for (OrderRuleConst orderRuleConst : OrderRuleConst.values()) {
            if (orderRuleConst.getName().equals(name)) {
                return orderRuleConst;
            }
        }
        return null;
    }

    public static OrderRuleConst getById (Long id) {
        for (OrderRuleConst orderRuleConst : OrderRuleConst.values()) {
            if (orderRuleConst.getId().equals(id)) {
                return orderRuleConst;
            }
        }
        return null;
    }
}

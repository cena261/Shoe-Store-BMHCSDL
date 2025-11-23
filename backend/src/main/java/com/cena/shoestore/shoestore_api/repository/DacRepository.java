package com.cena.shoestore.shoestore_api.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DacRepository {

    EntityManager entityManager;

    public List<Object[]> findCurrentUserRoles() {
        String sql = "SELECT GRANTED_ROLE, ADMIN_OPTION, DEFAULT_ROLE " +
                "FROM USER_ROLE_PRIVS " +
                "ORDER BY GRANTED_ROLE";

        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    public List<Object[]> findCurrentUserTablePrivileges() {
        String sql = "SELECT TABLE_NAME, PRIVILEGE, GRANTABLE " +
                "FROM USER_TAB_PRIVS " +
                "WHERE TABLE_NAME IN ('V_USERS_SAFE', 'V_ORDERS_SUMMARY') " +
                "ORDER BY TABLE_NAME, PRIVILEGE";

        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    public List<Object[]> findSampleUsersViewData() {
        String sql = "SELECT USERID, FULLNAME, CREATEDAT, LASTLOGIN " +
                "FROM V_USERS_SAFE " +
                "WHERE ROWNUM <= 5 " +
                "ORDER BY CREATEDAT DESC";

        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    public List<Object[]> findSampleOrdersViewData() {
        String sql = "SELECT ORDERID, USERID, ORDERDATE, TOTALAMOUNT, ORDERSTATUS " +
                "FROM V_ORDERS_SUMMARY " +
                "WHERE ROWNUM <= 5 " +
                "ORDER BY ORDERDATE DESC";

        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    public List<Object[]> findCurrentUserViewPrivileges() {
        String sql = "SELECT VIEW_NAME, PRIVILEGE, GRANTABLE " +
                "FROM USER_TAB_PRIVS " +
                "WHERE TABLE_NAME IN (SELECT VIEW_NAME FROM USER_VIEWS) " +
                "ORDER BY VIEW_NAME";

        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
}

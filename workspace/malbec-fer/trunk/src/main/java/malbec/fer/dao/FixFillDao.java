package malbec.fer.dao;

import static malbec.util.SqlUtil.findSqlException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import malbec.fix.message.FixFill;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixFillDao {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EntityManagerFactory emf;

    private static FixFillDao instance = new FixFillDao();

    private FixFillDao() {
        emf = Persistence.createEntityManagerFactory("BADB");
    }

    public static FixFillDao getInstance() {
        return instance;
    }

    
    public long persist(FixFill fill) {
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            em.persist(fill);
            tx.commit();

            return fill.getId();
        } catch (RuntimeException e) {
            tx.rollback();
            Throwable sql = findSqlException(e);
            if (sql != null) {
                log.error("Unable to save new order:" + fill.toString(), sql);
                SQLException sqlE = (SQLException) sql;
                if (sqlE.getErrorCode() == 2601) {
                    // don't send an email
                    return -1;
                }
            }
            log.error("Unable to save new execution report:" + fill.toString(), e);
            throw e;
        } finally {
            em.close();
        }
    }

    public FixFill findById(String beginString, String senderCompId, String senderSubId, Date tradeDate, String executionId) {
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        StringBuilder queryString = new StringBuilder(256);

        queryString.append("from FixFill as fills where fills.beginString = :beginString ");
        queryString.append("and fills.senderCompId = :senderCompId ");
        queryString.append("and fills.tradeDate = :tradeDate ");
        queryString.append("and fills.executionId = :executionId ");
        
        if (senderSubId == null) {
            queryString.append("and fills.senderSubId is null");
        } else {
            queryString.append("and fills.senderSubId = :senderSubId");
        }
        Query query = em.createQuery(queryString.toString());
        
        query.setParameter("beginString", beginString);
        query.setParameter("senderCompId", senderCompId);
        query.setParameter("tradeDate", new DateTime(tradeDate).toDateMidnight().toDate());
        query.setParameter("executionId", executionId);
        
        if (senderSubId != null) {
            query.setParameter("senderSubId", senderSubId);
        }

        FixFill dbFill = null;

        // Put in our own logic as we may get no results
        List<?> results = query.getResultList();
        if (results.size() > 0) {
            dbFill = (FixFill) results.get(0);
        }

        tx.commit();
        em.close();

        return dbFill;
    }
}

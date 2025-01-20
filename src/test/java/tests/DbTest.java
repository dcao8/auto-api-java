package tests;

import model.user.dao.CustomerAddressDao;
import model.user.dao.CustomerDao;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;


import java.util.UUID;

import static java.lang.System.out;

public class DbTest {
    @Test
    void checkDBConnection() {
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .build();
        try {
            SessionFactory sessionFactory =
                    new MetadataSources(registry)
                            .addAnnotatedClass(CustomerDao.class)
                            .addAnnotatedClass(CustomerAddressDao.class)
                            .buildMetadata()
                            .buildSessionFactory();
            sessionFactory.inTransaction(session -> {
                String hql = "FROM CustomerDao c JOIN FETCH c.addresses WHERE c.id=: id";
                Query query = session.createQuery(hql, CustomerDao.class);
                query.setParameter("id", UUID.fromString("b82ed619-fbe6-48b2-a800-c0ee8315a0b5"));
                CustomerDao customer = (CustomerDao) query.getSingleResult();
                out.println();
            });
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
package tests;

import model.user.dao.CustomerDao;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

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
                            .buildMetadata()
                            .buildSessionFactory();
            sessionFactory.inTransaction(session -> {
                session.createSelectionQuery("from CustomerDao", CustomerDao.class)
                        .getResultList()
                        .forEach(customer -> {
                            out.println("Id:" + customer.getId());
                        });
            });
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
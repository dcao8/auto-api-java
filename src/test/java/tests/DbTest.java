package tests;

import model.user.dao.CustomerAddressDao;
import model.user.dao.CustomerDao;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import utils.DbUtils;


import java.util.UUID;

import static java.lang.System.out;

public class DbTest {
    @Test
    void checkDBConnection() {
        CustomerDao customer = DbUtils.getCustomerFormDb("b82ed619-fbe6-48b2-a800-c0ee8315a0b5");
        out.println();
    }
}
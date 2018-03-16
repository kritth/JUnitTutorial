package com.mcit.tutorial.junit.config;

import com.mcit.tutorial.junit.model.Course;
import com.mcit.tutorial.junit.model.Department;
import com.mcit.tutorial.junit.model.Student;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

/**
 * Configuration class
 *
 * @author Kasidit
 */
public class HibernateConfiguration {
    private static final String DRIVER = "org.h2.Driver";
    private static final String URL = "jdbc:h2:~/db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String DIALECT = "org.hibernate.dialect.H2Dialect";

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    private static HibernateConfiguration instance;

    public static HibernateConfiguration getInstance() {
        return instance;
    }

    private HibernateConfiguration() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

                // Configurations
                Configuration configuration = new Configuration();

                Properties prop = new Properties();
                prop.put(Environment.DRIVER, HibernateConfiguration.DRIVER);
                prop.put(Environment.URL, HibernateConfiguration.URL);
                prop.put(Environment.USER, HibernateConfiguration.USER);
                prop.put(Environment.PASS, HibernateConfiguration.PASSWORD);
                prop.put(Environment.DIALECT, HibernateConfiguration.DIALECT);
                prop.put(Environment.SHOW_SQL, "false");
                prop.put(Environment.HBM2DDL_AUTO, "create");

                configuration.addProperties(prop);

                // Add entities
                configuration.addAnnotatedClass(Course.class);
                configuration.addAnnotatedClass(Student.class);
                configuration.addAnnotatedClass(Department.class);

                // Create registry
                registry = registryBuilder.applySettings(configuration.getProperties()).build();

                // Create data source
                sessionFactory = configuration.buildSessionFactory(registry);
            } catch (Exception e) {
                e.printStackTrace();
                shutdown();
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void startup() {
        instance = new HibernateConfiguration();
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
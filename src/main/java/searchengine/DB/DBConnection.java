package searchengine.DB;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection  {
    public static SessionFactory getSessionFactory(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("C:\\Games\\skillbox\\searchengine\\src\\main\\resources\\hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        return sessionFactory;
    }
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection("jdbc:mysql://localhost:3306/maindatabase","root", "Hd906betterr");
    }
    public static void addSite(Site site) {
        {
            Session session = DBConnection.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(site);
            transaction.commit();
            session.close();


        }
    }


}

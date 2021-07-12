package DataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@Slf4j
public class Jdbc {

    private DataSource dataSource;
    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedJdbcTemplate;

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;


    @Before
    public void setUpDataSourceConnections() throws SQLException {

        try {
            log.info("\n Creating IBM DB2 JCC DataSource");
            dataSource = new com.ibm.db2.jcc.DB2SimpleDataSource();

            ((com.ibm.db2.jcc.DB2BaseDataSource) dataSource).setServerName("a3dvdb1087");
            ((com.ibm.db2.jcc.DB2BaseDataSource) dataSource).setPortNumber(Integer.parseInt("50000"));
            ((com.ibm.db2.jcc.DB2BaseDataSource) dataSource).setDatabaseName("CIDEVDB1");
            ((com.ibm.db2.jcc.DB2BaseDataSource) dataSource).setDriverType(4);

            log.info("\n Connecting to database using JDBC Universal type 4 driver....");
            String user = System.getProperty("user");
            String pass = System.getProperty("pass");
            con = dataSource.getConnection(user, pass);
            log.info("\n Connected to database successfully.");

        } catch (Exception e) {
            log.error("\n Error ..." + e);
            e.printStackTrace();
        }

        /** Use in memory H2 database with mock data instead  **/
         /* DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:resources/jdbc/FraudAnalysis.sql")
            .build();*/
       /* dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(mock(Connection.class));*/

        jdbcTemplate = new JdbcTemplate(dataSource);

        log.info("\n jdbcTemplate = " + jdbcTemplate);
    }

    @Test
    public void testJdbcTemplate() {
        log.info("Querying for fraud analysis records where auth_id = 'A' : ");

        String query = "SELECT COUNT(*) FROM SYSCAT.TABLES";
        jdbcTemplate.query(query, new ResultSetExtractor<String>() {
            @Override
            public String extractData(ResultSet resultSet)
                throws SQLException, DataAccessException {
                return null;
            }
        });

        jdbcTemplate.query(
            "Select AUTH_ID, HRCH_DETL_1_ID, HRCH_DETL_2_ID, HRCH_DETL_3_ID, HRCH_DETL_4_ID from CIWFAQT.THREEDS_FRAUD_ANALYSIS_AUTH where CAVV_DATA = ?",
                new Object[] { "A" },
            (rs, rowNum) -> new FraudAuthAnalysis(
                rs.getLong("AUTH_ID"),
                rs.getString("HRCH_DETL_1_ID"),
                rs.getString("HRCH_DETL_2_ID"),
                rs.getString("HRCH_DETL_3_ID") ,
                rs.getString("HRCH_DETL_4_ID"))
        ).forEach(
            fraudAuthAnalysis ->
                log.info(fraudAuthAnalysis.toString())
        );
    }


    @Test
    public void testPlainSQL() throws SQLException {

        log.info("\n Executing query....");
        stmt = con.createStatement();
        String query = "SELECT COUNT(*) FROM SYSCAT.TABLES";
        rs = stmt.executeQuery(query);
        while (rs.next())
        {
            log.info("\n " + query + " = " + rs.getInt(1));
        }
    }

    @After
    public void closeConnection() throws SQLException {
        rs.close();
        stmt.close();
        log.info("\n Closing connection...");
        con.close();
        log.info("\n Connection closed.");
    }
}

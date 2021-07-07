package DataBase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@Slf4j
public class Jdbc {

    @Autowired
    private DataSource dataSource;
    JdbcTemplate jdbcTemplate;
    TransactionTemplate transactionTemplate;
    NamedParameterJdbcTemplate namedJdbcTemplate;

    @Before
    public void setUpDataSourceConnections() throws SQLException {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:FraudAnalysis.sql")
            .build();
//        dataSource = mock(DataSource.class);
//        when(dataSource.getConnection()).thenReturn(mock(Connection.class));
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testJdbcTemplate() {
        log.info("Querying for fraud analysis records where auth_id = 'A' : ");
        jdbcTemplate.query(
            "Select AUTH_ID, HRCH_DETL_1_ID, HRCH_DETL_2_ID, HRCH_DETL_3_ID, HRCH_DETL_4_ID from CIWFAQT.THREEDS_FRAUD_ANALYSIS_AUTH where CAVV_DATA = ?",
                new Object[] { "A" },
            (rs, rowNum) -> new FraudAuthAnalysis(
                rs.getLong("AUTH_ID"),
                rs.getString("HRCH_DETL_1_ID"),
                rs.getString("HRCH_DETL_2_ID"),
                rs.getString("HRCH_DETL_3_ID") ,
                rs.getString("HRCH_DETL_4_ID"))
        ).forEach(fraudAuthAnalysis -> log.info(fraudAuthAnalysis.toString()));
    }


    @Test
    public void testNamedJdbcTemplate() {

    }
}

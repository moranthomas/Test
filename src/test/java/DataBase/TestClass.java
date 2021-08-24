package DataBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import java.sql.PreparedStatement;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestClass {

    private NamedParameterJdbcTemplate template;
    @BeforeEach
    void emptyTestTable() {
        template.execute("TRUNCATE TABLE TEST.\"NullColumn\" IMMEDIATE", PreparedStatement::execute);
    }
    @Test
    public void connectivityTest() {
        assertThat(template.queryForObject("SELECT 1 FROM SYSIBM.SYSDUMMY1", Map.of(), Integer.class)).isEqualTo(1);
    }
    @Test
    void NullableColumnInsert() {
        template.update("INSERT INTO TEST.\"NullColumn\" (\"Nullable\") VALUES (:nullable)", Map.of("nullable", "ABC"));
        assertThat(template.queryForObject("SELECT COUNT(*) FROM TEST.\"NullColumn\" WHERE \"Nullable\" IS NOT NULL", Map.of(), Integer.class)).isEqualTo(1);
        SqlParameterSource sps = new MapSqlParameterSource().addValue("nullable", null);
        template.update("INSERT INTO TEST.\"NullColumn\" (\"Nullable\") VALUES (:nullable)", sps);
        assertThat(template.queryForObject("SELECT COUNT(*) FROM TEST.\"NullColumn\" WHERE \"Nullable\" IS NULL", Map.of(), Integer.class)).isEqualTo(1);
        assertThat(template.queryForObject("SELECT COUNT(*) FROM TEST.\"NullColumn\"", Map.of(), Integer.class)).isEqualTo(2);
    }
}






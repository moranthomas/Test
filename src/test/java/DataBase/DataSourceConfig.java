package DataBase;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    /** NOTE - This class will not operate correctly outside of a spring container context **/

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        // These two properties can also be externalized out to application.properties.....
        // spring.datasource.url=jdbc:h2:mem:test
        // spring.datasource.driver-class-name=org.h2.Driver
        dataSourceBuilder.url("jdbc:db2://a3dvdb1087:50000/CIDEVDB1");
        dataSourceBuilder.driverClassName("com.ibm.db2.jcc.DB2Driver");
        dataSourceBuilder.username("");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }
}

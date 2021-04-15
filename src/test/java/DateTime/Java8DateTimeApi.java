package DateTime;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Java8DateTimeApi {

    @Test
    public void printAllAvailableZoneIds() {
        Set<String> allZoneIds = ZoneId.getAvailableZoneIds();

        // Iterate Set Using For-Each Loop
        for(String str : allZoneIds) {
           log.info(str);
        }

        // Iterate Set Using the Java Stream API
        Stream<String> stream = allZoneIds.stream();
        stream.forEach((element) -> { log.info(element); });
    }

    @Test
    public void printESTZoneId() {
        Set<String> allZoneIds = ZoneId.getAvailableZoneIds();

        // Iterate Set Using For-Each Loop
        for (String str : allZoneIds) {
            if (str.contains("New_York")) {
                log.info(str);
            }
        }
    }

    @Test
    public void testUsingZonedDateTime() {

        ZoneId zoneId = ZoneId.of("America/New_York");

        // "date": "2021-02-11T06:00:00.000+0000"
        LocalDateTime localDateTime = LocalDateTime.parse("2021-02-11T06:00:00.000");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

        log.info(String.valueOf(zonedDateTime));

    }

    @Test
    public void testUsingZonedDateTimeToSQLTimeStamp() {

        ZoneId zoneId = ZoneId.of("America/New_York");

        // "date": "2021-02-11T06:00:00.000+0000"
        LocalDateTime localDateTime = LocalDateTime.parse("2021-02-11T06:00:00.000");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        //Timestamp timestamp2 = Timestamp.from(zonedDateTime.toInstant());
        Timestamp timestamp2 = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        log.info(String.valueOf(timestamp2));

    }


}

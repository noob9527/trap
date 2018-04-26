package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeSpec {
    @Test
    @DisplayName("using Duration.between to find out the diff between two instants")
    void test100() {
        Instant i1 = Instant.now();
        Instant i2 = i1.plus(1, ChronoUnit.DAYS);
        Duration timeElapsed = Duration.between(i1, i2);
        assertThat(timeElapsed.getSeconds()).isEqualTo(86400);
    }

    @Test
    @DisplayName("using util method to find out the diff between two local dates")
    void test200() {
        LocalDate today = LocalDate.now();
        LocalDate nextDate = today.plusMonths(1);

        Period period = today.until(nextDate);
        assertThat(period.getMonths()).isEqualTo(1);

        long days = today.until(nextDate, ChronoUnit.DAYS);
        assertThat(days).isBetween(28L, 31L);
    }

    @Nested
    class ZonedDateTimeSpec {
        @Test
        @DisplayName("construct nonexistent time test")
        void test100() {
            ZonedDateTime skipped = ZonedDateTime.of(
                    LocalDate.of(2013, 3, 31),
                    LocalTime.of(2, 30),
                    ZoneId.of("Europe/Berlin")
            );
            assertThat(skipped.getHour()).isEqualTo(3);
        }

        @Test
        @DisplayName("ambiguous time caused by the ends of DST")
        void test200() {
            // 2013-10-27T02:30+02:00[Europe/Berlin]
            ZonedDateTime ambiguous = ZonedDateTime.of(
                    LocalDate.of(2013, 10, 27),
                    LocalTime.of(2, 30),
                    ZoneId.of("Europe/Berlin")
            );
            // 2013-10-27T02:30+01:00[Europe/Berlin]
            ZonedDateTime anHourLater = ambiguous.plusHours(1);
            assertThat(ambiguous.getHour()).isEqualTo(anHourLater.getHour());
            assertThat(ambiguous.getOffset()).isLessThan(anHourLater.getOffset());
        }
    }

}

package uplus.nucube.common.trace.uplus;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class LocalDateParser {
    private LocalDate searchDate;

    public LocalDateParser(String currentDate) {
        //this.searchDate = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.searchDate = LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    // 해당날짜의 시작시간(ex:2018-06-15 00:00:00)
    public LocalDateTime startDate() {
        return this.searchDate.atStartOfDay();
    }

    // 해당날짜의 끝 시간(ex:2018-06-15 23:59:59)
    public LocalDateTime endDate() {
        return LocalDateTime.of(this.searchDate, LocalTime.of(23,59,59));
    }
}
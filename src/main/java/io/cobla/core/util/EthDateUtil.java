package io.cobla.core.util;


import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EthDateUtil {

    private static final String YYYY_MM_DD_HH_24 =  "yyyy-MM-dd hh:mm:ss";

    public static String hexToDateSeoul(@NotNull String hexDate,@NotNull String timeZone){
        ZoneId zoneId = ZoneId.of(timeZone );//"UTC","Asia/Seoul"

        Long dateLong = new BigInteger(hexDate.substring(2),16).longValue();
        Instant instant = Instant.ofEpochSecond( dateLong );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );

        LocalDateTime time = zdt.toLocalDateTime();

        DateTimeFormatter format = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_24);

         return format.format(zdt);
    }


    public static LocalDateTime hexToDate(@NotNull String hexDate,@NotNull String timeZone){
        ZoneId zoneId = ZoneId.of(timeZone );//"UTC","Asia/Seoul"

        Long dateLong = new BigInteger(hexDate.substring(2),16).longValue();
        Instant instant = Instant.ofEpochSecond( dateLong );
        ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
        LocalDateTime time = zdt.toLocalDateTime();

        return time;
    }
}

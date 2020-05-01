package com.alamobot.core;

import com.sun.tools.javac.util.StringUtils;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fluttercode.datafactory.impl.DataFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Originally sourced from https://github.com/BancVue/common-spring-boot/blob/master/src/mainTest/groovy/com/bancvue/boot/testsupport/RandomGenerator.groovy
 */
@Slf4j
public class RandomGenerator {

    static final List<String> STATES = Arrays.asList(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
            "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
            "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    );
    public static final String[] PROTOCOLS = {"http", "https"};
    public static final String[] TOP_LEVEL_DOMAINS = {"com", "org", "net"};

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final String TEST_REALM = "t";
    private static final String PROD_REALM = "p";

    private static final ZonedDateTime PAST_ZONED_DATE_TIME = ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final ZonedDateTime FUTURE_ZONED_DATE_TIME = ZonedDateTime.of(2050, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    private static final Random random = new Random();
    private static final DataFactory df = new DataFactory();
    private static final Lorem lorem = LoremIpsum.getInstance();
    private static final AtomicLong randomSeed;

    static {
        setRandomField(df, random);
        setRandomField(lorem, random);
        randomSeed = getRandomSeed(random);
    }

    @SneakyThrows
    private static void setRandomField(Object object, Random random) {
        Field field = object.getClass().getDeclaredField("random");
        field.setAccessible(true);
        field.set(object, random);
    }

    @SneakyThrows
    private static AtomicLong getRandomSeed(Random random) {
        Field field = Random.class.getDeclaredField("seed");
        field.setAccessible(true);
        return (AtomicLong) field.get(random);
    }

    public static void setSeed(long seed) {
        log.info("Setting RandomGenerator seed to {}", seed);
        // set the seed directly rather than invoking random.setSeed b/c setSeed scrambles the seed and we want a
        // specific seed in order to guarantee deterministic randomness
        randomSeed.set(seed);
    }

    @SneakyThrows
    public static long getSeed() {
        return randomSeed.get();
    }


    public RandomGenerator() {
    }

    public UUID uuid() {
        // copied from java.util.UUID.randomUUID
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        randomBytes[6]  &= 0x0f;  /* clear version        */
        randomBytes[6]  |= 0x40;  /* set to version 4     */
        randomBytes[8]  &= 0x3f;  /* clear variant        */
        randomBytes[8]  |= 0x80;  /* set to IETF variant  */
        // copied from java.util.UUID.UUID(byte[])
        long mostSigBits = 0;
        long leastSigBits = 0;
        for (int i=0; i<8; i++)
            mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
        for (int i=8; i<16; i++)
            leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);

        return new UUID(mostSigBits, leastSigBits);
    }

    public UUID optionalUuid() {
        return shouldBeNull() ? null : uuid();
    }

    public String uuidString() {
        return uuid().toString();
    }

    public String optionalUuidString() {
        return shouldBeNull() ? null : uuidString();
    }

    public long id() {
        return toId(random.nextLong());
    }


    public Long optionalId() {
        return shouldBeNull() ? null : id();
    }

    public int intId() {
        return toId(random.nextInt());
    }

    public String intIdString() {
        return String.valueOf(intId());
    }

    public Integer optionalIntId() {
        return shouldBeNull() ? null : intId();
    }

    private int toId(int number) {
        if (number < 0) {
            number *= -1;
        } else if (number == 0) {
            number = 1;
        }
        return number;
    }

    private long toId(long number) {
        if (number < 0) {
            number *= -1;
        } else if (number == 0) {
            number = 1;
        }
        return number;
    }

    public String alamoId() {
        return alamoIdNumber().toString();
    }

    public Integer alamoIdNumber() {
        return intBetween(100000, 999999);
    }

    public int tinyInt() {
        return intBetween(0, 50);
    }

    public Integer optionalTinyInt() {
        return shouldBeNull() ? null : tinyInt();
    }

    public int negativeInt() {
        return intBetween(Integer.MIN_VALUE, -1);
    }

    public Integer optionalNegativeInt() {
        return shouldBeNull() ? null : negativeInt();
    }

    public int positiveInt() {
        return intBetween(1, Integer.MAX_VALUE);
    }

    public Integer optionalPositiveInt() {
        return shouldBeNull() ? null : positiveInt();
    }

    public Integer intBetween(int min, int max) {
        return df.getNumberBetween(min, max);
    }

    public Integer optionalIntBetween(int min, int max) {
        return shouldBeNull() ? null : intBetween(min, max);
    }

    public int nextInt() {
        return random.nextInt();
    }

    public long nextLong() {
        return random.nextLong();
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public float nextFloat() {
        return random.nextFloat();
    }

    // TODO: remove on next major version bump (this class is generally used via lombok @Delegate which doesn't delegate
    //  to deprecated methods by default, so can't mark @Deprecated)
    // @Deprecated there is no way to return a deterministic value, use localDateInPast()
    public LocalDate dateInPastDays(int numOfDays) {
        return LocalDate.now().minusDays(intBetween(1, numOfDays));
    }

    // TODO: remove on next major version bump (this class is generally used via lombok @Delegate which doesn't delegate
    //  to deprecated methods by default, so can't mark @Deprecated)
    // @Deprecated there is no way to return a deterministic value, use optionalLocalDateInPast
    public LocalDate optionalDateInPastDays(int numOfDays) {
        return shouldBeNull() ? null : dateInPastDays(numOfDays);
    }

    public LocalDate localDateInPast() {
        return zonedDateTimeInPastUTC().toLocalDate();
    }

    public LocalDate optionalLocalDateInPast() {
        return shouldBeNull() ? null : localDateInPast();
    }

    public LocalDate localDateInFuture() {
        return zonedDateTimeInFutureUTC().toLocalDate();
    }

    public LocalDate optionalLocalDateInFuture() {
        return shouldBeNull() ? null : localDateInFuture();
    }

    public LocalDate localDate() {
        return coinFlip() ? localDateInPast() : localDateInFuture();
    }

    public LocalDate optionalLocalDate() {
        return shouldBeNull() ? null : localDate();
    }

    public Date sqlDateInFuture() {
        return Date.valueOf(localDateInFuture());
    }

    public Date optionalSqlDateInFuture() {
        return shouldBeNull() ? null : sqlDateInFuture();
    }

    public Date sqlDate() {
        return Date.valueOf(localDate());
    }

    public Date optionalSqlDate() {
        return shouldBeNull() ? null : sqlDate();
    }

    public LocalDateTime localDateTimeInPast() {
        return zonedDateTimeInPastUTC().toLocalDateTime();
    }

    public LocalDateTime optionalLocalDateTimeInPast() {
        return shouldBeNull() ? null : localDateTimeInPast();
    }

    public LocalDateTime localDateTimeInFuture() {
        return zonedDateTimeInFutureUTC().toLocalDateTime();
    }

    public LocalDateTime optionalLocalDateTimeInFuture() {
        return shouldBeNull() ? null : localDateTimeInFuture();
    }

    public LocalDateTime localDateTime() {
        return coinFlip() ? localDateTimeInPast() : localDateTimeInFuture();
    }

    public LocalDateTime optionalLocalDateTime() {
        return shouldBeNull() ? null : localDateTime();
    }

    public Timestamp timeStampInFuture() {
        return Timestamp.valueOf(localDateTimeInFuture());
    }

    public Timestamp optionalTimeStampInFuture() {
        return shouldBeNull() ? null : timeStampInFuture();
    }

    public Timestamp timeStamp() {
        return Timestamp.valueOf(localDateTime());
    }

    public Timestamp optionalTimeStamp() {
        return shouldBeNull() ? null : timeStamp();
    }

    public ZonedDateTime zonedDateTimeInFuture() {
        return zonedDateTimeInFuture(ZoneId.systemDefault());
    }

    public ZonedDateTime zonedDateTimeInFutureUTC() {
        return zonedDateTimeInFuture(UTC);
    }

    public ZonedDateTime zonedDateTimeInFuture(ZoneId zoneId) {
        return FUTURE_ZONED_DATE_TIME.withZoneSameInstant(zoneId)
                .plusDays(intBetween(1, 365))
                .plusHours(intBetween(0, 23))
                .plusMinutes(intBetween(0, 59))
                .plusSeconds(intBetween(0, 59))
                .plus(intBetween(0, 999), ChronoUnit.MILLIS);
    }

    public ZonedDateTime zonedDateTimeInPast() {
        return zonedDateTimeInPast(ZoneId.systemDefault());
    }

    public ZonedDateTime zonedDateTimeInPastUTC() {
        return zonedDateTimeInPast(UTC);
    }

    public ZonedDateTime zonedDateTimeInPast(ZoneId zoneId) {
        return PAST_ZONED_DATE_TIME.withZoneSameInstant(zoneId)
                .minusDays(intBetween(1, 365))
                .minusDays(intBetween(1, 365))
                .minusHours(intBetween(0, 23))
                .minusMinutes(intBetween(0, 59))
                .minusSeconds(intBetween(0, 59))
                .minus(intBetween(0, 999), ChronoUnit.MILLIS);
    }

    public ZonedDateTime optionalZonedDateTimeInFuture() {
        return shouldBeNull() ? null : zonedDateTimeInFuture();
    }

    public ZonedDateTime zonedDateTime() {
        return coinFlip() ? zonedDateTimeInPast() : zonedDateTimeInFuture();
    }

    public ZonedDateTime zonedDateTimeUTC() {
        return coinFlip() ? zonedDateTimeInPastUTC() : zonedDateTimeInFutureUTC();
    }

    public ZonedDateTime optionalZonedDateTime() {
        return shouldBeNull() ? null : zonedDateTime();
    }

    public OffsetDateTime offsetDateTimeInPast() {
        return zonedDateTimeInPastUTC().toOffsetDateTime();
    }

    public OffsetDateTime optionalOffsetDateTimeInPast() {
        return shouldBeNull() ? null : offsetDateTimeInPast();
    }

    public OffsetDateTime offsetDateTimeInFuture() {
        return zonedDateTimeInFutureUTC().toOffsetDateTime();
    }

    public OffsetDateTime optionalOffsetDateTimeInFuture() {
        return shouldBeNull() ? null : offsetDateTimeInFuture();
    }

    public OffsetDateTime offsetDateTime() {
        return coinFlip() ? offsetDateTimeInPast() : offsetDateTimeInFuture();
    }

    public OffsetDateTime optionalOffsetDateTime() {
        return shouldBeNull() ? null : offsetDateTime();
    }

    /**
     * Returns a string of random characters.
     */
    public String text(int length) {
        // NOTE: we're using getRandomChars instead of getRandomText b/c the random text isn't so random.
        // they basically use a dictionary of words of specific lengths and the number of choices can be
        // very small (e.g. size 10 equates to 2 distinct words)
        return df.getRandomChars(length);
    }

    public String text() {
        return text(intBetween(1, 25));
    }

    public String optionalText(int length) {
        return shouldBeNull() ? null : text(length);
    }

    public String words(int length) {
        return lorem.getWords(length).substring(0, length).trim();
    }

    public String optionalWords(int length) {
        return shouldBeNull() ? null : words(length);
    }

    public String businessName() {
        return df.getBusinessName();
    }

    public String optionalBusinessName() {
        return shouldBeNull() ? null : businessName();
    }

    private boolean shouldBeNull() {
        return df.chance(5);
    }

    public String phoneNumber() {
        return df.getNumberText(3) + "-" + df.getNumberText(3) + "-" + df.getNumberText(4);
    }

    public String optionalPhoneNumber() {
        return shouldBeNull() ? null : phoneNumber();
    }

    public String numberText(int length) {
        return df.getNumberText(length);
    }

    public String optionalNumberText(int length) {
        return shouldBeNull() ? null : numberText(length);
    }

    public String state() {
        return item(STATES);
    }

    public String optionalState() {
        return shouldBeNull() ? null : state();
    }

    public String address() {
        return df.getAddress();
    }

    public String optionalAddress() {
        return shouldBeNull() ? null : address();
    }

    public String address(int maxLength) {
        String address = address();
        return (address.length() > maxLength) ? address.substring(0, maxLength) : address;
    }

    public String addressLine2() {
        return df.getAddressLine2();
    }

    public String optionalAddressLine2() {
        return shouldBeNull() ? null : addressLine2();
    }

    public String addressLine2(int probability) {
        return df.getAddressLine2(probability);
    }

    public String optionalAddressLine2(int probability) {
        return shouldBeNull() ? null : addressLine2();
    }

    public String city() {
        return df.getCity();
    }

    public String optionalCity() {
        return shouldBeNull() ? null : city();
    }

    public String city(int maxLength) {
        String city = city();
        return (city.length() > maxLength) ? city.substring(0, maxLength) : city;
    }

    public String email() {
        return df.getEmailAddress().replaceAll("\\s+", "_");
    }

    public String optionalEmail() {
        return shouldBeNull() ? null : email();
    }

    public String email(int maxLength) {
        String email = email();
        return (email.length() > maxLength) ? email.substring(0, maxLength) : email;
    }

    public <T> T item(List<T> items) {
        return df.getItem(items);
    }

    public <T> List<T> list(Supplier<T> supplier) {
        return list(0, 10, supplier);
    }

    public <T> List<T> nonEmptyList(Supplier<T> supplier) {
        return list(1, 10, supplier);
    }

    public <T> List<T> list(int minItems, int maxItems, Supplier<T> supplier) {
        int numItems = intBetween(minItems, maxItems);
        return list(numItems, supplier);
    }

    public <T> List<T> list(int numItems, Supplier<T> supplier) {
        List<T> items = new ArrayList<>();
        for (int x = 0; x < numItems; x++) {
            items.add(supplier.get());
        }
        return items;
    }

    public <T> Set<T> set(Supplier<T> supplier) {
        return set(0, 10, supplier);
    }

    public <T> Set<T> nonEmptySet(Supplier<T> supplier) {
        return set(1, 10, supplier);
    }

    public <T> Set<T> set(int minItems, int maxItems, Supplier<T> supplier) {
        int numItems = intBetween(minItems, maxItems);
        return set(numItems, supplier);
    }

    public <T> Set<T> set(int numItems, Supplier<T> supplier) {
        Set<T> items = new HashSet<>();
        for (int x = 0; x < numItems; x++) {
            items.add(supplier.get());
        }
        return items;
    }

    public <T, V> Map<T, V> map(Supplier<T> keySupplier, Supplier<V> valueSupplier) {
        return map(0, 10, keySupplier, valueSupplier);
    }

    public <T, V> Map<T, V> nonEmptyMap(Supplier<T> keySupplier, Supplier<V> valueSupplier) {
        return map(1, 10, keySupplier, valueSupplier);
    }

    public Map<String, String> nonEmptyStringMap() {
        return map(1, 10, this::text, this::text);
    }

    public <T, V> Map<T, V> map(int minItems, int maxItems, Supplier<T> keySupplier, Supplier<V> valueSupplier) {
        Map<T, V> items = new HashMap<>();
        int numItems = intBetween(minItems, maxItems);
        for (int x = 0; x < numItems; x++) {
            items.put(keySupplier.get(), valueSupplier.get());
        }
        return items;
    }

    public <T> T optionalItem(List<T> items) {
        return shouldBeNull() ? null : item(items);
    }

    public <T> T item(T... items) {
        return df.getItem(Arrays.asList(items));
    }

    public <T> T optionalItem(T... items) {
        return shouldBeNull() ? null : item(items);
    }

    public String name() {
        return df.getName();
    }

    public String optionalName() {
        return shouldBeNull() ? null : name();
    }

    public String firstName() {
        return df.getFirstName();
    }

    public String optionalFirstName() {
        return shouldBeNull() ? null : firstName();
    }

    public String firstName(int maxLength) {
        String firstName = firstName();
        return (firstName.length() > maxLength) ? firstName.substring(0, maxLength) : firstName;
    }

    public String lastName() {
        return df.getLastName();
    }

    public String optionalLastName() {
        return shouldBeNull() ? null : lastName();
    }

    public String lastName(int maxLength) {
        String lastName = lastName();
        return (lastName.length() > maxLength) ? lastName.substring(0, maxLength) : lastName;
    }

    public boolean coinFlip() {
        return df.chance(50);
    }

    public boolean weightedCoinFlip(int probability) {
        return df.chance(probability);
    }

    public int siteId() {
        return intBetween(1000, 10000);
    }

    public float floatBetween(float min, float max) {
        return nextFloat() * (max - min) + min;
    }

    public float latitude() {
        return floatBetween(-90.0f, 90.0f);
    }

    public float longitude() {
        return floatBetween(-180.0f, 180.0f);
    }

    public String scsCode() {
        return text(3);
    }

    public String serviceCode() {
        return text(5);
    }

    public String environmentId() {
        return generateForRandomRealm(this::environmentId);
    }

    public String environmentId(String realm) {
        return generateRandomEntityForRealm((r) -> r + "-" + text(22),
                this::environmentId,
                realm);
    }

    public String testEnvironmentId() {
        return generateForTest(this::environmentId);
    }

    public String prodEnvironmentId() {
        return generateForProd(this::environmentId);
    }

    public String legalEntityId() {
        return generateForRandomRealm(this::legalEntityId);
    }

    public String legalEntityId(String realm) {
        return generateRandomEntityForRealm((r) -> r + "-" + text(10) + "-" + text(11),
                this::legalEntityId,
                realm);
    }

    public String testLegalEntityId() {
        return generateForTest(this::legalEntityId);
    }

    public String prodLegalEntityId() {
        return generateForProd(this::legalEntityId);
    }

    private String generateForTest(Function<String, String> function) {
        return function.apply(TEST_REALM);
    }

    private String generateForProd(Function<String, String> function) {
        return function.apply(PROD_REALM);
    }

    private String generateForRandomRealm(Function<String, String> function) {
        return function.apply(environmentType());
    }

    private String generateRandomEntityForRealm(Function<String, String> idGenerationFunction,
                                                Supplier<String> idGenerationSupplier,
                                                String realm) {
        if (isNotBlank(realm)) {
            return idGenerationFunction.apply(realm);
        } else {
            return idGenerationSupplier.get();
        }
    }

    private boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    private boolean isNotBlank(String string) {
        return isBlank(string) == false;
    }

    public String environmentType() {
        return coinFlip() ? TEST_REALM : PROD_REALM;
    }

    public String luminateTestEnvironment() {
        return text(10);
    }

    public String entitlementId() {
        String prefix = coinFlip() ? "" : luminateTestEnvironment() + "-";
        return prefix + siteId();
    }

    public File file(int contentLength) throws IOException {
        return fileWithContent(text(contentLength));
    }

    public File fileWithContent(String content) throws IOException {
        File file = File.createTempFile(text(8), text(3));
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
        return file;
    }

    public URL url() throws MalformedURLException {
        String protocol = item(PROTOCOLS);
        String tld = item(TOP_LEVEL_DOMAINS);
        String hostname = text(intBetween(3, 10)) + "." + tld;
        int port = intBetween(80, 9000);
        String path = text(intBetween(3, 10));
        String queryParam = text(intBetween(3, 10)) + "=" + text(intBetween(3, 10));
        return new URL(protocol, hostname, port, "/" + path + "?" + queryParam);
    }

    public long amountOfPennies() {
        return amountOfPennies(1);
    }

    public long amountOfPennies(int minimum) {
        return amountOfPennies(minimum, 1000000);
    }

    public long amountOfPennies(int minimum, int maximum) {
        return intBetween(minimum, maximum);
    }

    public BigDecimal dollarAmount() {
        return new BigDecimal(amountOfPennies()).movePointLeft(2);
    }

    public BigDecimal dollarAmountLessThan(BigDecimal lessThan) {
        return new BigDecimal(amountOfPennies(1, lessThan.movePointRight(2).intValue())).movePointLeft(2);
    }

    public String country() {
        return item(Locale.getISOCountries());
    }

    public String zipCode() {
        return numberText(5);
    }

    public String ipAddress() {
        return String.join(".",
                Integer.toString(intBetween(1, 254)),
                Integer.toString(intBetween(1, 254)),
                Integer.toString(intBetween(1, 254)),
                Integer.toString(intBetween(1, 254)));
    }

    public String routingNumber() {
        return numberText(9);
    }

    public String bankAccountNumber() {
        return numberText(10);
    }

    public int httpStatusCode() {
        return intBetween(100, 599);
    }

    public Duration duration() {
        return Duration.ofSeconds(nextInt());
    }

    public Duration positiveDuration() {
        return Duration.ofSeconds(positiveInt());
    }

    public Duration negativeDuration() {
        return Duration.ofSeconds(negativeInt());
    }

    public String hexColor() {
        return String.format("#%06x", intBetween(0, 0xffffff));
    }

    public String engSysZone() {
        return engSysZone(item("p", "t"));
    }

    public String engSysZone(String realm) {
        return realm + "-" + item("usa", "eur", "aus", "can") + String.format("%02d", intBetween(1, 99));
    }
}

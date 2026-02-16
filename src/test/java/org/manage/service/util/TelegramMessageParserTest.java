package org.manage.service.util;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelegramMessageParserTest {

    @Test
    void parseDuration_HHMM() {
        assertThat(TelegramMessageParser.parseDuration("2:30")).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        assertThat(TelegramMessageParser.parseDuration("0:45")).isEqualTo(Duration.ofMinutes(45));
    }

    @Test
    void parseDuration_NaturalLanguage() {
        assertThat(TelegramMessageParser.parseDuration("2h 30m")).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        assertThat(TelegramMessageParser.parseDuration("2ч 30м")).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        assertThat(TelegramMessageParser.parseDuration("1.5h")).isEqualTo(Duration.ofHours(1).plusMinutes(30));
        assertThat(TelegramMessageParser.parseDuration("90m")).isEqualTo(Duration.ofMinutes(90));
        assertThat(TelegramMessageParser.parseDuration("1.5ч")).isEqualTo(Duration.ofHours(1).plusMinutes(30));
        assertThat(TelegramMessageParser.parseDuration("20м")).isEqualTo(Duration.ofMinutes(20));
    }

    @Test
    void parseDuration_NumericFallback() {
        assertThat(TelegramMessageParser.parseDuration("60")).isEqualTo(Duration.ofMinutes(60));
    }

    @Test
    void parseDuration_Invalid() {
        assertThatThrownBy(() -> TelegramMessageParser.parseDuration("abc"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TelegramMessageParser.parseDuration("2:60"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TelegramMessageParser.parseDuration("2h 30")) // Invalid because space + no unit in the middle
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parseMessage_Valid() {
        var result = TelegramMessageParser.parseMessage("2:30 coding");
        assertThat(result).isNotNull();
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        assertThat(result.getDescription()).isEqualTo("coding");

        result = TelegramMessageParser.parseMessage("2ч 30м разработка API");
        assertThat(result).isNotNull();
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        assertThat(result.getDescription()).isEqualTo("разработка API");

        result = TelegramMessageParser.parseMessage("1.5h meeting with team");
        assertThat(result).isNotNull();
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(1).plusMinutes(30));
        assertThat(result.getDescription()).isEqualTo("meeting with team");
        
        result = TelegramMessageParser.parseMessage("90м some task");
        assertThat(result).isNotNull();
        assertThat(result.getDuration()).isEqualTo(Duration.ofMinutes(90));
        assertThat(result.getDescription()).isEqualTo("some task");
    }

    @Test
    void parseMessage_Invalid() {
        assertThat(TelegramMessageParser.parseMessage("coding 2:30")).isNull();
        assertThat(TelegramMessageParser.parseMessage("2:30")).isNull(); // No description
        assertThat(TelegramMessageParser.parseMessage("just some text")).isNull();
    }
}

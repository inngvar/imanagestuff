package org.manage.config;

import org.manage.service.telegram.command.TelegramCommand;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class TelegramCommandMapProducer {

    @Inject
    Instance<TelegramCommand> commands;

    @Produces
    @Singleton
    public Map<String, TelegramCommand> produceMap() {
        return commands.stream()
            .collect(Collectors.toMap(
                TelegramCommand::commandName,
                i -> i
            ));
    }
}

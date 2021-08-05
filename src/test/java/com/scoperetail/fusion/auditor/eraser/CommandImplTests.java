package com.scoperetail.fusion.auditor.eraser;

import com.scoperetail.eraser.dto.Result;
import com.scoperetail.eraser.processor.CommandResult;
import com.scoperetail.eraser.properties.ConfigProperties;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogKeyRepository;
import com.scoperetail.fusion.audit.persistence.repository.MessageLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class CommandImplTests {
  @InjectMocks
  private CommandImpl command;

  @Mock
  private MessageLogRepository messageLogRepository;

  @Mock
  private MessageLogKeyRepository messageLogKeyRepository;

  @BeforeEach
  public void setUp() {
    final Pageable pageable = PageRequest.of(0, 15);
    final LocalDateTime pivoteDate = LocalDate.now().minusDays(15).atStartOfDay();
    List<String> logKeysToErase = new ArrayList<>();
    logKeysToErase.add("logKeyToErase");

    when(messageLogRepository.findLogKeysToErase(pivoteDate, pageable)).thenReturn(logKeysToErase);
    command = new CommandImpl(messageLogRepository, messageLogKeyRepository);
  }

  @Test
  public void execute_success_no_data() {
    ConfigProperties configProperties = new ConfigProperties();
    List<Result> resultList = new ArrayList<>();

    configProperties.setBatchSize(15);

    command.setRetentionDuration(20);
    assertEquals(CommandResult.STOP, command.execute(configProperties, resultList));
  }

  @Test
  public void execute_success_with_data() throws Exception {
    ConfigProperties configProperties = new ConfigProperties();
    List<Result> resultList = new ArrayList<>();
    configProperties.setBatchSize(15);

    command.setRetentionDuration(15);
    assertEquals(CommandResult.CONTINUE, command.execute(configProperties, resultList));
  }

  @Test
  public void execute_exception() throws Exception {
    ConfigProperties configProperties = new ConfigProperties();
    List<Result> resultList = new ArrayList<>();

    command = new CommandImpl(messageLogRepository, messageLogKeyRepository);
    configProperties.setBatchSize(15);

    assertEquals(CommandResult.DELAY, command.execute(configProperties, resultList));
  }
}

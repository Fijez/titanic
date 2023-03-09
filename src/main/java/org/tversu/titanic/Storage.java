package org.tversu.titanic;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Getter(onMethod_ = {@ShellMethod})
@NoArgsConstructor
@ShellComponent
@ShellCommandGroup(value = "storage")
@Component
public class Storage
{

  private List<Passenger> forTest;
  private List<Passenger> forTeach;
  private final String path = "src/main/resources/titanic.data.gz.txt";
  @Value("${border}")
  private Integer border;
  @Value("${shuffle}")
  private Boolean shuffle;

  private Passenger stringToPassenger(String o, Long id)
  {
    o = o.replace(" ", "");
    return new Passenger(id, Integer.valueOf(Character.toString(o.charAt(0))), Character.getNumericValue(o.charAt(1)), Character.getNumericValue(o.charAt(2)), Character.getNumericValue(o.charAt(3)));
  }

  @PostConstruct
  @ShellMethod(key = "load_data")
  public void loadDataFromFile() throws IOException
  {
    List<String> strings = FileUtils.readLines(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
    if(shuffle){
      System.out.println("Входные данные перемешаны перед формированием массивов для теста и для обучения");
      Collections.shuffle(strings);
    }
    List<Passenger> passengers = new ArrayList<>();
    for (int i = 0; i < strings.size(); i++) {
      passengers.add(stringToPassenger(strings.get(i), (long)i));
    }

    forTeach = passengers.stream().limit(border).collect(Collectors.toList());
    forTest = passengers.stream().skip(border).collect(Collectors.toList());

  }

  @ShellMethod(key = "r")
  public void redistributeData() throws IOException
  {
    List<String> strings = FileUtils.readLines(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
    Collections.shuffle(strings);
    List<Passenger> passengers = new ArrayList<>();
    for (int i = 0; i < strings.size(); i++) {
      passengers.add(stringToPassenger(strings.get(i), (long)i));
    }

    forTeach = passengers.stream().limit(border).collect(Collectors.toList());
    forTest = passengers.stream().skip(border).collect(Collectors.toList());

  }

  @ShellMethod(key = "test_data")
  public List<Passenger> getForTest(@ShellOption(defaultValue = "false", value = "random") String isRandOrder)
  {
    if (isRandOrder.equals("true")) {
      Collections.shuffle(forTest);
      System.out.println("shuffle array");
    }
    return forTest;
  }

  @ShellMethod(key = "teach_data")
  public List<Passenger> getForTeach(@ShellOption(defaultValue = "false", value = "random") String isRandOrder)
  {
    if (isRandOrder.equals("true")) {
      Collections.shuffle(forTeach);
      System.out.println("shuffle array");
    }
    return forTeach;
  }

}

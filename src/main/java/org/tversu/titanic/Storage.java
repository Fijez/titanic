package org.tversu.titanic;

import ch.qos.logback.core.util.FileUtil;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Description;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.awt.geom.IllegalPathStateException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Getter(onMethod_ = {@ShellMethod})
@AllArgsConstructor
@Builder
@ShellComponent
@ShellCommandGroup(value = "storage")
@Component
public class Storage
{

  private List<Passenger> forTest;
  private List<Passenger> forTeach;
  private final String path = "src/main/resources/titanic.data.gz.txt";
  private final int border = 1500;

  private Passenger stringToPassenger(String o, Long id)
  {
    o = o.replace(" ", "");
    return new Passenger(id, Integer.valueOf(Character.toString(o.charAt(0))), Character.getNumericValue(o.charAt(1)), Character.getNumericValue(o.charAt(2)), Character.getNumericValue(o.charAt(3)));
  }

  @PostConstruct
  public void loadDataFromFile() throws IOException
  {
    if (forTeach != null  && forTeach.size() > 0) {
      System.out.println("Data already converted.");
      return;
    }
    ClassLoader classLoader = Storage.class.getClassLoader();

    List<String> strings = FileUtils.readLines(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
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

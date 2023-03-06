package org.tversu.titanic;

import ch.qos.logback.core.util.FileUtil;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Description;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Getter(onMethod_ = {@ShellMethod})
@AllArgsConstructor
@Builder
@ShellComponent
@ShellCommandGroup(value = "storage")
public class Storage
{

  private List<Passenger> forTest;
  private List<Passenger> forTeach;
  private final String path = "titanic.data.gz.txt";

  private Passenger stringToPassenger(String o)
  {
    o = o.replace(" ", "");
    return new Passenger(Integer.valueOf(Character.toString(o.charAt(0))), Character.getNumericValue(o.charAt(1)), Character.getNumericValue(o.charAt(2)), Character.getNumericValue(o.charAt(3)));
  }

  @PostConstruct
  public void loadDataFromFile() throws IOException
  {
    if (forTeach != null) {
      System.out.println("Data already converted.");
      return;
    }
    ClassLoader classLoader = Storage.class.getClassLoader();

    List<Passenger> passengers = FileUtils.readLines(ResourceUtils.getFile(path), StandardCharsets.UTF_8)
        .stream()
        .map(this::stringToPassenger)
        .toList();

    forTeach = passengers.subList(0, 2000);
    forTest = passengers.subList(2000, passengers.size());

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

package org.tversu.titanic;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.awt.geom.IllegalPathStateException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

  private static List<Passenger> forTest;
  private static List<Passenger> forTeach;
  private final String path = "titanic.data.gz.txt";

  private static Passenger stringToPassenger(String o)
  {
    o = o.replace(" ", "");
    return new Passenger(Integer.valueOf(Character.toString(o.charAt(0))), o.charAt(1) == '1', o.charAt(2) == '1', o.charAt(3) == '1');
  }

  @PostConstruct
  public void loadDataFromFile()
  {
    if (forTeach != null) {
      System.out.println("Data already converted.");
      return;
    }
    ClassLoader classLoader = Storage.class.getClassLoader();

    try (InputStream resourceAsStream = classLoader.getResourceAsStream(path)) {
      assert resourceAsStream != null;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {

        forTeach = reader.lines()
            .limit(1700)
            .map(Storage::stringToPassenger)
            .collect(Collectors.toList());

        forTest = reader.lines()
            .skip(1700)
            .map(Storage::stringToPassenger)
            .collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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

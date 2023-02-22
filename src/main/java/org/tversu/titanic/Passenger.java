package org.tversu.titanic;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class Passenger
{
  private Integer cabinClass;
  private Boolean isAdult;
  private Boolean isMale;
  private Boolean isSurvived;
}

package org.tversu.titanic;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class Passenger
{
  private Integer cabinClass;
  private Integer isAdult;
  private Integer isMale;
  private Integer isSurvived;
}

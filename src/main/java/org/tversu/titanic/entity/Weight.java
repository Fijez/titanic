package org.tversu.titanic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class Weight
{
  private Double value;
  private Neuron inputNeuron;
  private Neuron outputNeuron;

}

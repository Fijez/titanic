package org.tversu.titanic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Weight
{
  private Double value;
  private Neuron inputNeuron;
  private Neuron outputNeuron;

}

package org.tversu.titanic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Neuron
{
  private List<Weight> inputNeurons = new ArrayList<>();
  private List<Weight> outputNeurons = new ArrayList<>();
  private Double value = 0D;
  private Double error = 0D;
}
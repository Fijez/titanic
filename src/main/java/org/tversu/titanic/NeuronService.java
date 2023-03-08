package org.tversu.titanic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tversu.titanic.entity.Neuron;
import org.tversu.titanic.entity.Weight;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NeuronService
{

  private final Storage storage;
  private List<List<Neuron>> neuronsNetwork;


  public void teach()
  {

    initialisationNetwork();

    List<Passenger> forTeach = storage.getForTeach();

    while (!endAlg()) {
      forTeach
          .forEach(inputData -> {
            neuronsNetwork.get(0).get(0).setValue(inputData.getIsMale().doubleValue());
            neuronsNetwork.get(0).get(1).setValue(inputData.getIsAdult().doubleValue());
            neuronsNetwork.get(0).get(2).setValue(inputData.getCabinClass().doubleValue());
          });
    }

  }

  private Double activationHide(Double val)
  {
    return val;
  }

  private Boolean endAlg()
  {
    return false;
  }

  private Double activationOut(Double val)
  {
    return val;
  }

  private void compare()
  {

  }

  private void initialisationNetwork()
  {
    int countHideNeurons = 3;
    neuronsNetwork = new ArrayList<>();
    neuronsNetwork.add(new ArrayList<>(3));
    neuronsNetwork.add(new ArrayList<>(countHideNeurons));
    neuronsNetwork.add(new ArrayList<>(1));

    for (int i = 0; i < 3; i++) {
      neuronsNetwork.get(0).add(Neuron.builder()
          .inputNeurons(null)
          .outputNeurons(new ArrayList<>(countHideNeurons))
          .build());
    }

    for (int i = 0; i < countHideNeurons; i++) {
      neuronsNetwork.get(1).add(Neuron.builder()
          .inputNeurons(new ArrayList<>(3))
          .outputNeurons(new ArrayList<>(1))
          .build());
    }

    neuronsNetwork.get(2).add(Neuron.builder()
        .inputNeurons(new ArrayList<>(countHideNeurons))
        .outputNeurons(null)
        .build());

    for (int i = 0; i < neuronsNetwork.size() - 1; i++) {
      List<Neuron> neurons = neuronsNetwork.get(i);
      for (Neuron neuron : neurons) {
        List<Weight> outputNeurons = neuron.getOutputNeurons();
        for (int k = 0; k < outputNeurons.size(); k++) {
          outputNeurons.add(Weight.builder()
              .inputNeuron(neuron)
              .outputNeuron(neuronsNetwork.get(i + 1).get(k))
              .value((long)(Math.random() - 0.5))
              .build());
        }
      }
    }
  }

  private void clearNeuronValues()
  {
    neuronsNetwork = neuronsNetwork.stream().map(s ->
        s.stream().peek(c -> c.setValue(0D)).toList()
    ).collect(Collectors.toList());
  }


}

package org.tversu.titanic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tversu.titanic.entity.Neuron;
import org.tversu.titanic.entity.Weight;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class NeuronService
{

  private final Storage storage;
  private final Integer neuronCount = 3;
  private List<List<Double>> hideWeights =
      List.of(List.of(0.5, 0.5, 0.5), List.of(0.5, 0.5, 0.5), List.of(0.5, 0.5, 0.5));
  private List<Double> outWeights = List.of(0.5, 0.5, 0.5);
  private List<List<Neuron>> neuronsNetwork;

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

  public void teach()
  {

    initialisationNetwork();

    List<Passenger> forTeach = storage.getForTeach();



    while (!endAlg()) {
      forTeach
          .forEach(o -> {
            Integer isSurvived = o.getIsSurvived();

            Integer cabinClass = o.getCabinClass();
            Integer isAdult = o.getIsAdult();
            Integer isMale = o.getIsMale();

            Integer[] input = {cabinClass, isAdult, isMale};
            List<Double> hideResult = new ArrayList<>(3);
            for (int i = 0; i < neuronCount; i++) {

              AtomicReference<Double> sumInputSignals = new AtomicReference<>(0D);

              int finalI = i;
              hideWeights.forEach(inNeuron -> {
                Double weight = inNeuron.get(finalI);
                sumInputSignals.updateAndGet(v ->
                    v + (weight == null ? 0 : weight * input[finalI]));
              });

              Double activateHideResult = activationHide(sumInputSignals.get());
              hideResult.set(i, activateHideResult);
            }

            List<Double> outResults = new ArrayList<>(3);
            for (int i = 0; i < neuronCount; i++) {

              AtomicReference<Double> sumInputSignals = new AtomicReference<>(0D);

              int finalI = i;
              for (int j = 0; j < outWeights.size(); j++) {
                Double weight = outWeights.get(j);
                sumInputSignals.updateAndGet(v ->
                    v + (weight == null ? 0 : weight * hideResult.get(finalI)));
              }

              Double activateHideResult = activationOut(sumInputSignals.get());
              outResults.set(i, activateHideResult);
            }
            Double outRes = outResults.stream().reduce(0D, Double::sum);
            Double errorOut = (isSurvived - outRes);

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


}

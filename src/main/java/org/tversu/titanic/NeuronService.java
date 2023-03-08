package org.tversu.titanic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tversu.titanic.entity.Neuron;
import org.tversu.titanic.entity.Weight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NeuronService
{

  private final Storage storage;
  private List<List<Neuron>> neuronsNetwork;
  private Double teachSpeed = 0.5;
  private final int limit = 100;


  public void teach()
  {

    initialisationNetwork();

    List<Passenger> forTeach = storage.getForTeach();

    int count = 0;
    while (!endAlg(count)) {
      count++;
      Collections.shuffle(forTeach);
      forTeach
          .forEach(inputData -> {
            //передача входынх параметров
            neuronsNetwork.get(0).get(0).setValue(inputData.getIsMale().doubleValue());
            neuronsNetwork.get(0).get(1).setValue(inputData.getIsAdult().doubleValue());
            neuronsNetwork.get(0).get(2).setValue(inputData.getCabinClass().doubleValue());

            //вычисление результата
            for (int i = 1; i < neuronsNetwork.size(); i++) {
              List<Neuron> neurons = neuronsNetwork.get(i);
              for (Neuron n : neurons) {
                n.setValue(activationSigmoida(sumWeightedIncomingSignals(n)));
              }
            }

            //вычисление ошибок
            Neuron outputNeuron = neuronsNetwork.get(2).get(0);
            outputNeuron.setError((inputData.getIsSurvived() - outputNeuron.getValue()) * activationSigmoidaDerivative(sumWeightedIncomingSignals(outputNeuron)));

            for (int i = neuronsNetwork.size() - 2; i > 0; i--) {
              List<Neuron> neurons = neuronsNetwork.get(i);
              for (Neuron n : neurons) {
                n.setError(n.getOutputWeights()
                    .stream()
                    .map(o -> o.getValue() * o.getOutputNeuron().getError())
                    .reduce(0D, Double::sum) * activationSigmoidaDerivative(sumWeightedIncomingSignals(n)));
              }
            }

            //Вычислене новых весов
            for (int i = 1; i < neuronsNetwork.size(); i++) {
              List<Neuron> neurons = neuronsNetwork.get(i);
              for (Neuron n : neurons) {
                for(Weight w: n.getInputWeights()){
                  w.setValue(w.getValue()+linkWeightChange(w));
                }
              }
            }

            //очистка всех данных кроме весов
            clearNeuronValues();
          });
    }
  }

  private Double sumWeightedIncomingSignals(Neuron n)
  {
    return n.getInputWeights()
        .stream()
        .map(w -> w.getValue() * w.getInputNeuron().getValue())
        .reduce(0D, Double::sum);
  }

  private Double activationSigmoida(Double val)
  {
    return 1 / (1 + Math.exp(-val));
  }

  private Double activationSigmoidaDerivative(Double val)
  {
    return activationSigmoida(val) * (1 - activationSigmoida(val));
  }

  private Double linkWeightChange(Weight w)
  {
    return teachSpeed*w.getOutputNeuron().getError()*w.getInputNeuron().getValue();
  }

  private Boolean endAlg(int count)
  {
    return count < limit;
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
          .inputWeights(null)
          .outputWeights(new ArrayList<>(countHideNeurons))
          .build());
    }

    for (int i = 0; i < countHideNeurons; i++) {
      neuronsNetwork.get(1).add(Neuron.builder()
          .inputWeights(new ArrayList<>(3))
          .outputWeights(new ArrayList<>(1))
          .build());
    }

    neuronsNetwork.get(2).add(Neuron.builder()
        .inputWeights(new ArrayList<>(countHideNeurons))
        .outputWeights(null)
        .build());

    for (int i = 0; i < neuronsNetwork.size() - 1; i++) {
      List<Neuron> neurons = neuronsNetwork.get(i);
      for (Neuron neuron : neurons) {
        List<Weight> outputNeurons = neuron.getOutputWeights();
        for (int k = 0; k < outputNeurons.size(); k++) {
          outputNeurons.add(Weight.builder()
              .inputNeuron(neuron)
              .outputNeuron(neuronsNetwork.get(i + 1).get(k))
              .value(Math.random() - 0.5)
              .build());
        }
      }
    }
  }

  private void clearNeuronValues()
  {
    neuronsNetwork = neuronsNetwork.stream().map(s ->
        s.stream().peek(c -> {
          c.setValue(0D);
          c.setError(0D);
        }).toList()
    ).collect(Collectors.toList());
  }


}

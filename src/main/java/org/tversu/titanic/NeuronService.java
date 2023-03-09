package org.tversu.titanic;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.tversu.titanic.entity.Neuron;
import org.tversu.titanic.entity.Weight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class NeuronService
{

  private final Storage storage;
  private List<List<Neuron>> neuronsNetwork;
  private Double teachSpeed = 0.5;
  private int limit = 1000;
  private final double precision = 0.7;

  @ShellMethod(key = "t")
  public void teach()
  {

    initialisationNetwork();

    List<Passenger> forTeach = storage.getForTeach();

    int count = 0;
    while (!isEndAlg(count)) {
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
            outputNeuron.setError((inputData.getIsSurvived() - outputNeuron.getValue())
                * (1 - outputNeuron.getValue()) * outputNeuron.getValue());
            //* activationSigmoidaDerivative(sumWeightedIncomingSignals(outputNeuron)));

            for (int i = neuronsNetwork.size() - 2; i > 0; i--) {
              List<Neuron> neurons = neuronsNetwork.get(i);
              for (Neuron n : neurons) {
                n.setError(n.getOutputWeights()
                    .stream()
                    .map(o -> o.getValue() * o.getOutputNeuron().getError())
                    .reduce(0D, Double::sum) * (1 - n.getValue()) * n.getValue());
//                    * activationSigmoidaDerivative(sumWeightedIncomingSignals(n)));
              }
            }

            //Вычислене новых весов
            for (int i = 1; i < neuronsNetwork.size(); i++) {
              List<Neuron> neurons = neuronsNetwork.get(i);
              for (Neuron n : neurons) {
                for (Weight w : n.getInputWeights()) {
                  w.setValue(w.getValue() + linkWeightChange(w));
                }
              }
            }

//            Double globalError = 0.5 * Math.pow(inputData.getIsSurvived() - neuronsNetwork.get(2).get(0).getValue(), 2);
//            System.out.println("Global error = " + globalError);
            //очистка всех данных кроме весов
            clearNeuronValues();
          });
    }
  }

  @ShellMethod(key = "test")
  private void testNetwork(){
    List<Passenger> forTest = storage.getForTest();
    Collections.shuffle(forTest);
    AtomicInteger countWrong0 = new AtomicInteger();
    AtomicInteger countWrong1 = new AtomicInteger();
    forTest.forEach(inputData -> {
      neuronsNetwork.get(0).get(0).setValue(inputData.getIsMale().doubleValue());
      neuronsNetwork.get(0).get(1).setValue(inputData.getIsAdult().doubleValue());
      neuronsNetwork.get(0).get(2).setValue(inputData.getCabinClass().doubleValue());

      for (int i = 1; i < neuronsNetwork.size(); i++) {
        List<Neuron> neurons = neuronsNetwork.get(i);
        for (Neuron n : neurons) {
          n.setValue(activationSigmoida(sumWeightedIncomingSignals(n)));
        }
      }
      if((inputData.getIsSurvived() == 0 && neuronsNetwork.get(2).get(0).getValue() >= precision) ||
      inputData.getIsSurvived() == 1 && neuronsNetwork.get(2).get(0).getValue() <= precision) {
//        System.out.println("res = " + neuronsNetwork.get(2).get(0).getValue() + " " +
//            "standart = " + inputData.getIsSurvived());
//        System.out.println(inputData);
        if (inputData.getIsSurvived() == 0){
          countWrong0.getAndIncrement();
        } else {
          countWrong1.getAndIncrement();
        }
      }
      clearNeuronValues();
    });
    System.out.println();
    System.out.println("wrong 0 = " + countWrong0.get());
    System.out.println("wrong 1 = " + countWrong1.get());
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
    return teachSpeed * w.getOutputNeuron().getError() * w.getInputNeuron().getValue();
  }

  private Boolean isEndAlg(int count)
  {
    return !(count < limit);
  }

  private void initialisationNetwork()
  {
    int countHideNeurons = 3;
    neuronsNetwork = new ArrayList<>();
    neuronsNetwork.add(nullArray(3));
    neuronsNetwork.add(nullArray(countHideNeurons));
    neuronsNetwork.add(nullArray(1));

    for (int i = 0; i < 3; i++) {
      neuronsNetwork.get(0).set(i, Neuron.builder()
          .inputWeights(null)
          .outputWeights(nullArray(countHideNeurons))
          .error(0D)
          .value(0D)
          .build());
    }

    for (int i = 0; i < countHideNeurons; i++) {
      neuronsNetwork.get(1).set(i, Neuron.builder()
          .inputWeights(new ArrayList<>())
          .outputWeights(nullArray(1))
          .error(0D)
          .value(0D)
          .build());
    }

    neuronsNetwork.get(2).set(0, Neuron.builder()
        .inputWeights(new ArrayList<>())
        .outputWeights(null)
        .error(0D)
        .value(0D)
        .build());

    for (int i = 0; i < neuronsNetwork.size() - 1; i++) {
      List<Neuron> neurons = neuronsNetwork.get(i);
      for (Neuron neuron : neurons) {
        List<Weight> outputWeights = neuron.getOutputWeights();
        for (int k = 0; k < outputWeights.size(); k++) {
          Weight weight = Weight.builder()
              .inputNeuron(neuron)
              .outputNeuron(neuronsNetwork.get(i + 1).get(k))
              .value(Math.random() - 0.5)
              .build();
          neuronsNetwork.get(i+1).get(k).getInputWeights().add(weight);
          outputWeights.set(k, weight);
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

  private <T> List<T> nullArray(int countElements)
  {
    List<T> list = new ArrayList<>();
    for (int i = 0; i < countElements; i++) {
      list.add(null);
    }
    return list;
  }

  @ShellMethod(key = "c")
  public void clearNetwork()
  {
    neuronsNetwork = null;
  }


}

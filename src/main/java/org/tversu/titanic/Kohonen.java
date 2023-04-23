package org.tversu.titanic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class Kohonen
{

  private final Storage storage;
  private List<List<Neuron>> neuronsNetwork;

  private int numbClasses = 2;
  private final int numbInputParams = 3;
  @Value("${border}")
  private Integer border;
  private final Double countAllRecords = 2201D;
  @Value("${teachSpeed}")
  private Double teachSpeed;
  @Value("${limit}")
  private Integer limit;
  @Value("${precision}")
  private Double precision;


  @ShellMethod(key = "tK")
  public void start()
  {
    if (neuronsNetwork == null || neuronsNetwork.size() == 0) {
      initialisationNetwork();
    }

    List<Passenger> forTeach = storage.getForTeach();


    int count = 0;
    while (!isEndAlg(count)) {
      count++;
      Collections.shuffle(forTeach);
      forTeach.forEach(inputData -> {
        //передача входынх параметров
        neuronsNetwork.get(0).get(0).setValue(inputData.getIsMale().doubleValue());
        neuronsNetwork.get(0).get(1).setValue(inputData.getIsAdult().doubleValue());
        neuronsNetwork.get(0).get(2).setValue(inputData.getCabinClass().doubleValue());
        //вычисление значений для нейронов на единственном слое
        for (int i = 1; i < neuronsNetwork.size(); i++) {
          List<Neuron> neurons = neuronsNetwork.get(i);
          for (Neuron n : neurons) {
            n.setValue(sumWeightedIncomingSignals(n));
          }
        }
      });
      //определение нейрона с наименьшим значением
      Neuron n = neuronsNetwork.get(1).stream().reduce((o1, o2) -> o1.getValue() < o2.getValue()? o1 : o2).get();
      //перераспредлеление весов нейрона
      for (Weight w : n.getInputWeights()) {
        w.setValue(w.getValue() + linkWeightChange(w));
      }
    }


  }

  @ShellMethod(key = "testK")
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
          n.setValue(sumWeightedIncomingSignals(n));
        }
      }
      Neuron n = neuronsNetwork.get(1).stream().reduce((o1, o2) -> o1.getValue() < o2.getValue()? o1 : o2).get();
      if((inputData.getIsSurvived() == 0 && n.getValue() >= precision) ||
          inputData.getIsSurvived() == 1 && n.getValue() <= precision) {
        System.out.println("res = " + n.getValue() + " " +
            "standart = " + inputData.getIsSurvived());
        System.out.println(inputData);
        if (inputData.getIsSurvived() == 0){
          countWrong0.getAndIncrement();
        } else {
          countWrong1.getAndIncrement();
        }
      }
//      clearNeuronValues();
    });
    System.out.println();
    System.out.println("must be 0 but 1 = " + countWrong0.get());
    System.out.println("must be 1 but 0 = " + countWrong1.get());
    System.out.println("процент ошибки = " + 100*(countWrong0.get()+countWrong1.get())/(countAllRecords-border));
  }


  private void initialisationNetwork()
  {

    neuronsNetwork = new ArrayList<>();
    neuronsNetwork.add(nullArray(numbInputParams));
    neuronsNetwork.add(nullArray(numbClasses));

    for (int i = 0; i < numbInputParams; i++) {
      neuronsNetwork.get(0).set(i, Neuron.builder()
          .inputWeights(null)
          .outputWeights(nullArray(numbClasses))
          .error(0D)
          .value(0D)
          .build());
    }

    for (int i = 0; i < numbClasses; i++) {
      neuronsNetwork.get(1).set(i, Neuron.builder()
          .inputWeights(new ArrayList<>())
          .outputWeights(null)
          .error(0D)
          .value(0D)
          .build());
    }

    for (int i = 0; i < neuronsNetwork.size() - 1; i++) {
      List<Neuron> neurons = neuronsNetwork.get(i);
      for (Neuron neuron : neurons) {
        List<Weight> outputWeights = neuron.getOutputWeights();
        for (int k = 0; k < outputWeights.size(); k++) {
          double f = Math.random() / Math.nextDown(1.0);
          double x = (0.5 - 1 / Math.sqrt(numbInputParams)) * (1.0 - f) + (0.5 + 1 / Math.sqrt(numbInputParams)) * f;
          Weight weight = Weight.builder()
              .inputNeuron(neuron)
              .outputNeuron(neuronsNetwork.get(i + 1).get(k))
              .value(x)
              .build();
          neuronsNetwork.get(i + 1).get(k).getInputWeights().add(weight);
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

  private Double normalization(double val)
  {
    return null;
  }

  private Double vectDist(Passenger p1, Passenger p2)
  {
    return Math.sqrt(Math.pow(p1.getIsMale() - p2.getIsMale(), 2) + Math.pow(p1.getCabinClass() - p2.getCabinClass(), 2) + Math.pow(p1.getIsAdult(), p2.getIsAdult()));
  }

  @ShellMethod(key = "cK")
  public void clearNetwork()
  {
    neuronsNetwork = null;
  }

  private Boolean isEndAlg(int count)
  {
    return !(count < limit);
  }

  private Double sumWeightedIncomingSignals(Neuron n)
  {
    return Math.sqrt(n.getInputWeights()
        .stream()
        .map(w -> Math.pow(w.getInputNeuron().getValue() - w.getValue(), 2))
        .reduce(0D, Double::sum));
  }

  private Double linkWeightChange(Weight w)
  {
    return teachSpeed * (w.getInputNeuron().getValue() - w.getValue());
  }

}

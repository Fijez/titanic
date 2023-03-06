package org.tversu.titanic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

  public void teach()
  {

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
                    v + (weight == null? 0 : weight * input[finalI]));
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

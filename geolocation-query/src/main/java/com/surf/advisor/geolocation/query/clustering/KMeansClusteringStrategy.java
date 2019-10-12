package com.surf.advisor.geolocation.query.clustering;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

@Slf4j
public class KMeansClusteringStrategy extends ClusteringStrategy<ClusterableGeolocation> {

  private static final int MIN_K = 1;
  private static final int MAX_K = 15;

  private static final int MAX_ITERATIONS = 100;

  /**
   * valid deviation from 1.0 detection of linearity, see usages
   */
  private static final double VALID_DELTA_RATIO_ERROR = 0.3;

  public KMeansClusteringStrategy(RectangleGeolocationRequest request) {
    super(request);
  }

  @Override
  Stream<List<ClusterableGeolocation>> clusterStream(@NotNull Collection<HashGeolocation> input) {

    var clusterableInput = input.stream().map(ClusterableGeolocation::of).collect(toList());

    int maxK = min(input.size(), MAX_K);

    var clusteringResults = IntStream.range(MIN_K, maxK).mapToObj(k -> {

      log.info("Applying K-Means clustering with k={}", k);
      var clusterer = new KMeansPlusPlusClusterer<ClusterableGeolocation>(k, MAX_ITERATIONS);

      return clusterer.cluster(clusterableInput);
    })
      .map(ClusteringResult::new)
      .toArray(ClusteringResult[]::new);

    logSquaredErrors(clusteringResults);

    Function<Integer, Double> getError = r -> clusteringResults[r].getSquaredError();
    Function<Integer, Double> getDelta = r -> abs(getError.apply(r) - getError.apply(r + 1));

    Integer optimalK = null;

    for (int r = 0; r < clusteringResults.length - 2; r++) {
      double deltaRatio = getDelta.apply(r + 1) / getDelta.apply(r);

      log.debug("[{}] / [{}] delta ratio: {}", r + 1, r, deltaRatio);
      if (1 - VALID_DELTA_RATIO_ERROR < deltaRatio && deltaRatio < 1 + VALID_DELTA_RATIO_ERROR) {

        optimalK = r + 1;
        log.info("Optimal cluster count found: {}", optimalK);
        break;
      }
    }

    int k = ofNullable(optimalK).orElse(maxK / 2);

    log.info("Return cluster count: {}", k);

    return of(clusteringResults[k - 1]).map(ClusteringResult::getClusters)
      .orElse(List.of()).stream().map(Cluster::getPoints);
  }

  private void logSquaredErrors(ClusteringResult[] clusteringResults) {
    List<Double> plot = Stream.of(clusteringResults)
      .map(ClusteringResult::getSquaredError)
      .collect(toList());

    log.info("Squared error curve: {}", plot);
  }

  @Getter
  private class ClusteringResult {

    private final List<CentroidCluster<ClusterableGeolocation>> clusters;
    private final double squaredError;

    private ClusteringResult(List<CentroidCluster<ClusterableGeolocation>> clusters) {
      this.clusters = clusters;
      squaredError = calcSquareError(clusters);
    }

    private double calcSquareError(List<CentroidCluster<ClusterableGeolocation>> clusters) {
      return clusters.stream().mapToDouble(cluster -> {
        double[] center = cluster.getCenter().getPoint();

        return cluster.getPoints().stream()
          .map(ClusterableGeolocation::getPoint)
          .mapToDouble(point -> pow(point[0] - center[0], 2) + pow(point[1] - center[1], 2))
          .sum();
      })
        .sum();
    }
  }
}

package com.surf.advisor.geolocation.query.clustering;

import static java.util.stream.Collectors.toList;

import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

@Slf4j
public class DBSCANClusteringStrategy extends ClusteringStrategy<ClusterableGeolocation> {

  public DBSCANClusteringStrategy(RectangleGeolocationRequest request) {
    super(request);
  }

  @Override
  Stream<List<ClusterableGeolocation>> clusterStream(@NotNull Collection<HashGeolocation> input) {

    double eps = getQueryRecSize() / 10.0;

    log.info("Applying DBSCAN clustering with eps: {}", eps);

    var clusterableInput = input.stream().map(ClusterableGeolocation::of).collect(toList());

    var clusterer = new DBSCANClusterer<ClusterableGeolocation>(eps, 1);

    var classifiedAsNoise = new HashSet<>(clusterableInput);

    var clusters = clusterer.cluster(clusterableInput).stream()
      .map(Cluster::getPoints)
      .peek(detectedCluster -> detectedCluster.forEach(classifiedAsNoise::remove))
      .collect(toList());

    log.info("detected {} clusters, points classified as noise: {}", clusters.size(),
      getIds(classifiedAsNoise));

    classifiedAsNoise.stream().map(List::of).forEach(clusters::add);

    return clusters.stream();
  }
}

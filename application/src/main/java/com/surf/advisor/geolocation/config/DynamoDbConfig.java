package com.surf.advisor.geolocation.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.geo.GeoConfig;
import com.amazonaws.geo.s2.internal.GeoQueryClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDbConfig {

  @Value("${amazon.access.key}")
  private String awsAccessKey;

  @Value("${amazon.access.secret-key}")
  private String awsSecretKey;

  @Value("${amazon.region}")
  private String awsRegion;

  @Value("${amazon.end-point.url}")
  private String awsDynamoDBEndPoint;

  @Bean
  public DynamoDBMapper mapper() {
    return new DynamoDBMapper(amazonDynamoDBConfig());
  }

  @Bean
  public GeoQueryClient geoQueryClient() {
    return new GeoQueryClient(
      amazonDynamoDBConfig(),
      new ThreadPoolExecutor(8, 16, 0L, MILLISECONDS, new LinkedBlockingQueue<>())
    );
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDBConfig() {
    return AmazonDynamoDBClientBuilder.standard()
      .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsDynamoDBEndPoint, awsRegion))
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
      .build();
  }

  @Bean
  public GeoConfig geoConfig() {
    return new GeoConfig.Builder()
      .geoHashColumn("geoHash")
      .geoHashKeyColumn("geoHashKey")
      .geoIndexName("geoHashKey-geoHash-index")
      .geoHashKeyLength(6)
      .build();
  }

}

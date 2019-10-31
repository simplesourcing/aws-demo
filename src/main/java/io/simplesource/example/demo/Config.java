package io.simplesource.example.demo;

public final class Config {
    public final String kafkaGroupId;
    public final String kafkaBootstrapServers;
    public final String elasticsearchHost;
    public final int elasticsearchPort;


    public Config(String kafkaGroupId, String kafkaBootstrapServers, String elasticsearchHost, int elasticsearchPort) {
        this.kafkaGroupId = kafkaGroupId;
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.elasticsearchHost = elasticsearchHost;
        this.elasticsearchPort = elasticsearchPort;
    }
}

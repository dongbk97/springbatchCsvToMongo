package com.example.springbatch.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.example.springbatch.repo.TranstemplateRepo;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.session.ClientSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;

@Configuration
@EnableMongoRepositories(/*basePackages = "com.baeldung.repository")*/basePackageClasses = TranstemplateRepo.class, mongoTemplateRef = "mongoTemplate")
@EnableConfigurationProperties
public class MongoConfig {
    @Value("${spring.data.mongodb.primary.host}")
    private String host;
    @Value("${spring.data.mongodb.primary.port}")
    private Integer port;
    @Value("${spring.data.mongodb.primary.database}")
    private String database;
    @Value("${spring.data.mongodb.primary.replica-set-name}")
    private String replica;


//    @Value("${spring.data.mongodb.uri}")
//    private String url;

    @Bean(name = "primaryProperties")
    @ConfigurationProperties(prefix = "mongodb.primary")
    @Primary
    public MongoProperties primaryProperties() {
        MongoProperties mongoProperties = new MongoProperties();
        mongoProperties.setHost(host);
        mongoProperties.setPort(port);
        mongoProperties.setDatabase(database);
        mongoProperties.setDatabase(replica);
        return mongoProperties;
    }


    @Bean(name = "primaryMongoClient")
    public MongoClient mongoClient(@Qualifier("primaryProperties") MongoProperties mongoProperties) {


        MongoDriverInformation mongoDriverInformation = MongoDriverInformation.builder().build();


//        MongoCredential credential = MongoCredential.createCredential(mongoProperties.getUsername(), mongoProperties.getAuthenticationDatabase(), mongoProperties.getPassword());
//        MongoClientSettings.Builder builder = MongoClientSettings.builder();
//        builder.applyToClusterSettings(builder1 -> builder1.hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))));
//        builder.credential(credential);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .retryWrites(true)
                .applyToClusterSettings(builder1 -> builder1.hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .readPreference(ReadPreference.primaryPreferred())
                .build();

        MongoClientImpl mongoClient = new MongoClientImpl(mongoClientSettings, mongoDriverInformation);



        ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
                .defaultTransactionOptions(TransactionOptions.builder()
                        .readConcern(ReadConcern.LOCAL)
                        .readPreference(ReadPreference.primaryPreferred())
                        .build())
                .build();

        // Tạo ClientSession
        ClientSession clientSession = mongoClient.startSession(sessionOptions);






//        mongoClient.startSession(sessionOptions);


        return mongoClient;


    }


//    @Bean
//    public ConnectionString connectionString() {
//        return new ConnectionString(url);
//    }


    @Primary
    @Bean(name = "primaryMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("primaryMongoClient") MongoClient mongoClient, @Qualifier("primaryProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

//    @Primary
//    @Bean(name = "mongoTemplate")
//    public MongoTemplate mongoTemplate(@Qualifier("primaryMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
//        return new MongoTemplate(mongoDatabaseFactory);
//    }

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("primaryMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTemplate primaryMongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        primaryMongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
        return primaryMongoTemplate;
    }

    @Bean(name = "transactionManager")
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        MongoTransactionManager transactionManager = new MongoTransactionManager(dbFactory);
        transactionManager.setRollbackOnCommitFailure(true);
        return new MongoTransactionManager(dbFactory);
    }

}

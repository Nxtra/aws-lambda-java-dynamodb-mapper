package io.theclouddeveloper.memorycards.di;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class DIModule {

    @Provides
    @Named("Region")
    String provideRegion(){
        return System.getenv("REGION") != null ? System.getenv("REGION") : "us-east-1";
    }

    @Provides
    @Singleton
    AmazonDynamoDB provideDynamoDBClient(@Named("Region") String region) {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(region)
                .build();
    }

    @Provides
    @Singleton
    DynamoDBMapper provideDynamoDBMapper(AmazonDynamoDB dynamoDbClient){
        return new DynamoDBMapper(dynamoDbClient);
    }

    @Provides
    @Singleton
    Gson provideGson(){
        return new GsonBuilder().create();
    }
}

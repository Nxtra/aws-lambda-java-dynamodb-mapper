package io.theclouddeveloper.memorycards.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.theclouddeveloper.memorycards.model.MemoryCard;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class MemoryCardDynamoDBService {

    private AmazonDynamoDB dynamoDbClient;
    private DynamoDBMapper dynamoDBMapper;

    @Inject
    public MemoryCardDynamoDBService(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        this.dynamoDbClient = amazonDynamoDB;
        this.dynamoDBMapper = dynamoDBMapper;
}

    public void saveMemoryCard(MemoryCard memoryCard) {
        log.info("Saving memoryCard: {}", memoryCard);
        dynamoDBMapper.save(memoryCard);
    }

    public List<MemoryCard> scanAllMemoryCards() {
        log.info("Scanning for all memorCards");
        List<MemoryCard> memoryCards = dynamoDBMapper.scan(MemoryCard.class, new DynamoDBScanExpression().withLimit(100)); // I limit the result here cause I don't wan't to add the extra complexity of pagination to this project
        return memoryCards;
    }

    public Optional<MemoryCard> getMemoryCardByUuid(String uuid) {
        MemoryCard memoryCard = new MemoryCard();
        memoryCard.setUuid(uuid);
        DynamoDBQueryExpression<MemoryCard> queryExpression = new DynamoDBQueryExpression<MemoryCard>()
                .withIndexName("uuidIndex")
                .withHashKeyValues(memoryCard)
                .withConsistentRead(false)
                .withLimit(1);
        List<MemoryCard> queryResult = dynamoDBMapper.query(MemoryCard.class, queryExpression);
        return queryResult.stream().findAny();
    }

    public void deleteMemoryCard(MemoryCard memoryCardToDelete) {
        log.info("Deleting memoryCard with id: {}", memoryCardToDelete.getUuid());
        dynamoDBMapper.delete(memoryCardToDelete);
    }

    public List<MemoryCard> getMemoryCardsFromAuthorWithCategory(String author, String category) {
        log.info("Finding memoryCards from Author: {} with category: {}", author, category);

        Map<String,String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#author","author");
        expressionAttributesNames.put("#category_createdTimestamp","category_createdTimestamp");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":author",new AttributeValue().withS(author));
        expressionAttributeValues.put(":category",new AttributeValue().withS(category));

        DynamoDBQueryExpression<MemoryCard> queryExpression = new DynamoDBQueryExpression<MemoryCard>()
                .withKeyConditionExpression("#author = :author and begins_with(#category_createdTimestamp, :category)")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        List<MemoryCard> queryResult = dynamoDBMapper.query(MemoryCard.class, queryExpression);
        return queryResult.stream().limit(1000).sorted(Comparator.comparing(MemoryCard::getCategoryCreatedTimestamp)).collect(Collectors.toList());
    }
}

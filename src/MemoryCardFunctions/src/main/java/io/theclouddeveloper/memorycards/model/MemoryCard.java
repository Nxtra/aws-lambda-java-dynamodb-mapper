package io.theclouddeveloper.memorycards.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Builder;
import lombok.ToString;


@ToString
@Builder
@DynamoDBTable(tableName="ProductCatalog")
public class MemoryCard {

    private String author;

    private String categoryCreatedTimestamp;
    private String category;
    private String createdTimestamp;
    private String memoryText;
    private String uuid;

    public MemoryCard() {
    }

    public MemoryCard(String author, String categoryCreatedTimestamp, String category, String createdTimestamp, String memoryText, String uuid) {
        this.author = author;
        this.categoryCreatedTimestamp = categoryCreatedTimestamp;
        this.category = category;
        this.createdTimestamp = createdTimestamp;
        this.memoryText = memoryText;
        this.uuid = uuid;
    }

    @DynamoDBHashKey
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    @DynamoDBRangeKey(attributeName="category_createdTimestamp")
    public String getCategoryCreatedTimestamp() {
        return categoryCreatedTimestamp;
    }

    public void setCategoryCreatedTimestamp(String categoryCreationTimestamp) {
        this.categoryCreatedTimestamp = categoryCreationTimestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "createdTimestampIndex")
    public void setCreatedTimestamp(String createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getMemoryText() {
        return memoryText;
    }

    public void setMemoryText(String memoryText) {
        this.memoryText = memoryText;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "uuidIndex")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

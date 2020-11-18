package com.networknt.configserver.provider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class  MongoDBProviderImplTest {
  String configKey = "files/eaap/globals/0.0.1/dev";
  MongoClient mongoClient = MongoClients.create("mongodb://root:example@0.0.0.0:27017");
  MongoDatabase database = mongoClient.getDatabase("configServer");
  MongoCollection<Document> collection = database.getCollection("configs");

  @Test
  public void testInsert() throws IOException {

    Document insert = new Document("_id", configKey);
    List<Document> collect = Files.list(FileSystems.getDefault().getPath("src/test/resources/config")).map(path -> {
      try {
        String content = Files.readAllLines(path).stream().collect(Collectors.joining());
        return new Document("configName", path.getFileName().toString()).append("content", content);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }).filter(Objects::nonNull).collect(Collectors.toList());
    insert.append("configs", collect);
    collection.insertOne(insert);
  }

  @Test
  public void testFind() {
    String configKey = "files/eaap/0.0.1/eaap-service/0.0.1/dev";
    Map<String, Object> configsMap = new HashMap<>();
    Document document = new Document("_id", configKey);
    Document entry;
    entry = collection.find(document).first();
    if (entry != null && !entry.isEmpty()) {
      List<Document> configs = ((List)entry.get("configs"));
      configs.forEach(config -> {
        byte[] content = config.getString("content").getBytes();
        String encodedContent = Base64.getMimeEncoder().encodeToString(content);
        configsMap.put(config.getString("configName"), encodedContent);
      });
    }
  }
}

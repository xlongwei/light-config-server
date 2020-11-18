package com.networknt.configserver.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.networknt.config.Config;
import com.networknt.configserver.constants.ConfigServerConstants;
import com.networknt.server.StartupHookProvider;

import java.util.Map;

/**
 * Created by stevehu on 2017-03-09.
 */
public class MongoStartupHookProvider implements StartupHookProvider {

    public static MongoDatabase db;
    private static final String MONGO_DB_URI = "mongoDBUri";
    private static final String MONGO_DB_NAME = "mongoDBName";


    public void onStartup() {
        System.out.println("MongoStartupHookProvider is called");
        initDataSource();
        System.out.println("MongoStartupHookProvider db = " + db);
    }

    static void initDataSource() {
        //init mongodb connection
        Map<String, Object> configServerConfig = Config.getInstance().getJsonMapConfig(ConfigServerConstants.CONFIG_NAME);
        String url = (String) configServerConfig.get(MONGO_DB_URI);
        String dbName = (String) configServerConfig.get(MONGO_DB_NAME);

        MongoClient mongoClient = MongoClients.create(url);
        db = mongoClient.getDatabase(dbName);
    }
}

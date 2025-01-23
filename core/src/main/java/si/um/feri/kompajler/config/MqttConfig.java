package si.um.feri.kompajler.config;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import si.um.feri.kompajler.screen.MapScreen;

public class MqttConfig {

    private static final String HOSTNAME = "fb8c9bba468848348cfa18678ea11f96.s1.eu.hivemq.cloud";
    private static final String USERNAME = "aljosa1-client";
    private static final String PASSWORD = "Admin123";
    private static final String CLIENT_ID = "client";
    private static MqttConfig instance;
    private MqttAsyncClient mqttClient;

    private MapScreen mapScreen;

    public MqttConfig(MapScreen mapScreen) {
        this.mapScreen = mapScreen;
        try {
            mqttClient = new MqttAsyncClient("ssl://" + HOSTNAME + ":8883", CLIENT_ID, new MemoryPersistence());
        } catch (MqttException e) {
            throw new RuntimeException("Failed to create MQTT client", e);
        }
    }

    public void startMqttClient() {
        initMqttClient();
        connect();
    }

    private void initMqttClient() {
        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println("Connected to " + serverURI);
                subscribe("price");
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + (cause != null ? cause.getMessage() : "Unknown cause"));
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Message received on topic '" + topic + "': " + message.toString());
                String[] parts = message.toString().split("\\|");
                String restaurantName = parts.length > 0 ? parts[0] : "Unknown";
                String newPrice = parts.length > 1 ? parts[1] : "Unknown";
                String restaurantId = parts.length > 2 ? parts[2] : "6669c55a9445f501f7ab07a1";
                showNotification(restaurantId, restaurantName);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Message delivery complete.");
            }
        });
    }

    private void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            mqttClient.connect(options).waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String restaurantId, String restaurantName) {
        System.out.println("Restaurant ID: " + restaurantId);
        mapScreen.priceChange(restaurantId);
    }
}

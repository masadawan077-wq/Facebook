package com.facebook;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;

public class ChatController {

    @FXML
    private Label friendNameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox messageContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField messageInput;

    private String friendUsername;
    private String chatFolderPath;
    private Timeline poller;
    private int lastMessageCount = 0;

    public void initializeChat(String friendUsername) {
        this.friendUsername = friendUsername;
        User friend = Database.LoadUser(friendUsername);
        if (friend != null) {
            friendNameLabel.setText(friend.getFirstname() + " " + friend.getLastname());
        } else {
            friendNameLabel.setText(friendUsername);
        }

        // Determine Chat Folder Path (Alphabetized)
        this.chatFolderPath = Database.Alphabetizefilename(Main.current.getCredentials().getUsername(), friendUsername);

        // Ensure chat exists in DB (create if not)
        // Check if DM_chat exists, if not create logic similar to Page.Inbox_page
        // For simplicity, we assume Database.WriteChat handles it or we just use the
        // folder path for messages.
        // Actually, Database.WriteMessage uses the folder path directly.

        loadMessages();
        startPolling();
    }

    private void loadMessages() {
        messageContainer.getChildren().clear();
        ArrayList<Message> messages = Database.Load_ALLMessages(chatFolderPath);
        lastMessageCount = messages.size();

        for (Message msg : messages) {
            addMessageToView(msg);
        }
        scrollToBottom();
    }

    private void addMessageToView(Message msg) {
        HBox row = new HBox();
        Label bubble = new Label(msg.getText());
        bubble.setWrapText(true);
        bubble.setMaxWidth(300);

        if (msg.getSender().equals(Main.current.getCredentials().getUsername())) {
            row.setAlignment(Pos.CENTER_RIGHT);
            bubble.getStyleClass().add("chat-bubble-sent");
        } else {
            row.setAlignment(Pos.CENTER_LEFT);
            bubble.getStyleClass().add("chat-bubble-received");
        }

        row.getChildren().add(bubble);
        messageContainer.getChildren().add(row);
    }

    private void startPolling() {
        poller = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            updateStatus();
            checkForNewMessages();
        }));
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }

    private void updateStatus() {
        boolean isOnline = Database.Check_Online(friendUsername);
        if (isOnline) {
            statusLabel.setText("Online");
            statusLabel.getStyleClass().removeAll("status-offline");
            statusLabel.getStyleClass().add("status-online");
        } else {
            statusLabel.setText("Offline");
            statusLabel.getStyleClass().removeAll("status-online");
            statusLabel.getStyleClass().add("status-offline");
        }
    }

    private void checkForNewMessages() {
        // Efficiently check for new messages
        // We can use Database.Load_New_Messages if we track size
        ArrayList<Message> newMessages = Database.Load_New_Messages(chatFolderPath, lastMessageCount);
        if (!newMessages.isEmpty()) {
            for (Message msg : newMessages) {
                addMessageToView(msg);
            }
            lastMessageCount += newMessages.size();
            scrollToBottom();
        }
    }

    @FXML
    void onSendClick(ActionEvent event) {
        String text = messageInput.getText();
        if (text.isEmpty())
            return;

        Message msg = new Message(text, Main.current.getCredentials().getUsername());
        Database.WriteMessage(chatFolderPath, msg);

        // Also add to view immediately
        addMessageToView(msg);
        lastMessageCount++; // Increment count since we added one
        scrollToBottom();

        messageInput.clear();

        // Send notification
        Database.Write_Notification(friendUsername, Main.Input_NotificationM());
    }

    @FXML
    void onBackClick(ActionEvent event) {
        if (poller != null) {
            poller.stop();
        }
        // Navigate back to Home
        // Ideally we should have a navigation manager, but for now we can reload
        // HomeView
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    HelloApplication.class.getResource("home-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 1000, 700);
            javafx.stage.Stage stage = (javafx.stage.Stage) messageInput.getScene().getWindow();
            stage.setTitle("Facebook - Home");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}

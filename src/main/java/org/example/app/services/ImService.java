package org.example.app.services;

import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

@Service
public class ImService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ImService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public List<Message> getDialogMessageList(int dialogId, @NotNull User user) {
        return
                (getCurrentDialogById(dialogId, user) != null) ?
                        Objects.requireNonNull(getCurrentDialogById(dialogId, user)).getMessageList() :
                            null;
    }

    @Nullable
    public Dialog getCurrentDialogById(int dialogId, @NotNull User user) {
        for (Dialog dialog : user.getDialogs())
            if (dialog.getDialog_id() == dialogId) {
                return dialog;
            }
        return null;
    }

    public byte[] convertTextIntoImage(String text) {
        if (text == null) {
            return null;
        }
        return Base64.getDecoder().decode(text);
    }

    public Message createMessage(String messageBody, @NotNull Dialog dialog) {
        byte[] imageData = convertTextIntoImage(messageBody);
        ImageIcon imageIcon = new ImageIcon(imageData);
        Image image = imageIcon.getImage();

        Message message = new Message();
        message.setMessage_id(message.hashCode());
        message.setDialog_id(dialog.getDialog_id());
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        message.setBody(messageBody);
        message.setImageBody(image);

        insertMessageIntoDb(message, dialog);

        return message;
    }

    private void insertMessageIntoDb(@NotNull Message message, @NotNull Dialog dialog) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", message.getMessage_id());
        params.put("dialog_id", dialog.getDialog_id());
        params.put("timestamp", message.getTimestamp());
        params.put("body", message.getBody());
        String exp = "INSERT INTO messages(message_id,timestamp,body)" +
                " VALUES (:message_id,:timestamp,:body)";
        jdbcTemplate.update(exp, params);
        exp = "INSERT INTO messages_in_dialog(dialog_id,message_id)" +
                " VALUES (:dialog_id,:message_id)";
        jdbcTemplate.update(exp, params);
    }

    public Dialog createDialog(User dialogOwner) {
        Dialog dialog = new Dialog();
        dialog.setDialog_id(dialog.hashCode());
        dialog.setDialogOwner(dialogOwner);

        insertDialogIntoDb(dialog, dialogOwner);

        return dialog;
    }

    private void insertDialogIntoDb(@NotNull Dialog dialog, @NotNull User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("dialog_id", dialog.hashCode());
        params.put("dialog_owner", user.getUser_id());
        params.put("new_messages_count", dialog.getNewMessagesCount());
        String exp = "INSERT INTO dialog(dialog_id,dialog_owner,partner,subject,new_messages_count)" +
                " VALUES (:dialog_id,:dialog_owner,NULL,NULL,:new_messages_count)";
        jdbcTemplate.update(exp, params);
    }
}
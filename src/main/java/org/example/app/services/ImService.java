package org.example.app.services;

import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ImService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    public List<Message> messageList(int dialog_id, @NotNull User user) {
        for (Dialog dialog : user.getDialogs())
            if (dialog.getDialog_id() == dialog_id) {
                return dialog.getMessageList();
            }
        return null;
    }
}
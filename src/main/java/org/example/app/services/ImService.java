package org.example.app.services;

import org.example.app.db.FindInDb;
import org.example.web.dto.Dialog;
import org.example.web.dto.Message;
import org.example.web.dto.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Service
public class ImService {

    final String FIRST_FOUND_IMAGE_A_CLASS = "serp-item__link";
    final String FIRST_FOUND_IMAGE_CLASS = "serp-item__thumb justifier__thumb";
    private final NamedParameterJdbcTemplate jdbcTemplate;
    Logger logger = Logger.getLogger("logger");

    public ImService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable("Dialog cannot be null")
    public List<Message> getDialogMessageList(int dialogId, @NotNull User user) {
        if ((getCurrentDialogById(dialogId, user) == null)) {
            return null;
        }
        Comparator<Message> messageComparator = Comparator.comparing(Message::getTimestamp, Comparator.naturalOrder());
        List<Message> messageList = Objects.requireNonNull(getCurrentDialogById(dialogId, user)).getMessageList();
        messageList.sort(messageComparator);
        return messageList;
    }

    @Nullable("No such dialog")
    public Dialog getCurrentDialogById(int dialogId, @NotNull User user) {
        for (Dialog dialog : user.getDialogs())
            if (dialog.getDialog_id() == dialogId) {
                return dialog;
            }
        return null;
    }

    @Nullable("No user with this login")
    public User getUserByLogin(String login) {
        return FindInDb.findUserWithoutDialogsByLogin(login, jdbcTemplate);
    }

    private Element getFirstFoundImageDiv(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element firstFoundImageDiv = null;
        for(Element div : doc.getElementsByClass(FIRST_FOUND_IMAGE_A_CLASS)) {
            if (div != null) {
                firstFoundImageDiv = div;
                break;
            }
        }
        return firstFoundImageDiv;
    }

    @Nullable
    private String getImageSrc(String url) throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();
            Element firstFoundImageDiv = doc.getElementsByClass(FIRST_FOUND_IMAGE_A_CLASS).first();
//            if (firstFoundImageDiv == null) {
//                return null;
//            }
            assert firstFoundImageDiv != null;
            Element imageItem = firstFoundImageDiv.getElementsByClass(FIRST_FOUND_IMAGE_CLASS).first();
            assert imageItem != null;
            return imageItem.attributes().get("src").concat(".jpg");
        } catch (HttpStatusException ex) {
            return null;
        }
    }

    @NotNull
    private String convertCyrillic(@NotNull String message){
        char[] abcCyr =   {'а','б','в','г','д', 'ё','е', 'ж','з','ѕ','и','й','к','л','љ','м','н','њ','о','п','р','с','т','ќ','у','ф','х','ц', 'ч', 'ш',  'щ','ъ','ы','ь','э', 'ю', 'я', 'А','Б','В','Г','Д','Ѓ','Е', 'Ж','З','Ѕ','И','Й','К','Л','Љ','М','Н','Њ','О','П','Р','С','Т', 'Ќ', 'У','Ф', 'Х','Ц','Ч','Џ',  'Ш',  'Щ','Ъ','Ы','Ь','Э', 'Ю', 'Я'};
        String[] abcLat = {"a","b","v","g","d","yo","e","zh","z","y","i","j","k","l","q","m","n","w","o","p","r","s","t","k","u","f","h","c","ch","sh","shy", "","y", "","e","yu","ya", "A","B","V","G","D","G","E","Zh","Z","Y","I","J","K","L","Q","M","N","W","O","P","R","S","T","KJ","U","F","H", "C","CH", "X","SH","SHY", "","Y", "","E","YU","YA"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }

    private void setAnyImageAttrToMessage(@NotNull String messageBody, Message message) throws IOException {
        String imageSrc = convertTextIntoImage(messageBody.toLowerCase());
        if (imageSrc == null) {
            byte[] imageData = Base64.getDecoder().decode(convertCyrillic(messageBody));
            ImageIcon imageIcon = new ImageIcon(imageData);
            Image image = imageIcon.getImage();
            message.setImageBody(image);
            message.setByte_code(imageData);
        } else {
            message.setImageSrc(imageSrc);
        }
    }

    @Nullable
    private String convertTextIntoImage(String text) throws IOException {
        if (text == null || text.chars().filter(c -> c != ' ').count() == 0) {
            return null;
        }
        String spaceChar = "%20";
        String url = "https://yandex.ru/images/search?from=tabbar&text=";
        if (text.contains(" ")) {
            String[] wordsInMessage = text.trim().split(" ");
            IntStream
                    .range(0, wordsInMessage.length - 1)
                    .forEach(i -> wordsInMessage[i] = wordsInMessage[i].concat(spaceChar));
            for (String s : wordsInMessage) {
                url = url.concat(s);
            }
        } else {
            url = url.concat(text);
        }
        return getImageSrc(url);
    }

    public Message createMessage(String messageBody, @NotNull Dialog dialog, User user) throws IOException {
        Message message = new Message();
        message.setMessage_id(100000000 + new Random().nextInt(900000000));
        message.setDialog_id(dialog.getDialog_id());
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        message.setBody(messageBody);
        message.setSender(user);
        setAnyImageAttrToMessage(messageBody, message);
        insertMessageIntoDb(message, dialog);
        return message;
    }

    private void insertMessageIntoDb(@NotNull Message message, @NotNull Dialog dialog) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", message.getMessage_id());
        params.put("dialog_id", dialog.getDialog_id());
        params.put("timestamp", message.getTimestamp());
        params.put("body", message.getBody());
        params.put("image_src", message.getImageSrc());
        params.put("byte_code", Arrays.toString(message.getByte_code()));
        params.put("sender", dialog.getDialogOwner().getUser_id());
        String exp = "INSERT INTO messages(message_id,timestamp,body,image_src,byte_code,sender)" +
                " VALUES (:message_id,:timestamp,:body,:image_src,:byte_code,:sender)";
        jdbcTemplate.update(exp, params);
        exp = "INSERT INTO messages_in_dialog(dialog_id,message_id)" +
                " VALUES (:dialog_id,:message_id)";
        jdbcTemplate.update(exp, params);
    }

    public Dialog createDialog(User dialogOwner, User partner, String subject) {
        Dialog dialog = new Dialog();
        dialog.setDialog_id(dialog.hashCode());
        dialog.setDialogOwner(dialogOwner);
        dialog.setPartner(partner);
        dialog.setSubject(subject);
        insertDialogIntoDb(dialog);
        logger.info(dialog.toString());
        return dialog;
    }

    private void insertDialogIntoDb(@NotNull Dialog dialog) {
        Map<String, Object> params = new HashMap<>();
        params.put("dialog_id", dialog.hashCode());
        params.put("dialog_owner", dialog.getDialogOwner().getUser_id());
        params.put("partner", dialog.getPartner().getUser_id());
        params.put("subject", dialog.getSubject());
        params.put("new_messages_count", dialog.getNewMessagesCount());
        String exp = "INSERT INTO dialog(dialog_id,dialog_owner,partner,subject,new_messages_count)" +
                " VALUES (:dialog_id,:dialog_owner,:partner,:subject,:new_messages_count)";
        jdbcTemplate.update(exp, params);
        logger.info("dialog into db - success");
    }
}
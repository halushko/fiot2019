package com.halushko.fiot2019.pk.bot.actions.entities;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.db.DBClass;
import com.halushko.fiot2019.pk.bot.db.DBUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.util.*;

public class Task extends DBClass<Task> {
    private static final Map<Long, TreeSet<Task>> unreadMessages = new LinkedHashMap<>();
    public static final Task INSTANCE = new Task(new Document());
    private static boolean indexesHasBeenGenerated = false;

    private final String taskId;
    private final Long userId;
    private Update update;
    private final Integer date;
    private byte flags = 0;
    private final String text;

    private Task(Document document) {
        if (!document.isEmpty()) {
            taskId = document.getString(TASK_ID_FIELD);
            userId = document.getLong(USER_ID_FIELD);
            flags = document.getInteger(FLAGS_FIELD).byteValue();
            text = document.getString(TEXT_FIELD);
            date = document.getInteger(DATE_FIELD);
        } else {
            flags = 0;
            taskId = "NULL" + new Date().getTime() + new Random().nextInt(1000);
            userId = Long.MAX_VALUE;
            text = "";
            date = 0;
        }
    }

    private Task(@NotNull Update update) {
        if (update == null) {
            throw new NullPointerException("Update can't be NULL");
        } else {
            this.update = update;
        }

        if (update.hasMessage() || update.hasEditedMessage()) {
            this.flags |= update.hasMessage() ? (byte)0 :(byte) 1;
        } else {
            throw new NullPointerException("Can't find text");
        }

        this.flags |= getMessage().hasText() ? (byte) 2 : (byte) 0;
        this.flags |= getMessage().hasDocument() ? (byte) 4 : (byte) 0;

        this.userId = getMessage().getChatId();
        this.text = isText() ? getMessage().getText() : isDocument() ? getMessage().getDocument().getFileId() : "UNSUPPORTED";
        this.date = getMessage().getDate();
        this.taskId = generateUniqueIndexField();
    }

    private boolean isEdited() {
        return (flags & (byte) 1) > 0;
    }

    private boolean isText() {
        return (flags & (byte) 2) > 0;
    }

    private boolean isDocument() {
        return (flags & (byte) 4) > 0;
    }

    public String getTaskId() {
        return taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public Message getMessage() {
        return isEdited() ? getUpdate().getEditedMessage() : getUpdate().getMessage();
    }

    public Update getUpdate() {
        return update;
    }

    private int compareTo(Task compareWith) {
        if (compareWith == null || compareWith.getDate() == null) return 1;
        if (getDate() == null) return -1;
        return getDate().compareTo(compareWith.getDate());
    }

    private String generateUniqueIndexField() {
        Message msg = getMessage();
        return "" + msg.getDate() + msg.getChatId() + new Random().nextInt(1000);
    }

    public static void add(Update update) {
        Task t;
        if (update == null || (t = new Task(update)).getTaskId() == null || t.getUserId() == null) return;

        TreeSet<Task> taskSet;
        if (!unreadMessages.containsKey(t.getUserId())) {
            taskSet = new TreeSet<>(Task::compareTo);
            unreadMessages.put(t.getUserId(), taskSet);
        } else {
            taskSet = unreadMessages.get(t.getUserId());
        }
        taskSet.add(t);
        DBUtil.getInstance().insert(t);
    }

    public static Set<Task> unreadMessages() {
        if (unreadMessages.isEmpty()) {
            return new HashSet<>();
        }
        List<Long> idsToRemove = new ArrayList<>();
        TreeSet<Task> result = null;
        Long id = null;
        for (Map.Entry<Long, TreeSet<Task>> a : unreadMessages.entrySet()) {
            id = a.getKey();
            idsToRemove.add(id);
            if (a.getValue().size() > 5) {
                Bot.sendTextMessage(id, null, "Занадто багато повідомлень було надіслано від Вас. " +
                        "Спробуйте знову, Ваша команда буде оброблена як тільки до Вас дойде черга знову", null);
                id = null;
            } else {
                result = a.getValue();
                break;
            }
        }

        idsToRemove.forEach(unreadMessages::remove);
        return id != null ? result : new HashSet<>();
    }

//    public static Set<Task> getRandomUnreadTask() {
//        Set<Task> result = new TreeSet<>(Task::compareTo);
//        synchronized (RANDOM) {
//            MongoCollection<Task> table = getTable("UnreadMessages");
//            Task random = table.findOneAndDelete(new Document());
//            if (random != null) {
//                result.add(random);
//                Document findBy = new Document("userId", random.userId);
//                for(Task t: table.find(findBy)){
//                    result.add(t);
//                }
//                table.deleteMany(findBy);
//            }
//            result.forEach(Task::setRead);
//        }
//        return result;
//    }

//    private MongoCollection<Task> getTable() {
//        return DBUtil.getInstance().getTable(this, Task.class);
//    }

    @Override
    public String tableName() {
        return "Tasks";
    }

    @Override
    public void regenerateIndexes(MongoCollection<Task> table) {
        table.createIndex(Indexes.text("taskId"), new IndexOptions().unique(true).background(true));
        table.createIndex(Indexes.ascending("userId"), new IndexOptions().background(true));
        indexesHasBeenGenerated = true;
    }

    @Override
    protected boolean isIndexesHasBeenGenerated() {
        return indexesHasBeenGenerated;
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append(USER_ID_FIELD, getUserId());
        doc.append(TASK_ID_FIELD, getTaskId());
        doc.append(DATE_FIELD, getDate());
        doc.append(FLAGS_FIELD, flags);
        doc.append(TEXT_FIELD, getText());
        return doc;
    }

    @Override
    public Task toJavaObject(Document doc) {
        return new Task(doc);
    }

    private final static String TASK_ID_FIELD = "taskId";
    private final static String USER_ID_FIELD = "userId";
    private final static String DATE_FIELD = "date";
    private final static String FLAGS_FIELD = "flags";
    private final static String TEXT_FIELD = "text";
}

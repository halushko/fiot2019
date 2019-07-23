package com.halushko.fiot2019.pk.bot.actions.tasks;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.answers.Answer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public final class Tasks {
    private static final Map<Long, TreeSet<Task>> unreadMessages = new LinkedHashMap<>();
    private static final Map<Long, TreeMap<String, Task>> readMessages = new HashMap<>();
    private static final Map<Long, TreeMap<String, Answer>> answers = new HashMap<>();

    public static boolean addToHistory(Task task, Answer answer){
        Long userId = task.getUserId();
        if(!answers.containsKey(userId)){
            answers.put(userId, new TreeMap<>());
        }
        return answers.get(userId).put(task.getTaskId(), answer) == null;
    }

    public static boolean add(Update update) {
        if (update == null) return false;
        Task t = new Task(update);
        if (t.getUserId() == null) return false;
        if (!unreadMessages.containsKey(t.getUserId())) {
            unreadMessages.put(t.getUserId(), new TreeSet<>(Task::compareTo));
        }
        return unreadMessages.get(t.getUserId()).add(t);
    }

    public static Set<Task> getUnreadMessages() {
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
                if(!readMessages.containsKey(id)){
                    readMessages.put(id, new TreeMap<>());
                }
                TreeMap<String, Task> history = readMessages.get(id);
                for (Task t: result) {
                    history.put(t.getTaskId(), t);
                }
                break;
            }
        }

        idsToRemove.forEach(unreadMessages::remove);
        return id != null ? result : new HashSet<>();
    }
}

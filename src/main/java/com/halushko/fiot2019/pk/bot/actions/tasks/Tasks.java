package com.halushko.fiot2019.pk.bot.actions.tasks;

import com.halushko.fiot2019.pk.bot.Bot;
import com.halushko.fiot2019.pk.bot.actions.answers.Answer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

import static javax.swing.UIManager.get;

public final class Tasks {
    private static final Map<Long, TreeSet<Task>> scheduled = new LinkedHashMap<>();
    private static final Map<Long, TreeMap<String, Answer>> historyAnswer = new HashMap<>();
    private static final Map<Long, TreeMap<String, Task>> historyTask = new HashMap<>();

    public static boolean addToHistory(Task task, Answer answer){
        Long userId = task.getUserId();
        if(!historyAnswer.containsKey(userId)){
            historyAnswer.put(userId, new TreeMap<>());
        }
        return historyAnswer.get(userId).put(task.getTaskId(), answer) == null;
    }

    public static boolean add(Update update) {
        if (update == null) return false;
        Task t = new Task(update);
        if (t.getUserId() == null) return false;
        if (!scheduled.containsKey(t.getUserId())) {
            scheduled.put(t.getUserId(), new TreeSet<>(Task::compareTo));
        }
        return scheduled.get(t.getUserId()).add(t);
    }

    public static Set<Task> getScheduled() {
        if (scheduled.isEmpty()) {
            return new HashSet<>();
        }
        List<Long> idsToRemove = new ArrayList<>();
        TreeSet<Task> result = null;
        Long id = null;
        for (Map.Entry<Long, TreeSet<Task>> a : scheduled.entrySet()) {
            id = a.getKey();
            idsToRemove.add(id);
            if (a.getValue().size() > 5) {
                Bot.sendTextMessage(id, null, "Занадто багато повідомлень було надіслано від Вас. " +
                        "Спробуйте знову, Ваша команда буде оброблена як тільки до Вас дойде черга знову", null);
                id = null;
            } else {
                result = a.getValue();
                if(!historyTask.containsKey(id)){
                    historyTask.put(id, new TreeMap<>());
                }
                TreeMap<String, Task> history = historyTask.get(id);
                for (Task t: result) {
                    history.put(t.getTaskId(), t);
                }
                break;
            }
        }

        idsToRemove.forEach(scheduled::remove);
        return id != null ? result : new HashSet<>();
    }
}

package com.halushko.fiot2019.pk.bot.actions.tasks;

import com.halushko.fiot2019.pk.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public final class Tasks {
    private static final Map<Long, TreeSet<Task>> tasks = new LinkedHashMap<>();

    public static boolean add(Update update){
        if(update == null) return false;
        Task t = new Task(update);
        if(t.getId() == null) return false;
        if(!tasks.containsKey(t.getId())){
            tasks.put(t.getId(), new TreeSet<>(Task::compareTo));
        }
        return tasks.get(t.getId()).add(t);
    }

    public static Set<Task> getTasks() {
        if(tasks.isEmpty()) {
            return new HashSet<>();
        }
        List<Long> idsToRemove = new ArrayList<>();
        TreeSet<Task> result = null;

        for(Map.Entry<Long, TreeSet<Task>> a: tasks.entrySet()){
            Long id = a.getKey();
            if(a.getValue().size() > 5) {
                Bot.sendTextMessage(id, "Занадто багато повідомлень було надіслано від Вас. " +
                        "Спробуйте знову, Ваша команда буде оброблена як тільки до Вас дойде черга знову");
                idsToRemove.add(id);
            } else {
                idsToRemove.add(id);
                result = a.getValue();
                break;
            }
        }

        idsToRemove.forEach(tasks::remove);
        return result != null ? result : new HashSet<>();
    }
}

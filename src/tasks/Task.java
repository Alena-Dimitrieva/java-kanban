package tasks;

import enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id; // уникальный идентификатор
    protected String title;
    protected String description;
    protected Status status;
    protected Duration duration; // продолжительность задачи
    protected LocalDateTime startTime; // дата и время начала

    // Конструктор для создания новой задачи
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW; // по умолчанию новая
    }

    // Новый конструктор с временем и длительностью
    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this(title, description);
        this.startTime = startTime;
        this.duration = duration;
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Добавлено по ТЗ спринта 8

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    //Стандартные методы

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}



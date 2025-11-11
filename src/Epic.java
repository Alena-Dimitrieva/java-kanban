import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds; // список id подзадач
    protected LocalDateTime endTime; // дата и время конца

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new java.util.ArrayList<>();
        // Начальные значения — чтобы не держать null в duration
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    public void updateTimeAndDuration(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            return;
        }

        // Самое раннее начало среди подзадач (игнорируем пустые startTime)
        LocalDateTime earliest = subtasks.stream()
                .filter(s -> s.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        // Самое позднее окончание среди подзадач (игнорируем пустые endTime)
        LocalDateTime latestEnd = subtasks.stream()
                .filter(s -> s.getEndTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Сумма длительностей всех подзадач с duration
        long totalMinutes = subtasks.stream()
                .filter(s -> s.getDuration() != null)
                .mapToLong(s -> s.getDuration().toMinutes())
                .sum();

        // Обновление полей эпика
        this.startTime = earliest;
        this.duration = Duration.ofMinutes(totalMinutes);
        this.endTime = latestEnd;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Переопределённый toString
    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskIds=" + subtaskIds +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }
}
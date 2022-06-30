package spring.audit.domain;

import org.springframework.util.CollectionUtils;
import spring.audit.config.AuditConfigurer;
import spring.audit.type.CommandType;
import spring.audit.type.DisplayType;

import java.util.*;
import java.util.stream.Collectors;

public class AuditAttribute {
    private CommandType commandType = CommandType.UPDATE;
    private boolean isTouched = false;
    private String label = "";
    private String title = "";
    private Map<String, Object> id;
    private Map<String, Object> parameter;
    private List<SqlRow> originRows = new ArrayList<>();
    private List<SqlRow> updatedRows = new ArrayList<>();

    public AuditAttribute() {}

    public AuditTrail toAuditTail(AuditConfigurer configurer) {
        return AuditTrail.builder()
                .commandType(getCommandType())
                .label(getLabel())
                .title(getTitle())
                .content(getDiffString(configurer.displayType()))
                .id(getId())
                .param(getParameter())
                .build();
    }

    public boolean isUpdated() {
        if (!isTouched) {
            return false;
        }
        if (originRows == updatedRows) {
            return false;
        }
        if (isEmpty(originRows) || isEmpty(updatedRows)) {
            return true;
        }
        boolean isUpdated = false;
        int limit = originRows.size();
        for (int i=0; i < limit; i++) {
            SqlRow originRow = originRows.get(i);
            SqlRow updatedRow = updatedRows.get(i);
            boolean isSame = originRow.entrySet().stream()
                    .allMatch(origin -> {
                        String key = origin.getKey();
                        SqlColumn originColumn = origin.getValue();

                        if (!updatedRow.containsKey(key)) {
                            return false;
                        }
                        SqlColumn updatedColumn = updatedRow.get(key);
                        return equalsValue(updatedColumn.getData(), originColumn.getData());
                    });

            if (!isSame) {
                isUpdated = true;
                break;
            }
        }
        return isUpdated;
    }

    private String getDiffString(DisplayType displayType) {
        List<String> result = new ArrayList<>();
        List<SqlRow> tor = originRows;
        List<SqlRow> tur = updatedRows;

        if (originRows == updatedRows) {
            return null;
        }
        if (isEmpty(originRows)) {
            tor = copyAsEmptyList(tur);
        }
        if (isEmpty(updatedRows)) {
            tur = copyAsEmptyList(tor);
        }

        for (int i=0; i < tor.size(); i++) {
            SqlRow originRow = tor.get(i);
            SqlRow updatedRow = tur.get(i);
            String rowString = originRow.entrySet().stream()
                    .filter(origin -> {
                        String key = origin.getKey();
                        SqlColumn originColumn = origin.getValue();

                        if (!updatedRow.containsKey(key)) {
                            return false;
                        }
                        SqlColumn updatedColumn = updatedRow.get(key);
                        return notEqualsValue(updatedColumn.getData(), originColumn.getData());
                    })
                    .map(origin -> toRowString(
                            displayType.displayName(origin.getKey(), origin.getValue().getComment()),
                            origin.getValue().getData(),
                            updatedRow.get(origin.getKey()).getData()
                        )
                    )
                    .collect(Collectors.joining(", "));

            result.add("{ " + rowString + " }");
        }
        return String.join(",  ", result);
    }

    private String toRowString(String label, String originData, String updatedData) {
        return n(label) + ": `" + n(originData) + "` -> `" + n(updatedData) + "`";
    }

    private String n(String n) {
        return n == null ? "" : n;
    }

    private List<SqlRow> copyAsEmptyList(List<SqlRow> target) {
        return target.stream()
                .map(targetRow -> {
                    SqlRow row = new SqlRow();
                    targetRow.forEach((key, value) -> {
                        SqlColumn column = new SqlColumn();
                        column.setComment(value.getComment());
                        row.put(key, column);
                    });
                    return row;
                })
                .collect(Collectors.toList());
    }

    private boolean equalsValue(String s, String s2) {
        return Objects.equals(s, s2);
    }

    private boolean notEqualsValue(String s, String s2) {
        return !Objects.equals(s, s2);
    }

    private boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public Map<String, Object> getId() {
        return id;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    private String getLabel() {
        return label;
    }

    private String getTitle() {
        return title;
    }

    public void setId(Map<String, Object> id) {
        this.id = id;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOriginRows(List<SqlRow> originRows) {
        if (isEmpty(originRows)) {
            this.commandType = CommandType.INSERT;
        }
        this.isTouched = false;
        this.originRows = originRows;
    }

    public void setUpdatedRows(List<SqlRow> updatedRows) {
        if (isEmpty(updatedRows) && !isEmpty(originRows)) {
            this.commandType = CommandType.DELETE;
        }
        this.isTouched = true;
        this.updatedRows = updatedRows;
    }

}
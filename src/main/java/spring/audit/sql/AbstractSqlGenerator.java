package spring.audit.sql;

import java.util.List;

public abstract class AbstractSqlGenerator implements AuditSqlGenerator {

    public static final String COMMENT_SUFFIX = "_$$COMMENTS";

    protected final String addCommentSuffix(String str) {
        return str + COMMENT_SUFFIX;
    }

    public abstract String makeSelectSqlWithComment(List<String> columnNames, String tableName);

}
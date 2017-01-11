package sk.r3n.plugin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;
import sk.r3n.util.FileUtil;

public class StructureWriter implements Serializable {

    public void write(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        log.info("Sequences");
        writeSequencies(log, overwrite, targetDir, targetPackage, structure);

        log.info("Tables");
        writeTables(log, overwrite, targetDir, targetPackage, structure);

        log.info("Dtos");
        File dtoDir = new File(targetDir, "dto");
        dtoDir.mkdirs();
        targetPackage = targetPackage + ".dto";
        writeDtos(log, overwrite, dtoDir, targetPackage, structure);
    }

    private void writeSequencies(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        File file = new File(targetDir, "SEQUENCE.java");
        if (!file.exists() || overwrite) {
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(targetPackage).append(";\n");
            sb.append("\n");
            sb.append("import java.io.Serializable;\n");
            sb.append("import sk.r3n.sql.Sequence;\n");
            sb.append("\n");
            sb.append("public class SEQUENCE implements Serializable {\n");
            sb.append("\n");
            structure.getSequences().forEach((sequence) -> {
                sb.append(sequenceJava(sequence));
            });
            sb.append("\n");
            sb.append("}\n");
            FileUtil.write(file, sb.toString().getBytes());
        } else {
            log.info("SKIPPED");
        }
    }

    private String sequenceJava(Sequence sequence) {
        StringBuilder sb = new StringBuilder();
        sb.append("    public static Sequence ").append(sequence.getName().toUpperCase()).append("() {\n");
        sb.append("        return new Sequence(\"").append(sequence.getName().toLowerCase()).append("\");\n");
        sb.append("    }\n");
        return sb.toString();
    }

    private void writeTables(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        File file = new File(targetDir, "TABLE.java");
        if (!file.exists() || overwrite) {
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(targetPackage).append(";\n");
            sb.append("\n");
            sb.append("import java.io.Serializable;\n");
            sb.append("import sk.r3n.sql.Table;\n");
            sb.append("\n");
            sb.append("public class TABLE implements Serializable {\n");
            structure.getTables().forEach((table) -> {
                sb.append(tableJava(table));
            });
            sb.append("\n");
            sb.append("}\n");
            FileUtil.write(file, sb.toString().getBytes());
        } else {
            log.info("SKIPPED");
        }

        for (Table table : structure.getTables()) {
            file = new File(targetDir, table.getName().toUpperCase() + ".java");
            if (!file.exists() || overwrite) {
                StringBuilder sb = new StringBuilder();
                sb.append("package ").append(targetPackage).append(";\n");
                sb.append("\n");
                sb.append("import java.io.Serializable;\n");
                sb.append("import sk.r3n.sql.Column;\n");
                sb.append("import sk.r3n.sql.DataType;\n");
                sb.append("\n");
                sb.append("public class ").append(table.getName().toUpperCase()).append(" implements Serializable {\n");
                structure.getColumns(table).forEach((column) -> {
                    sb.append(columnJava(table, column));
                });
                sb.append("\n");
                sb.append("    public static Column[] columns() {\n");
                sb.append("        return new Column[]{");
                for (int i = 0; i < structure.getColumns(table).size(); i++) {
                    Column column = structure.getColumns(table).get(i);
                    sb.append(column.getName().toUpperCase()).append("()");
                    if (i < structure.getColumns(table).size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("};\n");
                sb.append("    }\n");
                sb.append("\n");
                sb.append("    public static Column[] columns(String alias) {\n");
                sb.append("        return new Column[]{");
                for (int i = 0; i < structure.getColumns(table).size(); i++) {
                    Column column = structure.getColumns(table).get(i);
                    sb.append(column.getName().toUpperCase()).append("(alias)");
                    if (i < structure.getColumns(table).size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("};\n");
                sb.append("    }\n");
                sb.append("}\n");
                FileUtil.write(file, sb.toString().getBytes());
            } else {
                log.info("SKIPPED");
            }
        }
    }

    private String tableJava(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    public static Table ").append(table.getName().toUpperCase()).append("() {\n");
        sb.append("        return new Table(\"").append(table.getName().toLowerCase()).append("\", \"").append(table.getAlias().toLowerCase()).append("\");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static Table ").append(table.getName().toUpperCase()).append("(String alias) {\n");
        sb.append("        return new Table(\"").append(table.getName().toLowerCase()).append("\", alias);\n");
        sb.append("    }\n");
        return sb.toString();
    }

    private String columnJava(Table table, Column column) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    public static Column ").append(column.getName().toUpperCase()).append("() {\n");
        sb.append("        return new Column(\"").append(column.getName().toLowerCase()).append("\", TABLE.").append(table.getName().toUpperCase()).append("(), DataType.").append(column.getDataType()).append(");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static Column ").append(column.getName().toUpperCase()).append("(String alias) {\n");
        sb.append("        return new Column(\"").append(column.getName().toLowerCase()).append("\", TABLE.").append(table.getName().toUpperCase()).append("(alias), DataType.").append(column.getDataType()).append(");\n");
        sb.append("    }\n");
        return sb.toString();
    }

    private void writeDtos(Log log, boolean overwrite, File dtoDir, String targetPackage, Structure structure) {
        for (Table table : structure.getTables()) {
            String className = toCamelCase(false, table.getName(), "_");

            File file = new File(dtoDir, className + ".java");
            if (!file.exists() || overwrite) {
                StringBuilder sb = new StringBuilder();
                sb.append("package ").append(targetPackage).append(";\n");
                sb.append("\n");
                sb.append("import java.io.Serializable;\n");
                for (Column column : structure.getColumns(table)) {
                    if (column.getDataType() == DataType.BLOB) {
                        sb.append("import java.io.File;\n");
                        break;
                    }
                }
                for (Column column : structure.getColumns(table)) {
                    if (column.getDataType() == DataType.BIG_DECIMAL) {
                        sb.append("import java.math.BigDecimal;\n");
                        break;
                    }
                }
                for (Column column : structure.getColumns(table)) {
                    if (column.getDataType() == DataType.DATE
                            || column.getDataType() == DataType.TIME
                            || column.getDataType() == DataType.TIME_STAMP) {
                        sb.append("import java.util.Date;\n");
                        break;
                    }
                }
                sb.append("import sk.r3n.dto.ColumnId;\n");
                sb.append("\n");
                sb.append("public class ").append(className).append(" implements Serializable {\n");

                List<String> fields = new ArrayList();
                List<String> methods = new ArrayList();
                structure.getColumns(table).forEach((column) -> {
                    fillFieldAndMethods(table, column, fields, methods);
                });

                fields.forEach((field) -> {
                    sb.append(field);
                });

                methods.forEach((method) -> {
                    sb.append(method);
                });

                sb.append("\n");
                sb.append("    @Override\n");
                sb.append("    public String toString() {\n");
                sb.append("        return \"").append(className).append("{\"");
                List<Column> columns = structure.getColumns(table);
                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);
                    String fieldName = toCamelCase(true, column.getName(), "_");
                    sb.append(" + \"");
                    if (i < fields.size() && i > 0) {
                        sb.append(",");
                    }
                    sb.append(" ").append(fieldName).append("=\" + ").append(fieldName);
                }
                sb.append(" + '}';\n");
                sb.append("    }\n");
                sb.append("\n");
                sb.append("}\n");
                FileUtil.write(file, sb.toString().getBytes());
            } else {
                log.info("SKIPPED");
            }
        }
    }

    private void fillFieldAndMethods(Table table, Column column, List<String> fields, List<String> methods) {
        String fieldName = toCamelCase(true, column.getName(), "_");

        String type = "";
        switch (column.getDataType()) {
            case BOOLEAN:
                type = "Boolean";
                break;
            case STRING:
                type = "String";
                break;
            case SHORT:
                type = "Short";
                break;
            case INTEGER:
                type = "Integer";
                break;
            case LONG:
                type = "Long";
                break;
            case BIG_DECIMAL:
                type = "BigDecimal";
                break;
            case DATE:
            case TIME:
            case TIME_STAMP:
                type = "Date";
                break;
            case BLOB:
                type = "File";
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("    @ColumnId(");
        sb.append("table = \"").append(table.getName().toLowerCase()).append("\", ");
        sb.append("column = \"").append(column.getName().toLowerCase()).append("\"");
        sb.append(")\n");
        sb.append("    protected ").append(type).append(" ").append(fieldName).append(";\n");
        fields.add(sb.toString());

        sb = new StringBuilder();
        sb.append("\n");
        sb.append("    public ").append(type).append(" get")
                .append(Character.toString(fieldName.charAt(0)).toUpperCase()).append(fieldName.substring(1))
                .append("() {\n");
        sb.append("        return ").append(fieldName).append(";\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public void set").append(Character.toString(fieldName.charAt(0)).toUpperCase()).append(fieldName.substring(1))
                .append("(").append(type).append(" ").append(fieldName).append(") {\n");
        sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
        sb.append("    }\n");
        methods.add(sb.toString());
    }

    private String toCamelCase(boolean firstLower, String string, String regex) {
        StringBuilder sb = new StringBuilder();

        String[] parts = string.toLowerCase().split(regex);

        boolean first = true;

        for (String part : parts) {
            if (first && firstLower) {
                sb.append(part);
                first = false;
            } else {
                if (part.length() < 2) {
                    sb.append(part.toUpperCase());
                } else {
                    sb.append(Character.toString(part.charAt(0)).toUpperCase());
                    sb.append(part.substring(1));
                }
            }
        }

        return sb.toString();
    }
}

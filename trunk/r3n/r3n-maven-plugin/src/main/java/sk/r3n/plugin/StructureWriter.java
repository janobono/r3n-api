package sk.r3n.plugin;

import java.io.File;
import java.io.Serializable;
import org.apache.maven.plugin.logging.Log;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;
import sk.r3n.util.FileUtil;

/**
 *
 * @author jan
 */
public class StructureWriter implements Serializable {

    public void write(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        log.info("Sequences");
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
            for (Sequence sequence : structure.getSequences()) {
                sb.append("    public static Sequence ").append(sequence.getName().toUpperCase()).append("() {\n");
                sb.append("        return new Sequence(\"").append(sequence.getName()).append("\");\n");
                sb.append("    }\n");
            }
            sb.append("\n");
            sb.append("}\n");
            FileUtil.write(file, sb.toString().getBytes());
        } else {
            log.info("SKIPPED");
        }

        log.info("Tables");
        file = new File(targetDir, "TABLE.java");
        if (!file.exists() || overwrite) {
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(targetPackage).append(";\n");
            sb.append("\n");
            sb.append("import java.io.Serializable;\n");
            sb.append("import sk.r3n.sql.Table;\n");
            sb.append("\n");
            sb.append("public class TABLE implements Serializable {\n");
            for (Table table : structure.getTables()) {
                sb.append("\n");
                sb.append("    public static Table ").append(table.getName().toUpperCase()).append("() {\n");
                sb.append("        return new Table(\"").append(table.getName()).append("\", \"").append(table.getAlias()).append("\");\n");
                sb.append("    }\n");
                sb.append("\n");
                sb.append("    public static Table ").append(table.getName().toUpperCase()).append("(String alias) {\n");
                sb.append("        return new Table(\"").append(table.getName()).append("\", alias);\n");
                sb.append("    }\n");
            }
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
                for (Column column : structure.getColumns(table)) {
                    sb.append("\n");
                    sb.append("    public static Column ").append(column.getName().toUpperCase()).append("() {\n");
                    sb.append("        return new Column(\"").append(column.getName()).append("\", TABLE.").append(table.getName().toUpperCase()).append("(), DataType.").append(column.getDataType()).append(");\n");
                    sb.append("    }\n");
                    sb.append("\n");
                    sb.append("    public static Column ").append(column.getName().toUpperCase()).append("(String alias) {\n");
                    sb.append("        return new Column(\"").append(column.getName()).append("\", TABLE.").append(table.getName().toUpperCase()).append("(alias), DataType.").append(column.getDataType()).append(");\n");
                    sb.append("    }\n");
                }
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

        File dtoDir = new File(targetDir, "dto");
        dtoDir.mkdirs();
        targetPackage = targetPackage + ".dto";

        for (Table table : structure.getTables()) {
            String className = "";
            String[] subNames = table.getName().toLowerCase().split("_");
            for (String subName : subNames) {
                className += Character.toString(subName.charAt(0)).toUpperCase() + subName.substring(1);
            }
            file = new File(dtoDir, className + ".java");
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
                sb.append("import sk.r3n.dto.TableId;\n");
                sb.append("import sk.r3n.dto.ColumnId;\n");
                sb.append("\n");
                sb.append("@TableId(name = \"").append(table.getName()).append("\")\n");
                sb.append("public class ").append(className).append(" implements Serializable {\n");
                for (Column column : structure.getColumns(table)) {
                    String fieldName = "";
                    subNames = column.getName().toLowerCase().split("_");
                    boolean first = true;
                    for (String subName : subNames) {
                        if (first) {
                            fieldName += subName;
                            first = false;
                        } else {
                            fieldName += Character.toString(subName.charAt(0)).toUpperCase() + subName.substring(1);
                        }
                    }
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

                    sb.append("\n");
                    sb.append("    @ColumnId(name = \"").append(column.getName()).append("\")\n");
                    sb.append("    protected ").append(type).append(" ").append(fieldName).append(";\n");
                    sb.append("\n");
                    sb.append("    public ").append(type).append(" get")
                            .append(Character.toString(fieldName.charAt(0)).toUpperCase()).append(fieldName.substring(1))
                            .append("() {\n");
                    sb.append("        return ").append(fieldName).append(";\n");
                    sb.append("    }\n");
                    sb.append("\n");
                    sb.append("    public void set").append(Character.toString(fieldName.charAt(0)).toUpperCase()).append(fieldName.substring(1))
                            .append("(").append(type).append(" ").append(fieldName).append("){\n");
                    sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
                    sb.append("    }\n");
                }
                sb.append("\n");
                sb.append("}\n");
                FileUtil.write(file, sb.toString().getBytes());
            } else {
                log.info("SKIPPED");
            }
        }

    }
}

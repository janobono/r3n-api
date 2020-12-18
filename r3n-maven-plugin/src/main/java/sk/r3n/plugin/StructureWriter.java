/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin;

import org.apache.maven.plugin.logging.Log;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;
import sk.r3n.util.FileUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Java objects writer.
 *
 * @author janobono
 * @since 21 August 2014
 */
class StructureWriter implements Serializable {

    private static final String REPLACE_PACKAGE = "<REPLACE_PACKAGE>";
    private static final String REPLACE_IMPORT = "<REPLACE_IMPORT>";
    private static final String REPLACE_CLASS_NAME = "<REPLACE_CLASS_NAME>";
    private static final String REPLACE_DEFINITION = "<REPLACE_DEFINITION>";
    private static final String REPLACE_TABLE_NAME = "<REPLACE_TABLE_NAME>";

    private final String sequenceTemplate;
    private final String tableTemplate;
    private final String columnTemplate;
    private final String dtoTemplate;

    public StructureWriter(String sequenceTemplate, String tableTemplate, String columnTemplate, String dtoTemplate) {
        this.sequenceTemplate = sequenceTemplate;
        this.tableTemplate = tableTemplate;
        this.columnTemplate = columnTemplate;
        this.dtoTemplate = dtoTemplate;
    }

    public void write(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        log.info("Sequences");
        File r3nDir = new File(targetDir, "r3n");
        r3nDir.mkdirs();
        writeSequences(log, overwrite, r3nDir, targetPackage + ".r3n", structure);

        log.info("Tables");
        writeTables(log, overwrite, r3nDir, targetPackage + ".r3n", structure);

        log.info("Dtos");
        writeDtos(log, overwrite, targetDir, targetPackage, structure);
    }

    private void writeSequences(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        File file = new File(targetDir, "MetaSequence.java");
        if (!file.exists() || overwrite) {
            String content = sequenceTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
            content = content.replaceAll(REPLACE_DEFINITION, sequencesToString(structure.getSequences()));
            FileUtil.write(file, content.getBytes());
        } else {
            log.info("SKIPPED");
        }
    }

    private void writeTables(Log log, boolean overwrite, File targetDir, String targetPackage, Structure structure) {
        File file = new File(targetDir, "MetaTable.java");
        if (!file.exists() || overwrite) {
            String content = tableTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
            content = content.replaceAll(REPLACE_DEFINITION, tablesToString(structure.getTables()));
            FileUtil.write(file, content.getBytes());
        } else {
            log.info("SKIPPED");
        }

        for (Table table : structure.getTables()) {
            String className = "MetaColumn" + toCamelCase(false, table.getName());
            file = new File(targetDir, className + ".java");
            if (!file.exists() || overwrite) {
                String content = columnTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
                content = content.replaceAll(REPLACE_CLASS_NAME, toCamelCase(false, table.getName()));
                content = content.replaceAll(REPLACE_DEFINITION, columnsToString(structure.getColumns(table)));
                content = content.replaceAll(REPLACE_TABLE_NAME, table.getName().toUpperCase());
                FileUtil.write(file, content.getBytes());
            } else {
                log.info("SKIPPED");
            }
        }
    }

    private void writeDtos(Log log, boolean overwrite, File dtoDir, String targetPackage, Structure structure) {
        for (Table table : structure.getTables()) {
            String className = toCamelCase(false, table.getName()) + "Dto";
            File file = new File(dtoDir, className + ".java");
            if (!file.exists() || overwrite) {
                String content = dtoTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
                List<String> lines = new ArrayList<>();
                if (containsDataType(structure.getColumns(table), DataType.BLOB)) {
                    lines.add("import java.io.File;");
                }
                lines.add("import java.io.Serializable;");
                if (containsDataType(structure.getColumns(table), DataType.BIG_DECIMAL)) {
                    lines.add("import java.math.BigDecimal;");
                }
                if (containsDataType(structure.getColumns(table), DataType.DATE)) {
                    lines.add("import java.time.LocalDate;");
                }
                if (containsDataType(structure.getColumns(table), DataType.TIME_STAMP)) {
                    lines.add("import java.time.LocalDateTime;");
                }
                if (containsDataType(structure.getColumns(table), DataType.TIME)) {
                    lines.add("import java.time.LocalTime;");
                }
                content = content.replaceAll(REPLACE_IMPORT, linesToString(lines.toArray(new String[0])));
                content = content.replaceAll(REPLACE_CLASS_NAME, className);

                lines = new ArrayList<>();
                List<Column> columns = structure.getColumns(table);
                for (int index = 0; index < columns.size(); index++) {
                    lines.add(columnToString(table, columns.get(index)));
                    if (index < columns.size() - 1) {
                        lines.add("");
                    }
                }
                content = content.replaceAll(REPLACE_DEFINITION, linesToString(lines.toArray(new String[0])));
                FileUtil.write(file, content.getBytes());
            } else {
                log.info("SKIPPED");
            }
        }
    }

    private String sequencesToString(List<Sequence> sequences) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < sequences.size(); index++) {
            sb.append("    ");
            sb.append(sequences.get(index).getName().toUpperCase());
            sb.append("(\"");
            sb.append(sequences.get(index).getName().toLowerCase());
            sb.append("\")");
            if (index < sequences.size() - 1) {
                sb.append(",");
                sb.append("\n");
            } else {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private String tablesToString(List<Table> tables) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < tables.size(); index++) {
            sb.append("    ");
            sb.append(tables.get(index).getName().toUpperCase());
            sb.append("(\"");
            sb.append(tables.get(index).getName().toLowerCase());
            sb.append("\", \"");
            sb.append(tables.get(index).getAlias().toLowerCase());
            sb.append("\")");
            if (index < tables.size() - 1) {
                sb.append(",");
                sb.append("\n");
            } else {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private String columnsToString(List<Column> columns) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < columns.size(); index++) {
            sb.append("    ");
            sb.append(columns.get(index).getName().toUpperCase());
            sb.append("(\"");
            sb.append(columns.get(index).getName().toLowerCase());
            sb.append("\", DataType.");
            sb.append(columns.get(index).getDataType().name());
            sb.append(")");
            if (index < columns.size() - 1) {
                sb.append(",");
                sb.append("\n");
            } else {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private boolean containsDataType(List<Column> columns, DataType dataType) {
        for (Column column : columns) {
            if (column.getDataType() == dataType) {
                return true;
            }
        }
        return false;
    }

    private String columnToString(Table table, Column column) {
        StringBuilder sb = new StringBuilder();
        sb.append("    @ColumnId(table = \"");
        sb.append(table.getName().toLowerCase());
        sb.append("\", column = \"");
        sb.append(column.getName().toLowerCase());
        sb.append("\")\n");
        sb.append("    private ");
        switch (column.getDataType()) {
            case BOOLEAN:
                sb.append("Boolean");
                break;
            case STRING:
                sb.append("String");
                break;
            case SHORT:
                sb.append("Short");
                break;
            case INTEGER:
                sb.append("Integer");
                break;
            case LONG:
                sb.append("Long");
                break;
            case BIG_DECIMAL:
                sb.append("BigDecimal");
                break;
            case DATE:
                sb.append("LocalDate");
                break;
            case TIME:
                sb.append("LocalTime");
                break;
            case TIME_STAMP:
                sb.append("LocalDateTime");
                break;
            case BLOB:
                sb.append("File");
                break;
        }
        sb.append(" ");
        sb.append(toCamelCase(true, column.getName()));
        sb.append(";");
        return sb.toString();
    }

    private String toCamelCase(boolean firstLower, String string) {
        StringBuilder sb = new StringBuilder();

        String[] parts = string.toLowerCase().split("_");

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

    private String linesToString(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (line.length() > 0) {
                sb.append(line);
            }
            if (index < lines.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}

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
import sk.r3n.sql.impl.ColumnBase;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Java objects writer.
 *
 * @author janobono
 * @since 21 August 2014
 */
class StructureWriter implements Serializable {

    private static final String REPLACE_PACKAGE = "<REPLACE_PACKAGE>";
    private static final String REPLACE_DEFINITION = "<REPLACE_DEFINITION>";

    private static final String REPLACE_ENUM_NAME = "<REPLACE_ENUM_NAME>";
    private static final String REPLACE_TABLE_NAME = "<REPLACE_TABLE_NAME>";

    private static final String REPLACE_IMPORT = "<REPLACE_IMPORT>";
    private static final String REPLACE_RECORD_NAME = "<REPLACE_RECORD_NAME>";
    private static final String REPLACE_RECORD_MEMBERS = "<REPLACE_RECORD_MEMBERS>";
    private static final String REPLACE_RECORD_METHODS = "<REPLACE_RECORD_METHODS>";

    private final String sequenceTemplate;
    private final String tableTemplate;
    private final String columnTemplate;
    private final String dtoTemplate;

    public StructureWriter(final String sequenceTemplate, final String tableTemplate, final String columnTemplate, final String dtoTemplate) {
        this.sequenceTemplate = sequenceTemplate;
        this.tableTemplate = tableTemplate;
        this.columnTemplate = columnTemplate;
        this.dtoTemplate = dtoTemplate;
    }

    public void write(final Log log, final boolean blobFile, final boolean overwrite, final File targetDir, final String targetPackage, final Structure structure) {
        final File r3nDir = new File(targetDir, "r3n");
        r3nDir.mkdirs();

        final File r3nMetaDir = new File(r3nDir, "meta");
        r3nMetaDir.mkdirs();
        log.info("Sequences");
        writeSequences(log, overwrite, r3nMetaDir, targetPackage + ".r3n.meta", structure);
        log.info("Tables");
        writeTables(log, overwrite, r3nMetaDir, targetPackage + ".r3n.meta", structure);

        final File r3nDtoDir = new File(r3nDir, "dto");
        r3nDtoDir.mkdirs();
        log.info("Dtos");
        writeDtos(log, blobFile, overwrite, r3nDtoDir, targetPackage + ".r3n.dto", structure);
    }

    private void writeSequences(final Log log, final boolean overwrite, final File targetDir, final String targetPackage, final Structure structure) {
        final File file = new File(targetDir, "MetaSequence.java");
        if (!file.exists() || overwrite) {
            String content = sequenceTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
            content = content.replaceAll(REPLACE_DEFINITION, sequencesToString(structure.getSequences()));
            write(file, content.getBytes());
        } else {
            log.info("SKIPPED");
        }
    }

    private void writeTables(final Log log, final boolean overwrite, final File targetDir, final String targetPackage, final Structure structure) {
        File file = new File(targetDir, "MetaTable.java");
        if (!file.exists() || overwrite) {
            String content = tableTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
            content = content.replaceAll(REPLACE_DEFINITION, tablesToString(structure.getTables()));
            write(file, content.getBytes());
        } else {
            log.info("SKIPPED");
        }

        for (final Table table : structure.getTables()) {
            final String className = "MetaColumn" + toCamelCase(false, table.name());
            file = new File(targetDir, className + ".java");
            if (!file.exists() || overwrite) {
                String content = columnTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
                content = content.replaceAll(REPLACE_ENUM_NAME, toCamelCase(false, table.name()));
                content = content.replaceAll(REPLACE_DEFINITION, columnsToString(structure.getColumns(table)));
                content = content.replaceAll(REPLACE_TABLE_NAME, table.name().toUpperCase());
                write(file, content.getBytes());
            } else {
                log.info("SKIPPED");
            }
        }
    }

    private void writeDtos(final Log log, final boolean blobFile, final boolean overwrite, final File dtoDir, final String targetPackage, final Structure structure) {
        for (final Table table : structure.getTables()) {
            final String recordName = toCamelCase(false, table.name()) + "Dto";
            final File file = new File(dtoDir, recordName + ".java");
            if (!file.exists() || overwrite) {
                String content = dtoTemplate.replaceAll(REPLACE_PACKAGE, targetPackage);
                final List<String> lines = new LinkedList<>();
                if (blobFile && containsDataType(structure.getColumns(table), DataType.BLOB)) {
                    lines.add("import java.io.File;");
                }
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
                String importLines = linesToString(lines.toArray(new String[0]));
                if (importLines.length() > 0) {
                    importLines = "\n" + importLines + "\n";
                }
                content = content.replaceAll(REPLACE_IMPORT, importLines);
                content = content.replaceAll(REPLACE_RECORD_NAME, recordName);
                final List<Column> columns = structure.getColumns(table);
                content = content.replaceAll(REPLACE_RECORD_MEMBERS, columnsToDtoMembers(columns, blobFile));
                content = content.replaceAll(REPLACE_RECORD_METHODS, columnsToDtoMethods(table, columns, blobFile));
                write(file, content.getBytes());
            } else {
                log.info("SKIPPED");
            }
        }
    }

    private String sequencesToString(final List<Sequence> sequences) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < sequences.size(); index++) {
            sb.append("    ");
            sb.append(sequences.get(index).name().toUpperCase());
            sb.append("(\"");
            sb.append(sequences.get(index).name().toLowerCase());
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

    private String tablesToString(final List<Table> tables) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < tables.size(); index++) {
            sb.append("    ");
            sb.append(tables.get(index).name().toUpperCase());
            sb.append("(\"");
            sb.append(tables.get(index).name().toLowerCase());
            sb.append("\", \"");
            sb.append(tables.get(index).alias().toLowerCase());
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

    private String columnsToString(final List<Column> columns) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < columns.size(); index++) {
            sb.append("    ");
            sb.append(((ColumnBase) columns.get(index)).name().toUpperCase());
            sb.append("(\"");
            sb.append(((ColumnBase) columns.get(index)).name().toLowerCase());
            sb.append("\", DataType.");
            sb.append(columns.get(index).dataType().name());
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

    private boolean containsDataType(final List<Column> columns, final DataType dataType) {
        for (final Column column : columns) {
            if (column.dataType() == dataType) {
                return true;
            }
        }
        return false;
    }

    private String columnsToDtoMembers(final List<Column> columns, final boolean blobFile) {
        final StringBuilder sb = new StringBuilder();
        if (columns.size() > 0) {
            sb.append("\n");
        }
        for (int index = 0; index < columns.size(); index++) {
            final Column column = columns.get(index);
            sb.append("        ").append(dataTypeToJava(column.dataType(), blobFile)).append(" ");
            sb.append(toCamelCase(true, ((ColumnBase) column).name()));
            if (index < columns.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String columnsToDtoMethods(final Table table, final List<Column> columns, final boolean blobFile) {
        final String recordName = toCamelCase(false, table.name()) + "Dto";
        final String instanceName = toCamelCase(true, table.name()) + "Dto";
        final StringBuilder sb = new StringBuilder();
        // toArray
        if (columns.size() > 0) {
            sb.append("\n    public static Object[] toArray(final ").append(recordName).append(" ").append(instanceName).append(") {");
            sb.append("\n        return new Object[]{");
            for (int index = 0; index < columns.size(); index++) {
                final Column column = columns.get(index);
                sb.append("\n                ").append(instanceName).append(".");
                sb.append(toCamelCase(true, ((ColumnBase) column).name()));
                if (index < columns.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n        };");
            sb.append("\n    }");
        }
        // toObject
        sb.append("\n");
        if (columns.size() > 0) {
            sb.append("\n    public static ").append(recordName).append(" toObject(final Object[] array) {");
            sb.append("\n        return new ").append(recordName).append("(");
            for (int index = 0; index < columns.size(); index++) {
                final Column column = columns.get(index);
                sb.append("\n                (").append(dataTypeToJava(column.dataType(), blobFile)).append(") array[").append(index).append("]");
                if (index < columns.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n        );");
            sb.append("\n    }");
        }
        return sb.toString();
    }

    private String dataTypeToJava(final DataType dataType, final boolean blobFile) {
        return switch (dataType) {
            case BOOLEAN -> "Boolean";
            case STRING -> "String";
            case SHORT -> "Short";
            case INTEGER -> "Integer";
            case LONG -> "Long";
            case BIG_DECIMAL -> "BigDecimal";
            case DATE -> "LocalDate";
            case TIME -> "LocalTime";
            case TIME_STAMP -> "LocalDateTime";
            case BLOB -> blobFile ? "File" : "byte[]";
        };
    }

    private String toCamelCase(final boolean firstLower, final String string) {
        final StringBuilder sb = new StringBuilder();

        final String[] parts = string.toLowerCase().split("_");

        boolean first = true;

        for (final String part : parts) {
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

    private String linesToString(final String... lines) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < lines.length; index++) {
            final String line = lines[index];
            if (line.length() > 0) {
                sb.append(line);
            }
            if (index < lines.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void write(final File file, final byte[] data) {
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(file, false))) {
            os.write(data, 0, data.length);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}

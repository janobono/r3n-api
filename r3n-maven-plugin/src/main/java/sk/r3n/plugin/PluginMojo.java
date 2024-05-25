/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Plugin mojo.
 *
 * @author janobono
 * @since 21 August 2014
 */
@Mojo(name = "r3n-gen")
public class PluginMojo extends AbstractMojo {

    final static Logger log = LoggerFactory.getLogger(PluginMojo.class);

    @Parameter(defaultValue = "true", required = true)
    private boolean blobFile;

    @Parameter(defaultValue = "true", required = true)
    private boolean overwrite;

    @Parameter(defaultValue = "${basedir}/src/main/java", required = true)
    private File baseDir;

    @Parameter
    private String targetPackage;

    @Parameter
    private String jdbcDriver;

    @Parameter
    private String jdbcUrl;

    @Parameter
    private String jdbcUser;

    @Parameter
    private String jdbcPassword;

    @Override
    public void execute() {
        baseDir = new File(baseDir, "/" + targetPackage.replaceAll("\\.", "/"));
        baseDir.mkdirs();

        log.info(baseDir.getAbsolutePath());
        log.info(targetPackage);

        try {
            Class.forName(jdbcDriver);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (final Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)) {
            final Structure structure = StructureLoader.getInstance(jdbcDriver).load(connection);
            new StructureWriter(
                    readTemplate("/MetaSequence.txt"),
                    readTemplate("/MetaTable.txt"),
                    readTemplate("/MetaColumn.txt"),
                    readTemplate("/Dto.txt")
            ).write(blobFile, overwrite, baseDir, targetPackage, structure);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readTemplate(final String templateName) {
        try (
                final InputStream is = this.getClass().getResourceAsStream(templateName);
                final ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            assert is != null;
            is.transferTo(os);
            return os.toString();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}

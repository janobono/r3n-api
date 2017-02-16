/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import sk.r3n.jdbc.SqlUtil;

@Mojo(name = "r3n-gen")
public class PluginMojo extends AbstractMojo {

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

        getLog().info(baseDir.getAbsolutePath());
        getLog().info(targetPackage);

        Connection connection = null;
        try {
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            Structure structure = StructureLoader.getInstance(jdbcDriver).load(getLog(), connection, jdbcUser);
            new StructureWriter().write(getLog(), overwrite, baseDir, targetPackage, structure);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

}

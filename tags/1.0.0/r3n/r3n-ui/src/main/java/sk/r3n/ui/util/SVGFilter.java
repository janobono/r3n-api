package sk.r3n.ui.util;

import java.awt.Dimension;
import java.io.File;
import sk.r3n.ui.UIService;
import sk.r3n.ui.impl.UIServiceImpl;

public class SVGFilter {
    
    public static void main(String[] args) {
        UIServiceImpl uIServiceImpl = new UIServiceImpl();
        File f = uIServiceImpl.openFile(UIService.FILE_DIRECTORIES_ONLY, null, null, null, null);
        if (f != null) {
            new SVGFilter().filter(f);
        }
    }
    
    public void filter(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                filter(f);
            }
        } else {
            if (file.getName().toLowerCase().endsWith(".svg")) {
                System.out.println(file);
                try {
                    new SVGIcon(file.toURI().toURL(), new Dimension(20, 20));
                } catch (Exception e) {
                    if (file.delete()) {
                        System.out.println("Deleted " + file);
                    }
                }
            }
        }
    }
}

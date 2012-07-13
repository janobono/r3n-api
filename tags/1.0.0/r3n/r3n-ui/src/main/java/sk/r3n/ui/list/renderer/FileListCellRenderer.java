package sk.r3n.ui.list.renderer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

public class FileListCellRenderer extends LabelListCellRenderer<File> {

	private static final long serialVersionUID = 8292294633519733449L;

	protected File mainDir;

	protected List<File> roots;

	protected FileSystemView fileSystemView;

	public FileListCellRenderer() {
		super();
		File[] rootsArray = File.listRoots();
		roots = Arrays.asList(rootsArray);
		fileSystemView = FileSystemView.getFileSystemView();
	}

	public File getMainDir() {
		return mainDir;
	}

	public List<File> getRoots() {
		return roots;
	}

	public void setMainDir(File mainDir) {
		this.mainDir = mainDir;
	}

	@Override
	public String getText(File value) {
		setIcon(null);
		// ak je to hlavny adresar
		if (value.equals(mainDir)) {
			return ".." + File.separatorChar;
		}
		setIcon(fileSystemView.getSystemIcon(value));
		// ak je root
		if (roots.contains(value)) {
			return value.getPath();
		}
		// ak je to obsah hlavneho adresara
		return fileSystemView.getSystemDisplayName(value);
	}

}
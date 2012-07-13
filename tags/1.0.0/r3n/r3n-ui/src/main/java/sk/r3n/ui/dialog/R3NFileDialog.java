package sk.r3n.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sk.r3n.action.IdAction;
import sk.r3n.ui.AllFileFilter;
import sk.r3n.ui.R3NFileFilter;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NButton;
import sk.r3n.ui.component.R3NComboBox;
import sk.r3n.ui.component.field.ValueField;
import sk.r3n.ui.component.field.VarcharField;
import sk.r3n.ui.list.R3NList;
import sk.r3n.ui.list.renderer.FileFilterListCellRenderer;
import sk.r3n.ui.list.renderer.FileListCellRenderer;
import sk.r3n.ui.util.UIServiceManager;

public class R3NFileDialog extends R3NOkCancelDialog implements MouseListener,
		ListSelectionListener, DocumentListener {

	private static final long serialVersionUID = 6888855371356971455L;

	private static final Logger LOGGER = Logger.getLogger(R3NFileDialog.class
			.getCanonicalName());

	public static final int OPEN = 10;
	public static final int SAVE = 20;

	protected ValueField<String> dirField;
	protected R3NList<File> fileList;
	protected FileListCellRenderer fileRenderer;
	protected R3NComboBox<R3NFileFilter> filterBox;
	protected VarcharField nameField;
	protected JButton newDirButton;

	protected int type;
	protected int filter;
	protected boolean hidden;

	public R3NFileDialog(Frame frame, int type, int filter, String title,
			File mainDir, R3NFileFilter[] filters) {
		super(frame);
		init(type, filter, title, mainDir, filters);
	}

	@Override
	public void execute(String groupId, int actionId, Object source) {
		super.execute(groupId, actionId, source);
		if (groupId.equals(UIService.class.getCanonicalName())) {
			switch (actionId) {
			case UIService.ACTION_SELECT:
				if (fileList.getSelectedValue() != null) {
					if (fileList.getSelectedValue().isDirectory()) {
						if (fileList.getSelectedValue().equals(
								fileRenderer.getMainDir())) {
							if (fileRenderer.getMainDir().getParentFile() != null)
								fileRenderer.setMainDir(fileRenderer
										.getMainDir().getParentFile());
							else {
								if (fileRenderer.getRoots().contains(
										fileRenderer.getMainDir()))
									fileRenderer.setMainDir(null);
								else
									fileRenderer.setMainDir(new File(System
											.getProperty("user.dir"))
											.getParentFile());
							}
						} else {
							fileRenderer
									.setMainDir(fileList.getSelectedValue());
						}
						refreshContent();
					} else {
						if (source == fileList) {
							setFileName(fileList.getSelectedValue().getName());
						}
						execute(UIService.class.getCanonicalName(),
								UIService.ACTION_OK, source);
						return;
					}
				}
				refreshUI();
				return;
			case UIService.ACTION_EDIT:
				refreshContent();
				refreshUI();
				return;
			case UIService.ACTION_DIR_NEW:
				ResourceBundle bundle = ResourceBundle.getBundle(this
						.getClass().getCanonicalName());
				String dirName = JOptionPane.showInputDialog(this,
						bundle.getString("DIR_NAME"),
						bundle.getString("NEW_DIR"),
						JOptionPane.INFORMATION_MESSAGE);
				if (dirName != null && dirName.length() > 0) {
					File dir = null;
					if (fileRenderer.getMainDir() != null) {
						dir = fileRenderer.getMainDir();
					} else if (fileRenderer.getRoots().contains(
							fileList.getSelectedValue())) {
						dir = fileList.getSelectedValue();
					}
					if (dir != null) {
						try {
							new File(dir.getAbsolutePath() + File.separatorChar
									+ dirName).mkdir();
							refreshContent();
						} catch (Exception e) {
							LOGGER.log(Level.WARNING, "", e);
						}
					}
				}
				refreshUI();
				return;
			}
		}
	}

	/**
	 * @return Zvolený súbor.
	 */
	public File getFile() {
		if (fileList.getSelectedValue() == null)
			return null;
		if (type == OPEN || nameField.getText().equals(""))
			return fileList.getSelectedValue();

		File dir = fileRenderer.getMainDir();
		if (dir == null)
			return null;

		R3NFileFilter vuiFileFilter = (R3NFileFilter) filterBox
				.getSelectedItem();
		return new File(dir.getAbsolutePath() + File.separatorChar
				+ vuiFileFilter.getName(nameField.getText()));
	}

	public void changedUpdate(DocumentEvent e) {
		refreshUI();
	}

	private void init(int type, int filter, String title, File mainDir,
			R3NFileFilter[] filters) {
		// Inicializacia dialogu
		setModal(true);
		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
				.getCanonicalName());
		this.type = type;
		this.filter = filter;
		if (title != null)
			setTitle(title);
		else {
			if (type == OPEN)
				setTitle(bundle.getString("TITLE_OPEN"));
			else
				setTitle(bundle.getString("TITLE_SAVE"));
		}
		// Formular
		JPanel form = new JPanel(new GridBagLayout());

		// Adresar
		dirField = new ValueField<String>();
		form.add(dirField, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 2, 10), 0, 0));

		// List suborov
		fileList = new R3NList<File>();
		fileList.addMouseListener(this);
		fileList.addListSelectionListener(this);
		fileRenderer = new FileListCellRenderer();
		if (mainDir != null) {
			if (!mainDir.exists())
				mainDir = null;
		}
		fileRenderer.setMainDir(mainDir);
		fileList.setCellRenderer(fileRenderer);
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_SELECT,
				JComponent.WHEN_FOCUSED, fileList, this);
		form.add(new JScrollPane(fileList), new GridBagConstraints(0, 1, 3, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 10, 2, 10), 0, 0));

		if (type == SAVE) {
			// Nazov suboru
			nameField = new VarcharField(true);
			nameField.setColumns(20);
			nameField.getDocument().addDocumentListener(this);
			form.add(new JLabel(bundle.getString("NAME"), JLabel.RIGHT),
					new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(2, 10, 2, 5), 0, 0));
			form.add(nameField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(2, 0, 2, 0), 0, 0));

			newDirButton = new R3NButton(UIService.class.getCanonicalName(),
					UIService.ACTION_DIR_NEW);
			newDirButton.addActionListener(new IdAction(UIService.class
					.getCanonicalName(), UIService.ACTION_DIR_NEW, this));
			form.add(newDirButton, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(2, 0, 2, 10), 0, 0));
		}

		// Typ suboru
		filterBox = new R3NComboBox<R3NFileFilter>();
		filterBox.setRenderer(new FileFilterListCellRenderer());
		if (filters == null) {
			filters = new R3NFileFilter[] { new AllFileFilter() };
		}
		for (int i = 0; i < filters.length; i++) {
			filterBox.addItem(filters[i]);
		}
		filterBox.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_EDIT, this));
		form.add(new JLabel(bundle.getString("FILTER"), JLabel.RIGHT),
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(2, 10, 10, 5), 0, 0));
		form.add(filterBox, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						2, 0, 10, 10), 0, 0));

		add(form, BorderLayout.CENTER);
		refreshUI();
	}

	public boolean initDialog(String fileName) {
		refreshContent();
		pack();
		refreshUI();
		setFileName(fileName);
		setVisible(true);
		return lastGroup.equals(UIService.class.getCanonicalName())
				&& lastAction == UIService.ACTION_OK;
	}

	public void insertUpdate(DocumentEvent e) {
		refreshUI();
	}

	@Override
	public boolean isInputValid() {
		if (isSelectionOk())
			return true;
		UIServiceManager.getDefaultUIService().getBuzzer().buzz(this);
		return false;
	}

	protected boolean isSelectionOk() {
		File mainDir = fileRenderer.getMainDir();
		File selectedFile = fileList.getSelectedValue();
		R3NFileFilter selectedFilter = (R3NFileFilter) filterBox
				.getSelectedItem();
		if (selectedFile != null) {
			switch (type) {
			case OPEN:
				if (selectedFile.canRead()) {
					switch (filter) {
					case UIService.FILE_FILES_ONLY:
						if (selectedFile.isFile())
							return true;
						break;
					case UIService.FILE_DIRECTORIES_ONLY:
						if (fileList.getSelectedValue().isDirectory())
							return true;
						break;
					default:
						return true;
					}
				}
				break;
			case SAVE:
				if (mainDir != null) {
					if (!nameField.isContentNull()) {
						String newFileName = nameField.getValue();
						if (newFileName.indexOf('.') == -1
								&& selectedFilter.getExtension() != null)
							newFileName += selectedFilter.getExtension();
						selectedFile = new File(mainDir.getAbsolutePath()
								+ File.separatorChar + newFileName);
					}
					if (selectedFilter.accept(selectedFile)) {
						if (selectedFile.exists()) {
							if (selectedFile.canWrite()) {
								switch (filter) {
								case UIService.FILE_FILES_ONLY:
									if (selectedFile.isFile())
										return true;
									break;
								case UIService.FILE_DIRECTORIES_ONLY:
									if (selectedFile.isDirectory()
											|| fileRenderer.getRoots()
													.contains(selectedFile))
										return true;
									break;
								default:
									return true;
								}
							}
						} else {
							if (mainDir.canWrite()) {
								return true;
							}
						}
					}
				}
				break;
			}
		}
		return false;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			execute(UIService.class.getCanonicalName(),
					UIService.ACTION_SELECT, e.getSource());
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	protected void refreshContent() {
		fileList.setValues(null);
		try {
			if (fileRenderer.getMainDir() != null) {
				File[] content = fileRenderer.getMainDir().listFiles(
						(R3NFileFilter) filterBox.getSelectedItem());
				List<File> dirs = new ArrayList<File>();
				List<File> files = new ArrayList<File>();
				for (int i = 0; i < content.length; i++) {
					File file = content[i];
					if (!file.isHidden() || (hidden && file.isHidden())) {
						if (file.isDirectory()) {
							dirs.add(file);
						} else {
							files.add(file);
						}
					}
				}
				Collections.sort(dirs);
				Collections.sort(files);
				dirs.addAll(files);
				dirs.add(0, fileRenderer.getMainDir());
				fileList.setValues(dirs);
			} else {
				fileList.setValues(fileRenderer.getRoots());
			}
			if (fileList.getValues().size() > 0) {
				fileList.setSelectedValue(fileList.getValues().get(0));
				valueChanged(null);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "", e);
		}
	}

	public void refreshUI() {
		okButton.setEnabled(isSelectionOk());
		if (newDirButton != null) {
			File dir = fileRenderer.getMainDir();
			if (dir != null) {
				newDirButton.setEnabled(dir.canWrite());
			} else
				newDirButton.setEnabled(false);
		}
		if (fileRenderer.getMainDir() != null) {
			try {
				dirField.setText(fileRenderer.getMainDir().getCanonicalPath());
			} catch (Exception e) {
			}
		} else {
			dirField.setText("");
		}
	}

	public void removeUpdate(DocumentEvent e) {
		refreshUI();
	}

	/**
	 * Nastavenie obsahu poľa zadávania názvu súboru.
	 * 
	 * @param name
	 *            Názov súboru
	 */
	public void setFileName(String name) {
		if (nameField != null) {
			if (name != null)
				nameField.setText(name);
			else
				nameField.setText("");
			refreshUI();
		}
	}

	/**
	 * Nastavenie príznaku zobrazovania skrytých súborov.
	 * 
	 * @param hidden
	 *            Príznak zobrazovania skrytých súborov.
	 */
	public void showHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void valueChanged(ListSelectionEvent e) {
		if (nameField != null && isSelectionOk()) {
			nameField.setText(fileList.getSelectedValue().getName());
			return;
		}
		refreshUI();
	}

}

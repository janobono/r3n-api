package sk.r3n.ui.impl;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.transcoder.TranscoderInput;
import org.osgi.service.component.ComponentContext;

import sk.r3n.action.IdAction;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.action.IdActionService;
import sk.r3n.ui.Buzzer;
import sk.r3n.ui.R3NFileFilter;
import sk.r3n.ui.R3NInputComponent;
import sk.r3n.ui.UIService;
import sk.r3n.ui.dialog.R3NOkDialog;
import sk.r3n.ui.dialog.R3NPasswordDialog;
import sk.r3n.ui.dialog.R3NStatusDialog;
import sk.r3n.ui.dialog.R3NYesNoCancelDialog;
import sk.r3n.ui.dialog.R3NYesNoDialog;
import sk.r3n.ui.panel.MessagePanel;
import sk.r3n.ui.util.BufferedImageTranscoder;
import sk.r3n.ui.util.SVGIcon;
import sk.r3n.ui.util.UIServiceManager;

public class UIServiceImpl implements UIService {

	protected IdActionService idActionService;

	protected Buzzer buzzer;

	protected float coefficient;

	protected Dimension defaultDimension;

	protected long statusId;

	protected Map<Long, R3NStatusDialog> statusDialogs;

	protected Map<String, Dimension> dimension;

	protected Map<String, Boolean> recount;

	protected Dimension max;

	protected Frame frame;

	public UIServiceImpl() {
		super();
		coefficient = 1.0f;
		defaultDimension = new Dimension(18, 18);
		statusId = 0;
		statusDialogs = new HashMap<Long, R3NStatusDialog>();

		dimension = new HashMap<String, Dimension>();
		recount = new HashMap<String, Boolean>();
		setMaxDimension(null);

		setRootFrame(null);

		UIServiceManager.setDefaultUIService(this);
	}

	protected void activate(ComponentContext context) {
		this.idActionService = (IdActionService) context
				.locateService("IdActionService");

		idActionService.add(UIService.class.getCanonicalName(), ACTION_CLOSE);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_RESTART);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_REFRESH);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_OK);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_CANCEL);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_YES);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_NO);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_UP);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_DOWN);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_LEFT);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_RIGHT);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_MOVE_UP);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_MOVE_DOWN);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_MOVE_LEFT);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_MOVE_RIGHT);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_FIRST);
		idActionService
				.add(UIService.class.getCanonicalName(), ACTION_PREVIOUS);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_NEXT);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_LAST);

		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_PREVIOUS_ROWS);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_NEXT_ROWS);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_BUTTON);

		idActionService.add(UIService.class.getCanonicalName(), FOCUS_FORWARD);
		idActionService.add(UIService.class.getCanonicalName(), FOCUS_BACKWARD);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_ADD);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_COPY);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_EDIT);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_REMOVE);

		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_ADD_TO_LIST);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_EDIT_ON_LIST);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_REMOVE_FROM_LIST);

		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_CELL_EDIT);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_CELL_OK);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_CELL_CANCEL);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_INFO);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_WARNING);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_ERROR);
		idActionService
				.add(UIService.class.getCanonicalName(), ACTION_QUESTION);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_SELECT);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_PREVIEW);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_PRINT);

		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_PROPERTIES);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_SEARCH);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_SWITCH_SEARCH_KEY);

		idActionService
				.add(UIService.class.getCanonicalName(), ACTION_FILE_NEW);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_FILE_OPEN);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_FILE_SAVE);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_FILE_SAVE_AS);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_FILE_DELETE);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_DIR_NEW);
		idActionService
				.add(UIService.class.getCanonicalName(), ACTION_DIR_OPEN);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_DIR_DELETE);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_DEFAULT);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_ENABLE);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_DISABLE);

		idActionService.add(UIService.class.getCanonicalName(), ACTION_ABOUT);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_HELP);
		idActionService.add(UIService.class.getCanonicalName(), ACTION_LICENSE);
		idActionService.add(UIService.class.getCanonicalName(),
				ACTION_LOG_PREVIEW);

		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle("sk.r3n.ui.impl.JFileChooser");
		} catch (Exception e) {
		}
		if (bundle != null) {
			UIManager.put("FileChooser.lookInLabelText",
					bundle.getString("lookInLabelText"));
			UIManager.put("FileChooser.saveInLabelText",
					bundle.getString("saveInLabelText"));
			UIManager.put("FileChooser.filesOfTypeLabelText",
					bundle.getString("filesOfTypeLabelText"));
			UIManager.put("FileChooser.upFolderToolTipText",
					bundle.getString("upFolderToolTipText"));
			UIManager.put("FileChooser.fileNameLabelText",
					bundle.getString("fileNameLabelText"));
			UIManager.put("FileChooser.homeFolderToolTipText",
					bundle.getString("homeFolderToolTipText"));
			UIManager.put("FileChooser.newFolderToolTipText",
					bundle.getString("newFolderToolTipText"));
			UIManager.put("FileChooser.listViewButtonToolTipTextlist",
					bundle.getString("listViewButtonToolTipTextlist"));
			UIManager.put("FileChooser.detailsViewButtonToolTipText",
					bundle.getString("detailsViewButtonToolTipText"));
			UIManager.put("FileChooser.openButtonText",
					bundle.getString("openButtonText"));
			UIManager.put("FileChooser.saveButtonText",
					bundle.getString("saveButtonText"));
			UIManager.put("FileChooser.cancelButtonText",
					bundle.getString("cancelButtonText"));
			UIManager.put("FileChooser.updateButtonText",
					bundle.getString("updateButtonText"));
			UIManager.put("FileChooser.helpButtonText",
					bundle.getString("helpButtonText"));
			UIManager.put("FileChooser.saveButtonToolTipText",
					bundle.getString("saveButtonToolTipText"));
			UIManager.put("FileChooser.openButtonToolTipText",
					bundle.getString("openButtonToolTipText"));
			UIManager.put("FileChooser.cancelButtonToolTipText",
					bundle.getString("cancelButtonToolTipText"));
			UIManager.put("FileChooser.updateButtonToolTipText",
					bundle.getString("updateButtonToolTipText"));
			UIManager.put("FileChooser.helpButtonToolTipText",
					bundle.getString("helpButtonToolTipText"));
		}
	}

	protected void deactivate(ComponentContext context) {
		idActionService
				.remove(UIService.class.getCanonicalName(), ACTION_CLOSE);

		idActionService.remove(UIService.class.getCanonicalName(), ACTION_OK);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_CANCEL);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_YES);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_NO);

		idActionService.remove(UIService.class.getCanonicalName(), ACTION_UP);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_DOWN);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_LEFT);
		idActionService
				.remove(UIService.class.getCanonicalName(), ACTION_RIGHT);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_MOVE_UP);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_MOVE_DOWN);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_MOVE_LEFT);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_MOVE_RIGHT);

		idActionService
				.remove(UIService.class.getCanonicalName(), ACTION_FIRST);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_PREVIOUS);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_NEXT);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_LAST);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_PREVIOUS_ROWS);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_NEXT_ROWS);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_BUTTON);

		idActionService.remove(UIService.class.getCanonicalName(),
				FOCUS_FORWARD);
		idActionService.remove(UIService.class.getCanonicalName(),
				FOCUS_BACKWARD);

		idActionService.remove(UIService.class.getCanonicalName(), ACTION_ADD);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_COPY);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_EDIT);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_REMOVE);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_ADD_TO_LIST);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_EDIT_ON_LIST);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_REMOVE_FROM_LIST);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_CELL_EDIT);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_CELL_OK);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_CELL_CANCEL);

		idActionService.remove(UIService.class.getCanonicalName(), ACTION_INFO);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_WARNING);
		idActionService
				.remove(UIService.class.getCanonicalName(), ACTION_ERROR);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_QUESTION);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_SELECT);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_PREVIEW);
		idActionService
				.remove(UIService.class.getCanonicalName(), ACTION_PRINT);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_PROPERTIES);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_SEARCH);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_SWITCH_SEARCH_KEY);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_FILE_NEW);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_FILE_OPEN);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_FILE_SAVE);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_FILE_SAVE_AS);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_FILE_DELETE);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_DIR_NEW);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_DIR_OPEN);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_DIR_DELETE);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_DEFAULT);

		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_ENABLE);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_DISABLE);

		idActionService
				.remove(UIService.class.getCanonicalName(), ACTION_ABOUT);
		idActionService.remove(UIService.class.getCanonicalName(), ACTION_HELP);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_LICENSE);
		idActionService.remove(UIService.class.getCanonicalName(),
				ACTION_LOG_PREVIEW);
		this.idActionService = null;
	}

	public String getActionMapKey(String groupId, int actionId) {
		return groupId + actionId;
	}

	public Buzzer getBuzzer() {
		if (buzzer == null) {
			buzzer = new Buzzer() {

				public void buzz(Component source) {
					buzz(source,
							ResourceBundle.getBundle(
									UIServiceImpl.class.getCanonicalName())
									.getString("NO_VALID"));
				}

				public void buzz(Component source, String text) {
					Toolkit.getDefaultToolkit().beep();
					UIServiceImpl.this.showMessageDialog(null, text,
							MESSAGE_ACTION_WARNING);
				}
			};
		}
		return buzzer;
	}

	private Icon getDialogIcon(int messageType) {
		int size = (int) (32 * coefficient);
		switch (messageType) {
		case MESSAGE_ACTION_INFORMATION:
		case MESSAGE_ACTION_ERROR:
		case MESSAGE_ACTION_WARNING:
		case MESSAGE_ACTION_QUESTION:
			break;
		default:
			return null;
		}
		URL url = idActionService.getIcon(UIService.class.getCanonicalName(),
				messageType);
		return getIcon(url, new Dimension(size, size));
	}

	public Frame getFrameForComponent(Component component) {
		if (component == null)
			return getRootFrame();
		if (component instanceof Frame)
			return (Frame) component;
		return getFrameForComponent(component.getParent());
	}

	public Icon getIcon(URL url) {
		return getIcon(url, new Dimension(
				(int) (defaultDimension.width * coefficient),
				(int) (defaultDimension.height * coefficient)));
	}

	public Icon getIcon(URL url, Dimension dimension) {
		if (url != null) {
			if (url.getFile().toLowerCase().endsWith(".svg")) {
				try {
					return new SVGIcon(url, dimension);
				} catch (Exception e) {
				}
			} else {
				return new ImageIcon(url);
			}
		}
		return null;
	}

	public IdActionService getIdActionService() {
		return idActionService;
	}

	public Image getImage(URL url) {
		BufferedImage image = null;
		if (url != null) {
			if (url.getFile().toLowerCase().endsWith(".svg")) {
				try {
					BufferedImageTranscoder t = new BufferedImageTranscoder();
					t.transcode(new TranscoderInput(url.toURI().toString()), 0,
							0);
					image = t.getBufferedImage();
				} catch (Exception e) {
				}
			} else {
				try {
					image = ImageIO.read(url);
				} catch (Exception e) {
				}
			}
		}
		return image;
	}

	public byte[] getPassword(String title) {
		R3NPasswordDialog vuiPasswordDialog = new R3NPasswordDialog(
				getRootFrame());
		if (title != null)
			vuiPasswordDialog.setTitle(title);
		if (vuiPasswordDialog.initDialog()) {
			return vuiPasswordDialog.getPassword();
		}
		return null;
	}

	public Frame getRootFrame() {
		return frame;
	}

	public Window getWindowForComponent(Component component) {
		if (component == null)
			return getRootFrame();
		if (component instanceof Frame || component instanceof Dialog)
			return (Window) component;
		return getWindowForComponent(component.getParent());
	}

	public boolean isInputValid(List<R3NInputComponent<?>> inputComponents) {
		for (R3NInputComponent<?> baseInputComponent : inputComponents) {
			int contentValid = baseInputComponent.contentValid();
			if (contentValid != R3NInputComponent.VALID) {
				if (baseInputComponent instanceof JComponent) {
					Component component = (JComponent) baseInputComponent;
					if (component.isEnabled()) {
						if (component.isFocusable()) {
							component.requestFocus();
						}
						getBuzzer().buzz(
								component,
								ResourceBundle.getBundle(
										UIServiceImpl.class.getCanonicalName())
										.getString("NO_VALID"));
						return false;
					}
				}
			}
		}
		return true;
	}

	public void modifyDimensions(Window window) {
		if (window instanceof JFrame) {
			if (((JFrame) window).getExtendedState() == JFrame.MAXIMIZED_BOTH)
				return;
		}
		Dimension result = window.getSize();
		// Zadane rozmery
		Dimension dim = dimension.get(window.getClass().getCanonicalName());
		if (dim != null) {
			if (result.width < dim.width)
				result.width = dim.width;
			if (result.height < dim.height)
				result.height = dim.height;
		}
		// Rozmery z nadradeneho
		Window owner = window.getOwner();
		Boolean recount = this.recount
				.get(window.getClass().getCanonicalName());
		if (owner != null && recount != null && recount) {
			dim = owner.getSize();
			if (result.width < dim.width)
				result.width = dim.width - 20;
			if (result.height < dim.height)
				result.height = dim.height - 20;
		}
		// Koeficient velkosti textu
		if (!(UIManager.getLookAndFeel().getClass().getCanonicalName()
				.endsWith("NimbusLookAndFeel"))) {
			result.setSize(coefficient * result.width, coefficient
					* result.height);
		}
		// Porovnanie s maximom
		if (max.width < result.width)
			result.width = max.width - 20;
		if (max.height < result.height)
			result.height = max.height - 20;
		// Porovnanie s obrazovkou
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		if (scr.width < result.width)
			result.width = scr.width;
		if (scr.height < result.height)
			result.height = scr.height;
		window.setSize(result);
	}

	public void modifyFocus(Component component) {
		KeyStroke backward = idActionService.getKeyStroke(
				UIService.class.getCanonicalName(), FOCUS_BACKWARD);
		KeyStroke forward = idActionService.getKeyStroke(
				UIService.class.getCanonicalName(), FOCUS_FORWARD);
		if (backward != null && forward != null) {
			Set<AWTKeyStroke> keys = new HashSet<AWTKeyStroke>();
			keys.add(backward);
			component.setFocusTraversalKeys(
					KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keys);
			keys = new HashSet<AWTKeyStroke>();
			keys.add(forward);
			component.setFocusTraversalKeys(
					KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);
		}
	}

	public void modifyFontSize(float coefficient) {
		try {
			UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
			Enumeration<Object> enum1 = uidefs.keys();
			while (enum1.hasMoreElements()) {
				Object item = enum1.nextElement();
				Object value = uidefs.get(item);
				if (value instanceof Font) {
					Font font = (Font) value;
					font = font.deriveFont(font.getSize() * coefficient);
					UIManager.put(item, font);
				}
			}
			this.coefficient = coefficient;
		} catch (Exception e) {
		}
	}

	public File openFile(int filter, String title, File defaultDir,
			R3NFileFilter[] filters, String fileName) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setFileSelectionMode(filter);
		if (title != null) {
			fileChooser.setDialogTitle(title);
		} else {
			fileChooser.setDialogTitle(ResourceBundle.getBundle(
					UIServiceImpl.class.getCanonicalName()).getString("OPEN"));
		}
		if (defaultDir != null && defaultDir.exists()) {
			fileChooser.setCurrentDirectory(defaultDir);
		} else {
			fileChooser.setCurrentDirectory(new File(System
					.getProperty("user.dir")));
		}
		if (filters != null && filters.length > 0) {
			fileChooser.setAcceptAllFileFilterUsed(false);
			for (R3NFileFilter fl : filters) {
				fileChooser.addChoosableFileFilter(fl);
			}
		}
		if (fileName != null) {
			fileChooser.setSelectedFile(new File(fileName));
		}
		if (fileChooser.showOpenDialog(getRootFrame()) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	public void positionCenterScreen(Window window) {
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wSize = window.getSize();
		int maxWidth = sSize.width;
		int maxHeight = sSize.height;
		// fit on window
		if (wSize.height > maxHeight)
			wSize.height = maxHeight;
		if (wSize.width > maxWidth)
			wSize.width = maxWidth;
		window.setSize(wSize);
		if (sSize.width != wSize.width && sSize.height != wSize.height) {
			int x = (sSize.width - wSize.width) / 2;
			int y = (sSize.height - wSize.height) / 2;
			window.setLocation(x, y);
		} else {
			if (window instanceof JFrame) {
				JFrame frame = (JFrame) window;
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			} else
				window.setLocation(0, 0);
		}
		window.toFront();
	}

	public void positionCenterWindow(Window parent, Window window) {
		if (parent == null) {
			positionCenterScreen(window);
			return;
		} else {
			if (!parent.isVisible()) {
				positionCenterScreen(window);
				return;
			}
		}
		Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wSize = window.getSize();
		if (wSize.height > sSize.height)
			wSize.height = sSize.height;
		if (wSize.width > sSize.width)
			wSize.width = sSize.width;
		window.setSize(wSize);
		Rectangle pBounds = parent.getBounds();
		int x = pBounds.x + ((pBounds.width - wSize.width) / 2);
		if (x < 0)
			x = 0;
		int y = pBounds.y + ((pBounds.height - wSize.height) / 2);
		if (y < 0)
			y = 0;
		if (x + wSize.width > sSize.width)
			x = sSize.width - wSize.width;
		if (y + wSize.height > sSize.height)
			y = sSize.height - wSize.height;
		window.setLocation(x, y);
		window.toFront();
	}

	public File saveFile(int filter, String title, File defaultDir,
			R3NFileFilter[] filters, String fileName) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setFileSelectionMode(filter);
		if (title != null) {
			fileChooser.setDialogTitle(title);
		} else {
			fileChooser.setDialogTitle(ResourceBundle.getBundle(
					UIServiceImpl.class.getCanonicalName()).getString("SAVE"));
		}
		if (defaultDir != null && defaultDir.exists()) {
			fileChooser.setCurrentDirectory(defaultDir);
		} else {
			fileChooser.setCurrentDirectory(new File(System
					.getProperty("user.dir")));
		}
		if (filters != null && filters.length > 0) {
			fileChooser.setAcceptAllFileFilterUsed(false);
			for (R3NFileFilter fl : filters) {
				fileChooser.addChoosableFileFilter(fl);
			}
		}
		if (fileName != null) {
			fileChooser.setSelectedFile(new File(fileName));
		}
		if (fileChooser.showSaveDialog(getRootFrame()) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			FileFilter fileFilter = fileChooser.getFileFilter();
			if (fileFilter instanceof R3NFileFilter) {
				String ext = ((R3NFileFilter) fileFilter).getExtension();
				if (!file.getName().toLowerCase().endsWith(ext.toLowerCase()))
					file = new File(file.getPath() + ext);
			}
			return file;
		}
		return null;
	}

	@Override
	public void setBuzzer(Buzzer buzzer) {
		this.buzzer = buzzer;
	}

	public void setDimension(String key, Dimension dimension) {
		if (dimension == null)
			this.dimension.remove(key);
		else
			this.dimension.put(key, dimension);
	}

	public void setKeyStroke(String groupId, int actionId, InputMap map,
			Object actionKey) {
		if (actionId == FOCUS_FORWARD || actionId == FOCUS_BACKWARD) {
			return;
		}
		KeyStroke keyStroke = idActionService.getKeyStroke(groupId, actionId);
		if (keyStroke != null) {
			map.put(keyStroke, actionKey);
		}
	}

	public void setKeyStroke(String groupId, int actionId, int condition,
			JComponent component, Action action) {
		if (actionId == FOCUS_FORWARD || actionId == FOCUS_BACKWARD) {
			return;
		}
		KeyStroke keyStroke = idActionService.getKeyStroke(groupId, actionId);
		String actionMapKey = getActionMapKey(groupId, actionId);
		if (keyStroke != null) {
			component.getInputMap(condition).put(keyStroke, actionMapKey);
			component.getActionMap().put(actionMapKey, action);
		} else {
			component.getActionMap().remove(actionMapKey);
		}
	}

	public void setKeyStroke(String groupId, int actionId, int condition,
			JComponent component, IdActionExecutor idActionExecutor) {
		setKeyStroke(groupId, actionId, condition, component, new IdAction(
				groupId, actionId, idActionExecutor));
	}

	public void setMaxDimension(Dimension max) {
		if (max == null)
			max = Toolkit.getDefaultToolkit().getScreenSize();
		this.max = max;
	}

	public void setRecount(String key, Boolean recount) {
		if (recount == null)
			this.recount.remove(key);
		else
			this.recount.put(key, recount);
	}

	public void setRootFrame(Frame frame) {
		if (frame == null) {
			frame = new Frame();
			frame.setSize(max);
			positionCenterScreen(frame);
		}
		this.frame = frame;
	}

	public void showCenterScreen(Window window) {
		positionCenterScreen(window);
		window.setVisible(true);
	}

	public void showCenterWindow(Window parent, Window window) {
		positionCenterWindow(parent, window);
		window.setVisible(true);
	}

	public void showMessageDialog(String title, Object message, int messageType) {
		R3NOkDialog dialog = new R3NOkDialog(getRootFrame()) {

			private static final long serialVersionUID = 6772942023292876492L;
		};
		dialog.setModal(true);
		JPanel form = new JPanel(new GridBagLayout());
		if (title != null)
			dialog.setTitle(title);
		else
			dialog.setTitle(idActionService.getName(
					UIService.class.getCanonicalName(), messageType));
		JLabel icon = new JLabel();
		icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		icon.setVerticalAlignment(JLabel.TOP);
		icon.setIcon(getDialogIcon(messageType));
		form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 0), 0, 0));
		MessagePanel messagePanel = new MessagePanel();
		messagePanel.setMessage(message);
		form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 0, 5, 5), 0, 0));
		dialog.add(form, BorderLayout.CENTER);
		dialog.pack();
		dialog.setVisible(true);
	}

	public int showYesNoCancelDialog(String title, Object message,
			int messageType) {
		R3NYesNoCancelDialog dialog = new R3NYesNoCancelDialog(getRootFrame()) {

			private static final long serialVersionUID = 6509675348749804838L;
		};
		dialog.setModal(true);
		JPanel form = new JPanel(new GridBagLayout());
		if (title != null)
			dialog.setTitle(title);
		else
			dialog.setTitle(idActionService.getName(
					UIService.class.getCanonicalName(), messageType));
		JLabel icon = new JLabel();
		icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		icon.setVerticalAlignment(JLabel.TOP);
		icon.setIcon(getDialogIcon(messageType));
		form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 0), 0, 0));
		MessagePanel messagePanel = new MessagePanel();
		messagePanel.setMessage(message);
		form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 0, 5, 5), 0, 0));
		dialog.add(form, BorderLayout.CENTER);
		dialog.pack();
		dialog.setVisible(true);
		return dialog.getLastAction();
	}

	public int showYesNoDialog(String title, Object message, int messageType) {
		R3NYesNoDialog dialog = new R3NYesNoDialog(getRootFrame()) {

			private static final long serialVersionUID = 1107964695867871206L;
		};
		dialog.setModal(true);
		JPanel form = new JPanel(new GridBagLayout());
		if (title != null)
			dialog.setTitle(title);
		else
			dialog.setTitle(idActionService.getName(
					UIService.class.getCanonicalName(), messageType));
		JLabel icon = new JLabel();
		icon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		icon.setVerticalAlignment(JLabel.TOP);
		icon.setIcon(getDialogIcon(messageType));
		form.add(icon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 0), 0, 0));
		MessagePanel messagePanel = new MessagePanel();
		messagePanel.setMessage(message);
		form.add(messagePanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 0, 5, 5), 0, 0));
		dialog.add(form, BorderLayout.CENTER);
		dialog.pack();
		dialog.setVisible(true);
		return dialog.getLastAction();
	}

	public void statusAutoProgress(long id) {
		if (statusDialogs.containsKey(id)) {
			statusDialogs.get(id).autoProgress();
		}
	}

	public void statusFinishProgress(long id) {
		if (statusDialogs.containsKey(id)) {
			statusDialogs.get(id).finishProgress();
		}
	}

	public void statusHide(long id) {
		if (statusDialogs.containsKey(id)) {
			statusDialogs.get(id).statusHide();
		}
		statusDialogs.remove(id);
	}

	public void statusIncrementProgress(long id) {
		if (statusDialogs.containsKey(id)) {
			statusDialogs.get(id).incrementProgress();
		}
	}

	public void statusSetText(long id, String text) {
		if (statusDialogs.containsKey(id) && text != null) {
			statusDialogs.get(id).setText(text);
		}
	}

	public void statusSetTitle(long id, String title) {
		if (statusDialogs.containsKey(id) && title != null) {
			statusDialogs.get(id).setTitle(title);
		}
	}

	public long statusShow(String title) {
		R3NStatusDialog vuiStatusDialog = new R3NStatusDialog(getRootFrame());
		if (title != null)
			vuiStatusDialog.setTitle(title);
		statusId++;
		statusDialogs.put(statusId, vuiStatusDialog);
		vuiStatusDialog.statusShow();
		return statusId;
	}

	public void statusStartProgress(long id) {
		if (statusDialogs.containsKey(id)) {
			statusDialogs.get(id).startProgress();
		}
	}

}

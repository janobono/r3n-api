package sk.r3n.sw.test;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.frame.R3NFrame;
import sk.r3n.sw.frame.StartupFrame;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.LongTermJobListener;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionKey;

public class SWTestApp {

    protected class TestWorker extends SwingWorker<Void, Void> {

        private final LongTermJobListener jobListener;

        public TestWorker(LongTermJobListener jobListener) {
            this.jobListener = jobListener;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Thread.sleep(300);
            for (int i = 0; i < 40; i++) {
                jobListener.jobInProgress(SWTestBundle.STARTUP_TEST_P.value(new Object[]{i + 1}), 10);
                Thread.sleep(200);
            }
            ((StartupFrame) jobListener).jobFinished();
            new TestFrame();
            return Void.TYPE.newInstance();
        }
    }

    protected class TestFrame extends R3NFrame {

        public TestFrame() {
            super();
            setTitle(SWTestBundle.MAIN_TITLE.value());
            setIconImage(SwingUtil.getImage(SWTestApp.class.getResource("/sk/r3n/sw/test/tux.png")));
            setSize(800, 600);

            JTabbedPane tabbedPane = new JTabbedPane();

            UIConfigPanel configPanel = new UIConfigPanel();
            tabbedPane.addChangeListener(configPanel);
            tabbedPane.addTab(SWTestBundle.CONFIG_TAB.value(), configPanel);
            tabbedPane.addTab(SWTestBundle.FILE_OPEN_TAB.value(), new FileOpenPanel());
            tabbedPane.addTab(SWTestBundle.FILE_SAVE_TAB.value(), new FileSavePanel());
            tabbedPane.addTab(SWTestBundle.MESSAGE_TAB.value(), new MessagePanel());
            tabbedPane.addTab(SWTestBundle.QUESTION_TAB.value(), new QuestionPanel());
            tabbedPane.addTab(SWTestBundle.ACTION_TAB.value(), new R3NActionPanel());
            tabbedPane.addTab(SWTestBundle.TABLE_TAB.value(), new TablePanel());
            tabbedPane.addTab(SWTestBundle.TREE_TAB.value(), new TreePanel());
            tabbedPane.addTab(SWTestBundle.COMPONENT_TAB.value(), new ComponentPanel());

            add(tabbedPane, BorderLayout.CENTER);

            ButtonPanel buttonPanel = new ButtonPanel(2, true);
            buttonPanel.addButton(new R3NButton(R3NAction.REFRESH, this));
            buttonPanel.addButton(new R3NButton(R3NAction.CLOSE, this));
            add(buttonPanel, BorderLayout.SOUTH);

            SwingUtil.setRootFrame(this);
            setVisible(true);
        }

        @Override
        public void execute(UIActionKey actionKey, Object source) {
            if (actionKey instanceof R3NAction) {
                switch ((R3NAction) actionKey) {
                    case CLOSE:
                        dispose();
                        break;
                    case REFRESH:
                        dispose();
                        new TestFrame();
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        StartupFrame startupFrame = new StartupFrame();
        SwingUtil.setRootFrame(startupFrame);
        startupFrame.setSize(250, 300);
        startupFrame.setAppIcon(SWTestApp.class.getResource("/sk/r3n/sw/test/tux.png"));
        startupFrame.setAppImage(SWTestApp.class.getResource("/sk/r3n/sw/test/tux.svg"), true);
        startupFrame.setInfoTextForegroun(Color.ORANGE);
        TestWorker testWorker = (new SWTestApp()).new TestWorker(startupFrame);
        testWorker.execute();
        startupFrame.jobStarted(SWTestBundle.STARTUP_TEST.value());
    }
}

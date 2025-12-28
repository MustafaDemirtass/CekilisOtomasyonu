import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class RaffleApp extends JFrame {

    private DatabaseHelper dbHelper;
    private SoundManager soundManager;

    
    private DefaultListModel<Participant> listModel;
    private JList<Participant> participantList;
    private JTextField nameInput;
    private JLabel winnerLabel;

    public RaffleApp() {
        dbHelper = new DatabaseHelper();
        soundManager = new SoundManager();

        
        setTitle("MySQL Çekiliş Projesi");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        
        JTabbedPane tabbedPane = new JTabbedPane();

        
        JPanel addPanel = createAddPanel();
        tabbedPane.addTab("Kayıt Ekranı", addPanel);
        customizeTab(tabbedPane, 0, "KAYIT EKRANI");

        
        JPanel rafflePanel = createRafflePanel();
        tabbedPane.addTab("Çekiliş Ekranı", rafflePanel);
        customizeTab(tabbedPane, 1, "ÇEKİLİŞ EKRANI");

        add(tabbedPane);

        
        refreshList();
    }

    
    private JPanel createAddPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        nameInput = new JTextField(15); 
        JButton addButton = new JButton("Ekle");
        JButton deleteButton = new JButton("Seçileni Sil"); 
        JButton clearButton = new JButton("Hepsini Sil");

        
        addButton.setBackground(new Color(0, 255, 19));
        addButton.setForeground(Color.DARK_GRAY);

        deleteButton.setBackground(new Color(220, 53, 69)); 
        deleteButton.setForeground(Color.darkGray);

        inputPanel.add(new JLabel("Ad Soyad:"));
        inputPanel.add(nameInput);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton); 
        inputPanel.add(clearButton);

        listModel = new DefaultListModel<>();
        participantList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(participantList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Katılımcı Listesi"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        
        addButton.addActionListener(e -> {
            if (!nameInput.getText().trim().isEmpty()) {
                dbHelper.addParticipant(nameInput.getText().trim());
                nameInput.setText("");
                refreshList();
            }
        });

        deleteButton.addActionListener(e -> {
            Participant selected = participantList.getSelectedValue();

            if (selected != null) {
                
                int confirm = JOptionPane.showConfirmDialog(this,
                        selected.getName() + " kişisini silmek istiyor musun?",
                        "Silme Onayı", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    
                    dbHelper.deletParticipant(selected.getId());
                   
                    refreshList();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen listeden silinecek kişiyi seçin!");
            }
        });

        
        clearButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Tüm Liste silinecek, emin misin?") == 0) {
                dbHelper.clearParticipants();
                refreshList();
            }
        });

        return panel;
    }

    
    private JPanel createRafflePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JButton drawButton = new JButton("ÇEKİLİŞİ BAŞLAT");
        drawButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        drawButton.setBackground(new Color(255, 203, 219));
        drawButton.setForeground(Color.DARK_GRAY);
        drawButton.setFocusPainted(false);

        winnerLabel = new JLabel("Hazır...");
        winnerLabel.setFont(new Font("Segoe UI ", Font.BOLD, 30));
        winnerLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(drawButton, gbc);

        gbc.gridy = 1;
        panel.add(winnerLabel, gbc);

        drawButton.addActionListener(e -> startRaffleAnimation(drawButton));

        return panel;
    }

    private void startRaffleAnimation(JButton btn) {
        List<Participant> participants = dbHelper.getAllParticipants();
        if (participants.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Listede kimse yok!");
            return;
        }

        
        new Thread(() -> {
            btn.setEnabled(false);
            Random random = new Random();
            try {
                
                for (int i = 0; i < 30; i++) {
                    String tempName = participants.get(random.nextInt(participants.size())).getName();
                    SwingUtilities.invokeLater(() -> winnerLabel.setText(tempName));

                    
                    Thread.sleep(50 + (i * 5));
                }

                

                Participant winner = participants.get(random.nextInt(participants.size()));
                SwingUtilities.invokeLater(() -> {

                 
                    String kazananIsim = winner.getName().toUpperCase();

                    winnerLabel.setText("<html><nobr><font face='Segoe UI Emoji'>🎉</font> "
                            + kazananIsim +
                            " <font face='Segoe UI Emoji'>🎉</font></nobr></html>");

                    winnerLabel.setForeground(new Color(202, 0, 255));
                    soundManager.playApplause("Alkis.wav");
                    btn.setEnabled(true);
                });

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void refreshList() {
        listModel.clear();
        List<Participant> participants = dbHelper.getAllParticipants();
        for (Participant p : participants) {
            listModel.addElement(p);
        }
    }

    public static void main(String[] args) {
        try {
           
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new RaffleApp().setVisible(true));
    }
    
    private void customizeTab(JTabbedPane tabbedPane, int index, String title) {
        JLabel label = new JLabel(title, SwingConstants.CENTER); 
        label.setPreferredSize(new Dimension(375, 40)); 
        label.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        tabbedPane.setTabComponentAt(index, label); 
    }
}
package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuScreen extends JPanel implements KeyListener{
    private ArrayList<String> words = new ArrayList<>();
    private String wordTyped;
    private String randomWord;
    private JLabel titleLabel;
    private JLabel wordToType;

    public MenuScreen(){
        //panel stuff
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Stack components vertically
        setBounds(832, 0, 200, 860);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        //titleLabel stuff
        titleLabel = new JLabel("MENU");
        titleLabel.setBounds(840, 10, 180, 30);
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        //wordToType stuff
        getWord();
        wordToType = new JLabel(randomWord);
        wordToType.setBounds(840, 40, 50, 25);
        wordToType.setForeground(Color.BLACK);
        wordToType.setFont(new Font("Arial", Font.BOLD, 16));
        wordToType.setAlignmentX(CENTER_ALIGNMENT);
        
        //adding all to panel
        add(titleLabel);
        add(wordToType);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        switch(e.getKeyCode()){
            case KeyEvent.VK_BACK_SPACE:
                if(!wordTyped.isEmpty()){
                    wordTyped = wordTyped.substring(0, wordTyped.length()-1);
                }
                System.out.println(wordTyped);
                break;
            case KeyEvent.VK_ENTER:
                if(matchingWord()){
                    getWord();
                    wordToType.setText(randomWord);
                    wordTyped = "";
                }
                break;
            default:
                wordTyped += e.getKeyChar();
                System.out.println(wordTyped);
                break;
        }
    }

    public boolean matchingWord(){
        for(int i = 0; i < randomWord.length(); i++){
            if(!wordTyped.equals(randomWord)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void getWord(){
        try {
            Scanner input = new Scanner(new File("src/Main/words.txt"));
            while (input.hasNextLine()) {
                words.add(input.nextLine().trim());
            }
            input.close();
        } catch (Exception e) {
            System.out.println("src/Main/words.txt");
        }
        
        randomWord = randomWord();
        wordTyped = "";
        System.out.println(randomWord);
    }

    public String randomWord() {
        int index = (int) (Math.random() * words.size());
        return words.get(index);
    }

    public static void main(String[] args) {
        new MenuScreen();
    }
}

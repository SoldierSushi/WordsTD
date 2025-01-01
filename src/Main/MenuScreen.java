package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuScreen extends JPanel implements KeyListener{
    private ArrayList<String> words = new ArrayList<>();
    private String wordTyped;
    private String randomWord;

    public MenuScreen(){
        setBounds(832, 0, 200, 860);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        JLabel wordToType = new JLabel();
        getWord();
        wordToType.setBounds(882, 820, 50, 25);
        wordToType.setText(randomWord);
        
        add(wordToType);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int buttonPressed = e.getKeyCode();
        
        if(buttonPressed == 8 && wordTyped.length() > 0){
            wordTyped = wordTyped.substring(0, wordTyped.length()-1);
        }else{
            wordTyped += e.getKeyChar();
        }
        System.out.println(wordTyped);
        
        if(matchingWord()){
            getWord();
            wordTyped = "";
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

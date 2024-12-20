package Main;

import javax.swing.JFrame;

public class Game extends JFrame{
    public Game(){
        setSize(640,640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        GameScreen gameScreen = new GameScreen();
        add(gameScreen);
        setVisible(true);
        
    }

    public static void main(String[] args) {
        Game game = new Game();
        System.out.println("Hello World");
    }

}


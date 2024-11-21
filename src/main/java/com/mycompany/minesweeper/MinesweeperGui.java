/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;



/**
 *
 * @author hp
 */
public class MinesweeperGui {
    
    private class MineTile extends JButton {
        int row;
        int col;
        
        public MineTile(int row, int col){
            this.row = row;
            this.col = col;
        }
    }
    
     private void initializeTimer() {
        timer = new Timer(1000, new ActionListener() { // Fires every second
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsElapsed++;
                int minutes = secondsElapsed / 60;
                int seconds = secondsElapsed % 60;
                time.setText(String.format("Time: %d:%02d", minutes, seconds));
            }
        });
        timer.start(); // Start the timer
    }
    private Timer timer;
    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numCols*tileSize;
    int boardHeight = numRows*tileSize;
    int secondsElapsed = 0; // Tracks time in seconds
    
    
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JLabel time = new JLabel();
    
    JPanel textPanel = new JPanel();
    JPanel scorePanel = new JPanel();
    JPanel boardPanel = new JPanel();
    
    
    
    int mineCount = 10;
    
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();
    
    int tilesClicked = 0;
    boolean gameOver = false;
    
    
    MinesweeperGui(){
        initializeTimer();
        //frame.setVisible(true);
        
        
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);
        
        time.setFont(new Font("Arial", Font.BOLD, 25));
        time.setHorizontalAlignment(JLabel.LEFT);
        time.setText("Time :");
        time.setOpaque(true);
        
        // Panel for Title and Timer
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setLayout(new GridLayout(2, 1)); // 2 rows, 1 column
        textPanel.setLayout(new BorderLayout());
        scorePanel.setLayout(new BorderLayout());
        
        // Add labels to their respective panels
        textPanel.add(textLabel, BorderLayout.CENTER);
        scorePanel.add(time, BorderLayout.WEST);
        
        
        frame.add(textPanel, BorderLayout.NORTH);
        
        // Add both panels to the northPanel
        northPanel.add(textPanel);
        northPanel.add(scorePanel);
        
        boardPanel.setLayout(new GridLayout(numRows, numCols));
        //boardPanel.setBackground(Color.green);
        frame.add(northPanel, BorderLayout.NORTH);

        frame.add(boardPanel);
        
        for(int row=0; row<numRows ;row++){
            for(int col=0;col< numCols; col++){
                MineTile tile = new MineTile(row,col);
                board[row][col] = tile;
                
                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        if(gameOver){
                            return;
                        }
                        MineTile tile = (MineTile)e.getSource();
                        
                        //leftclick
                        if(e.getButton() == MouseEvent.BUTTON1){
                            if(tile.getText() == ""){
                                if(mineList.contains(tile)){
                                    reavealMines();
                                    
                                }
                                else{
                                    checkMine(tile.row, tile.col);
                                }
                            }
                            
                        }
                        else if(e.getButton() == MouseEvent.BUTTON3){
                            if(tile.getText()=="" && tile.isEnabled()){
                                tile.setText("🚩");
                            }
                            else if(tile.getText()=="🚩"){
                                tile.setText("");
                            }
                        }
                    }
                });
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                //tile.setText("💣");
                boardPanel.add(tile);
                
            }
        }
        setMines();
        
        frame.setVisible(true);
        
        
        
        
    }
    
    void setMines(){
        mineList = new ArrayList<MineTile>();
        /*mineList.add(board[5][6]);
        mineList.add(board[3][7]);
        mineList.add(board[2][2]);
        mineList.add(board[4][4]);
        mineList.add(board[1][5]);*/
        
        int mineLeft = mineCount;
        while(mineLeft > 0){
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if(!mineList.contains(tile)){
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
        
    }
    
    void reavealMines(){
        for(int i=0;i < mineList.size(); i++){
            MineTile tile = mineList.get(i);
            tile.setText("💣");
                 
        }
        gameOver = true;
        textLabel.setText("Game Over");
    }
    
    void checkMine(int r, int c){
        
        if( r<0 || r>= numRows || c<0 || c>=numCols){
            return ;
        }
        
        MineTile tile = board[r][c];
        if(!tile.isEnabled()){
            return;
        }
        
        tile.setEnabled(false);
        tilesClicked += 1;
        
        int minesFound = 0;
        
        //top 3
        minesFound += countMine(r-1,c-1);
        minesFound += countMine(r-1,c);
        minesFound += countMine(r-1,c+1);
        
        //left and right
        minesFound += countMine(r,c-1);
        minesFound += countMine(r,c+1);
        
        //bottom 3
        minesFound += countMine(r+1,c-1);
        minesFound += countMine(r+1,c);
        minesFound += countMine(r+1,c+1);
        
        if (minesFound >0){
            tile.setText(Integer.toString(minesFound));
        }
        else{
            tile.setText("");
            //top 3
            checkMine(r-1,c-1);
            checkMine(r-1,c);
            checkMine(r-1,c+1);
            
            checkMine(r,c-1);
            checkMine(r,c+1);
            
            checkMine(r+1,c-1);
            checkMine(r+1,c);
            checkMine(r+1,c+1);
            
            
        }
        if(tilesClicked == numRows * numCols - mineList.size()){
            gameOver = true;
            textLabel.setText("Mines Cleared !");
        }
        
    }
    
    int countMine(int r, int c){
        if( r<0 || r>= numRows || c<0 || c>=numCols){
            return 0;
        }
        if(mineList.contains(board[r][c])){
            return 1;
        }
        return 0;
       
    }

}

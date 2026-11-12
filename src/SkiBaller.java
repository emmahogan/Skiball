import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;
/**
 * This project is a desktop Skiball game created using java swing.
 *
 * It was originally written as a class project for a Siena College computer science course
 * by Emma Hogan Benser and Andrew Towse, in March 2020.
 *
 * In 2026, the current fork of this project was created to adapt this application for a
 * demonstration of Github merge conflicts for students at Bard College.
 * Hi my name is Andrew Benser and I am testing out if I can push to this branch.
 *
 * @author Emma Hogan Benser, Andrew Benser
 * @version November 8, 2026
 */
public class SkiBaller extends MouseAdapter implements Runnable, ActionListener
{
    /////////////////////////////////
    //CONSTANTS//
    //width of the playing lane
    static final int LANE_WIDTH = 400;
    //height of the playing lane
    static final int LANE_HEIGHT = 800;
    //width of side panel
    static final int SIDE_WIDTH = 200;
    //y coord of divider line
    static final int DIVIDER_LINE = 500;

    //SETS MAX STRENGTH OF THE SHOTS
    static final int MAX_STRENGTH = 165;

    //center of target
    static final Point CENTER = new Point(200,175);
    //center of 50pt circle at top of target
    static final Point CENTER_50 = new Point(200, 75);
    //radius of innermost concentric circle,
    //width of each ring around it and diameter
    //of 50pt circle
    static final int  RING_WIDTH = 50;

    //Size of ball
    static final int BALL_SIZE = 10;

    //The upper left corner of all of the targets
    static final Point UPLEFT_50 = new Point(CENTER.x - RING_WIDTH/2, RING_WIDTH);
    static final Point UPLEFT_10 = new Point(CENTER.x - 5*RING_WIDTH/2, CENTER.y - 5*RING_WIDTH/2);
    static final Point UPLEFT_20 = new Point(CENTER.x - 3*RING_WIDTH/2, CENTER.y - 3*RING_WIDTH/2);
    static final Point UPLEFT_30 = new Point(CENTER.x - RING_WIDTH/2, CENTER.y - RING_WIDTH/2);
    
    // Number of shots per game
    static final int GAME_SHOTS = 10;
    
    //////////////////////////////////////////////
    //INSTANCE VARIABLE

    //variables needed to make target random colors
    Color[] colorList = new Color[3];
    Random rand = new Random();

    //panel with lane
    JPanel lanePanel;

    //side panel as Box instead of JPanel
    Box sidePanel;

    
    //the main panel
    JPanel mainPanel;
    //SKI BALL! label at the top of the side panel
    JLabel gameLabel = new JLabel("Ski Baller!");

    //text about most recent shot on side panel
    JLabel statusLabel;

    //Points earned in most recent shot and label displaying
    //on side panel
    int shotPoints;
    JLabel pointsLabel;

    //number of shots left counting down from 10 and
    //label displaying this on side panel
    int shotsLeft;
    JLabel shotsLabel;

    //Total points & label displaed on side panel
    int pointsTotal = 0;
    JLabel totalLabel;

    //highest score gotten this play session
    int highScore = 0;
    JLabel highestLabel;

    //Restart Button and label
    JButton restartButton;
    JLabel buttonLabel = new JLabel("Start Game");

    //slingshot line represented by 2 points
    Point slingLineStart;
    Point slingLineEnd;
    //the ball's upper left
    Point ballLocation;

    //the strength of yellowness of the ball color
    int yellowStrength = 255;

    //if the ball is currently being dragged
    boolean dragging;
    boolean playing = false;

    //keep track of ball locations for all shots in a game, to review at the end of game
    Point[] stored_shots = new Point[GAME_SHOTS];

    Font bigFont = new Font("Helvetica", Font.BOLD, 35);
    /**
     * This method creates the JFrame window and add the different panels and labels to the window
     * 
     */
    public void run(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        //the window
        JFrame frame = new JFrame("SkiBaller");
        //sets the window to the proper size
        frame.setPreferredSize(new Dimension(LANE_WIDTH + SIDE_WIDTH, LANE_HEIGHT));
        //Makes it so the window cannot be moved
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ///////////////////////CREATES ALL THGE NECESSARY LABELS AND ALIGNS THEM IN THE CENTER
        gameLabel.setFont(bigFont);
        gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel = new JLabel("Press Start Game to Play");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pointsLabel = new JLabel("");
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        shotsLabel = new JLabel("");
        shotsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalLabel = new JLabel("");
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highestLabel = new JLabel("");
        highestLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //CREATES MAIN PANEL AND ADDS IT TO THE FRAME
        mainPanel = new JPanel();
        frame.add(mainPanel);
        //CREATES SIDE PANELS AS A BOX LAYOUT AND SETS ITS PREFERRED SIZE
        sidePanel = new Box(BoxLayout.Y_AXIS);
        sidePanel.setPreferredSize(new Dimension(200, 800));

        mainPanel.setLayout(new BorderLayout());

        //ADDS ALL OF THE LABELS TO SIDEPANEL
        sidePanel.add(gameLabel);
        sidePanel.add(statusLabel);
        sidePanel.add(pointsLabel);
        sidePanel.add(shotsLabel);
        sidePanel.add(totalLabel);
        sidePanel.add(highestLabel);

        //CREATES THE RESTART BUTTON FOR THE GAME ALIGNS IT IN THE CENTER
        //AND ADDS AN ACTION LISTENER AS WELL AS ADDING IT TO SIDE PANEL
        restartButton = new JButton();
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartButton.add(buttonLabel);
        restartButton.addActionListener(this);
        sidePanel.add(restartButton);

        //SETS STARTING SHOTS TO GAME_SHOTS
        shotsLeft = GAME_SHOTS;

        //Sets board to be all white before starting for first time
        colorList[0] = new Color(255, 255, 255);
        colorList[1] = new Color(255, 255, 255);
        colorList[2] = new Color(255, 255, 255);

        lanePanel = new JPanel() {
            /**
             * Method that paints all of the objects onto the window for the game
             * @param takes a graphics component to draw the images on
             */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                //DRAWS ALL THE LINES AND CIRCLES NEEDED ON THE WINDOW
                g.drawLine(0, DIVIDER_LINE, LANE_WIDTH, DIVIDER_LINE);
                //g.drawLine(LANE_WIDTH-1, 0, LANE_WIDTH-1, LANE_HEIGHT);
                g.setColor(colorList[0]);
                g.fillOval(UPLEFT_10.x, UPLEFT_10.y, 5*RING_WIDTH, 5*RING_WIDTH);
                g.setColor(colorList[1]);
                g.fillOval(UPLEFT_20.x, UPLEFT_20.y, 3*RING_WIDTH, 3*RING_WIDTH);
                g.setColor(colorList[2]);
                g.fillOval(UPLEFT_30.x, UPLEFT_30.y, RING_WIDTH, RING_WIDTH);
                g.setColor(new Color(255, 0, 0));
                g.fillOval(UPLEFT_50.x, UPLEFT_50.y, RING_WIDTH, RING_WIDTH);

                //OUTLINES ALL OF THE TARGET CIRCLES WITH BLACK LINES
                g.setColor(new Color(0,0,0));
                g.drawOval(UPLEFT_10.x, UPLEFT_10.y, 5*RING_WIDTH, 5*RING_WIDTH);
                g.drawOval(UPLEFT_20.x, UPLEFT_20.y, 3*RING_WIDTH, 3*RING_WIDTH);
                g.drawOval(UPLEFT_30.x, UPLEFT_30.y, RING_WIDTH, RING_WIDTH);
                g.drawOval(UPLEFT_50.x, UPLEFT_50.y, RING_WIDTH, RING_WIDTH);
                if(dragging){
                    //DRAWS THE BALL WHERE IT IS BEING DRAGGED
                    g.setColor(new Color(255, yellowStrength, 0));
                    g.drawLine(slingLineStart.x, slingLineStart.y, slingLineEnd.x, slingLineEnd.y);
                    g.fillOval(slingLineEnd.x - BALL_SIZE/2, slingLineEnd.y - BALL_SIZE/2, BALL_SIZE, BALL_SIZE);
                }
                else if(ballLocation != null){
                    //PRINTS THE BALL WHERE IT LANDS
                    g.setColor(new Color(0, 0, 0));
                    g.fillOval(ballLocation.x - BALL_SIZE/2, ballLocation.y - BALL_SIZE/2, BALL_SIZE, BALL_SIZE);
                }
            }
        };
        mainPanel.add(lanePanel, BorderLayout.CENTER);

        //CODE TO PUT BORDER AROUND PANEL FOUND AT: https://www.tutorialspoint.com/swingexamples/add_border_to_panel.htm
        Border blackline = BorderFactory.createLineBorder(Color.black);
        lanePanel.setBorder(blackline);
        //ADDS THE SIDE PANEL TO THE MAIN PANEL
        mainPanel.add(sidePanel, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method that is called when a button is pressed and changes playing to true and paints the targets
     * as well as adding the mouselisteners to begin playing
     * 
     * @param actionEvent
     */
    public void actionPerformed(ActionEvent e){
        //ONCE START GAME BUTTON IS PRESSED IT ADDS THE MOUSELISTENERS TO PLAY THE GAME
        if(!playing){
            lanePanel.addMouseListener(this);
            lanePanel.addMouseMotionListener(this);
            playing = true;
        }
        //SETS EVERYTHING TO DEFAULT STARTING AMOUNT AND ADDS COLORS TO THE TARGET
        pointsTotal = 0;
        shotsLeft = GAME_SHOTS;
        ballLocation = null;
        updateLabels();
        buttonLabel.setText("Restart Game");
        colorList[0] = new Color(rand.nextInt(156), rand.nextInt(256), rand.nextInt(256));
        colorList[1] = new Color(rand.nextInt(256), rand.nextInt(156), rand.nextInt(256));
        colorList[2] = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(156));
        mainPanel.repaint();
    }

    /**
     * Method that is called everytime the ball is shot that updates to labels to be
     * current with the game score, shotsleft, etc.
     */
    public void updateLabels(){
        shotsLabel.setText("Shots left: " + shotsLeft);
        totalLabel.setText("Total: " + pointsTotal);
        highestLabel.setText("High Score: " + highScore);
    }

    /**
     * Method called when the mouse button is pressed that draws the slingline and sets dragging to true
     * @param MouseEvent that is used to get the location of the mouse
     */
    public void mousePressed(MouseEvent e){
        // if(e.getY() > DIVIDER_LINE){
        slingLineStart = new Point(e.getX(), DIVIDER_LINE);
        slingLineEnd = new Point(e.getX(), e.getY());
        dragging = true;
        //}
        lanePanel.repaint();
    }

    /**
     * Method that on release of the mouse the ball is "shot" and calls shotcalculation and changes relevant variables
     * @param MouseEvent used to find the point the mouse is at
     */
    public void mouseReleased(MouseEvent e){
        dragging = false;
        shotsLeft--;
        shotCalculation(e.getPoint());
        lanePanel.repaint();
        if(pointsTotal > highScore)
        {
            highScore = pointsTotal;
        }
        updateLabels();
        if(shotsLeft == 0){
            gameOver();
        }
    }

    /**
     * Method that clears the labels when the game ends
     * 
     */
    public void gameOver(){
        statusLabel.setText("");
        pointsLabel.setText("");
        shotsLabel.setText("");
        buttonLabel.setText("Play Again");
        lanePanel.removeMouseListener(this);
        lanePanel.removeMouseMotionListener(this);
        playing = false;
    }

    /**
     * Method that makes the slingLine move with the dragging mouse and calls 
     * calculateRangePoint if it is being dragged past maxstrength
     * @param MouseEvent e this is used to find the point of the mouse
     */
    public void mouseDragged(MouseEvent e)
    {

        if(e.getPoint().distance(slingLineStart) <= MAX_STRENGTH){
            slingLineEnd = new Point(e.getX(), e.getY());

        }
        else{
            slingLineEnd = calculateRangePoint(e.getPoint());
        }
        yellowStrength = 255 - (int)((255/MAX_STRENGTH) * slingLineStart.distance(slingLineEnd));
        lanePanel.repaint();
    }

    /**
     * Method calculates the point to put the slingLineEnd at if the mouse is further than the maxStrength
     * @param Point that the mouse is at that is too far away
     * @return Point when the line should end
     */
    public Point calculateRangePoint(Point end){
        double angle = Math.acos((end.x - slingLineStart.x)/(end.distance(slingLineStart)));
        int maxX =  (int) (MAX_STRENGTH * Math.cos(angle));
        int maxY = (int) (MAX_STRENGTH * Math.sin(angle));
        if(end.getY() > DIVIDER_LINE){
        return new Point(slingLineStart.x + maxX, slingLineStart.y + maxY);
    }
    else{
        return new Point(slingLineStart.x + maxX, slingLineStart.y - maxY);
    }
    }

    /**
     * Method that calculates where the ball lands after a shot
     * @param the point at which the ball is released
     * 
     */
    public void shotCalculation(Point release){        
        int xVal;
        int yVal;
        xVal = slingLineEnd.x - (slingLineEnd.x - slingLineStart.x) * 4;
        yVal = slingLineEnd.y - (slingLineEnd.y - slingLineStart.y) * 4;
        Point p = new Point(xVal, yVal);
        ballLocation = p;
        stored_shots[shotsLeft] = p; // stores point where shot is placed in array of stored shots at index shotsLeft
        pointsEarned(ballLocation);
    }
    
    /**
     * Method that calculates how many points you earn depending on which circle the ball lands in using the contains method
     * @param point the ball lands at
     */
    public void pointsEarned(Point shot){
        if(contains(shot, CENTER_50, RING_WIDTH/2)){
            shotPoints = 50;
            pointsTotal += shotPoints;
            statusLabel.setText("AWESOME shot!!");
        }
        else if(contains(shot, CENTER, RING_WIDTH/2)){
            shotPoints = 30;
            pointsTotal += shotPoints;
            statusLabel.setText("Nice!");
        }
        else if(contains(shot, CENTER, 3*RING_WIDTH/2)){
            shotPoints = 20;
            pointsTotal += shotPoints;
            statusLabel.setText("Not too bad..");
        }
        else if(contains(shot, CENTER, 5*RING_WIDTH/2)){
            shotPoints = 10;
            pointsTotal += shotPoints;
            statusLabel.setText("Could be worse");
        }
        //else if((shot.x > LANE_WIDTH || shot.x < 0) && (shot.y > LANE_HEIGHT || shot.y < 0)){
        //    shotPoints = 0;
        //    statusLabel.setText("Wrong way pal...");
        //}
        else{
            shotPoints = 0;
            statusLabel.setText("Terrible shot.");
        }
        pointsLabel.setText("" + shotPoints + " Points");
        mainPanel.repaint();
    }

    /**
     * Method that returns a boolean of whether or not the ball lands in the circle it was called on
     * @param p the point that the ball landed
     * @param center the center of the circle
     * @param radius thre radius of the circle that is being tested
     */
    public boolean contains(Point p, Point center, int radius) {
        return center.distance(p) <= radius;
    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new SkiBaller());
    }
}

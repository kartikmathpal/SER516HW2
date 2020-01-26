
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class SER516HW2 extends Application {

    private Shape[] shapes = new Shape[500];  // Contains shapes the user has drawn.
    private int shapeCount = 0; // Number of shapes that the user has drawn.
    private Canvas canvas; // The drawing area where the user draws.
    private Color currentColor = Color.RED;  // Color to be used for new shapes.

    /**
     * A main routine that simply runs this application.
     */
    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage stage) {
        canvas = makeCanvas();
        paintCanvas();
        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setStyle("-fx-border-width: 2px; -fx-border-color: #444");
        BorderPane root = new BorderPane(canvasHolder);
        root.setStyle("-fx-border-width: 1px; -fx-border-color: black");
        root.setBottom(makeToolPanel(canvas));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Click buttons to add shapes; drag shapes with your mouse");
        stage.setResizable(false);
        stage.show();
    }

    private Canvas makeCanvas() {

        // The listeners are given by methods that are defined below.
        Canvas canvas = new Canvas(800,600);
        canvas.setOnMousePressed( this::mousePressed );
        canvas.setOnMouseReleased( this::mouseReleased );
        canvas.setOnMouseDragged( this::mouseDragged );
        canvas.setOnMouseClicked(this::mouseClicked);
        return canvas;
    }

    private HBox makeToolPanel(Canvas canvas) {

        Button ovalButton = new Button("Add an Oval");
        ovalButton.setOnAction( (e) -> addShape( new OvalShape() ) );
        Button rectButton = new Button("Add a Rect");
        rectButton.setOnAction( (e) -> addShape( new RectShape() ) );
        /*Button roundRectButton = new Button("Add a RoundRect");
        roundRectButton.setOnAction( (e) -> addShape( new RoundRectShape() ) );*/
        ComboBox<String> combobox = new ComboBox<>();
        combobox.setEditable(false);
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.CYAN,
                Color.MAGENTA, Color.YELLOW, Color.BLACK, Color.WHITE };
        String[] colorNames = { "Red", "Green", "Blue", "Cyan",
                "Magenta", "Yellow", "Black", "White" };
        combobox.getItems().addAll(colorNames);
        combobox.setValue("Red");
        combobox.setOnAction(
                e -> currentColor = colors[combobox.getSelectionModel().getSelectedIndex()] );
        HBox tools = new HBox(10);
        tools.getChildren().add(ovalButton);
        tools.getChildren().add(rectButton);
        //tools.getChildren().add(roundRectButton);
        tools.getChildren().add(combobox);
        tools.setStyle("-fx-border-width: 5px; -fx-border-color: transparent; -fx-background-color: lightgray");
        return tools;
    }

    private void paintCanvas() {

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE); // Fill with white background.
        g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        for (int i = 0; i < shapeCount; i++) {
            Shape s = shapes[i];
            s.draw(g);
        }
    }

    private void addShape(Shape shape) {

        shape.setColor(currentColor);
        shape.reshape(10,10,150,100);
        shapes[shapeCount] = shape;
        shapeCount++;
        paintCanvas();
    }




    private Shape shapeBeingDragged = null;

    private int prevDragX;
    private int prevDragY;

    private void mousePressed(MouseEvent evt) {


        int x = (int)evt.getX();
        int y = (int)evt.getY();
        for ( int i = shapeCount - 1; i >= 0; i-- ) {
            Shape s = shapes[i];
            if (s.containsPoint(x,y)) {
                shapeBeingDragged = s;
                prevDragX = x;
                prevDragY = y;
                if (evt.isShiftDown()) {
                    for (int j = i; j < shapeCount-1; j++) {

                        shapes[j] = shapes[j+1];
                    }
                    shapes[shapeCount-1] = s;
                    paintCanvas();
                }
                return;
            }
        }
    }

    private void mouseDragged(MouseEvent evt) {

        int x = (int)evt.getX();
        int y = (int)evt.getY();
        if (shapeBeingDragged != null) {
            shapeBeingDragged.moveBy(x - prevDragX, y - prevDragY);
            prevDragX = x;
            prevDragY = y;
            paintCanvas();
        }
    }

    private void mouseClicked(MouseEvent evt) {

        addShape( new OvalShape());
    }
    private void mouseReleased(MouseEvent evt) {

        shapeBeingDragged = null;
    }



    //*** Next Step : create a seperate abstract class and implement it
    static abstract class Shape {


        int left, top;      // Position of top left corner of rectangle that bounds this shape.
        int width, height;  // Size of the bounding rectangle.
        Color color = Color.WHITE;  // Color of this shape.

        void reshape(int left, int top, int width, int height) {
            // Set the position and size of this shape.
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        void moveBy(int dx, int dy) {
            // Move the shape by dx pixels horizontally and dy pixels vertically
            // (by changing the position of the top-left corner of the shape).
            left += dx;
            top += dy;
        }

        void setColor(Color color) {
            // Set the color of this shape
            this.color = color;
        }

        boolean containsPoint(int x, int y) {
            // Check whether the shape contains the point (x,y).
            // By default, this just checks whether (x,y) is inside the
            // rectangle that bounds the shape.  This method should be
            // overridden by a subclass if the default behavior is not
            // appropriate for the subclass.
            if (x >= left && x < left+width && y >= top && y < top+height)
                return true;
            else
                return false;
        }

        abstract void draw(GraphicsContext g);
        // Draw the shape in the graphics context g.
        // This must be overriden in any concrete subclass.

    }


    //create a serperate class for rect and extend from above abs class shape
    static class RectShape extends Shape {
        // This class represents rectangle shapes.
        void draw(GraphicsContext g) {
            g.setFill(color);
            g.fillRect(left,top,width,height);
            g.setStroke(Color.BLACK);
            g.strokeRect(left,top,width,height);
        }
    }

    //create a serperate class for oval and extend from above abs class shape
    static class OvalShape extends Shape {
        // This class represents oval shapes.
        void draw(GraphicsContext g) {
            g.setFill(color);
            g.fillOval(left,top,width,height);
            g.setStroke(Color.BLACK);
            g.strokeOval(left,top,width,height);
        }
        boolean containsPoint(int x, int y) {

            double rx = width/2.0;   // horizontal radius of ellipse
            double ry = height/2.0;  // vertical radius of ellipse
            double cx = left + rx;   // x-coord of center of ellipse
            double cy = top + ry;    // y-coord of center of ellipse
            if ( (ry*(x-cx))*(ry*(x-cx)) + (rx*(y-cy))*(rx*(y-cy)) <= rx*rx*ry*ry )
                return true;
            else
                return false;
        }
    }

/*
* Can implement other shapes as well*/
//    static class RoundRectShape extends Shape {
//        void draw(GraphicsContext g) {
//            g.setFill(color);
//            g.fillRoundRect(left,top,width,height,width/3,height/3);
//            g.setStroke(Color.BLACK);
//            g.strokeRoundRect(left,top,width,height,width/3,height/3);
//        }
//    }


}
/*******NOTE
*
* In the next push will try tomake the position of the shapes dynamic
* will be implementing a second panel having shapes in place of buttons.
******** */



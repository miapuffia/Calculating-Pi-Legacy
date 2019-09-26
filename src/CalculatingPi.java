import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class CalculatingPi extends Application {
	boolean gameLoopRunning = false;
	
	int numCollisions = 0;
	
	double square1PrecisePos = 600;
	double square1Velocity = -100;
	
	double square2PrecisePos = 100;
	double square2Velocity = 0;
	
	PreciseSquare square1 = new PreciseSquare(100, 100, square1PrecisePos, square1Velocity, 16);
	PreciseSquare square2 = new PreciseSquare(20, 20, square2PrecisePos, square2Velocity, 1);
	Label collisions = new Label("Collisions: 0");
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		Label squareLabel1 = new Label(square1.getMass() + "");
		Label squareLabel2 = new Label(square2.getMass() + "");
		
		TextField speedTextField = new TextField("15");
		speedTextField.setPrefColumnCount(3);
		
		speedTextField.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if(!newValue.matches("\\d*")) {
		        	speedTextField.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		        
		        if(newValue.length() > 3) {
		        	speedTextField.setText(speedTextField.getText().substring(0, 3));
		        }
		    }
		});
		
		AnimationTimer gameLoop = new AnimationTimer() {
			public void handle(long now) {
				if(speedTextField.getText().length() == 0) {
		        	speedTextField.setText("1");
		        }
				
				square1.setPrecisePos(square1.getPrecisePos() + (((double) Integer.parseInt(speedTextField.getText()) / 1000) * square1.getVelocity()));
				square2.setPrecisePos(square2.getPrecisePos() + (((double) Integer.parseInt(speedTextField.getText()) / 1000) * square2.getVelocity()));
				
				square1.setX(Math.round(square1.getPrecisePos()));
				square2.setX(Math.round(square2.getPrecisePos()));
				
				if(square1.getPrecisePos() <= (square2.getPrecisePos() + square2.getWidth())) {
					collide();
					
					if(square1.getVelocity() <= 0) {
						numCollisions++;
						
						collisions.setText("Collisions: " + numCollisions);
					}
				}
				
				if(square2.getPrecisePos() <= 10) {
					square2.setVelocity(square2.getVelocity() * -1);
				}
			}
		};
		
		int sceneWidth = 800;
		int sceneHeight = 500;
		
		Line verticalLine = new Line(10, 0, 10, sceneHeight);
		verticalLine.setStroke(Color.BLACK);
		verticalLine.setStrokeWidth(1);
		
		Line horizontalLine = new Line(0, sceneHeight - 10, sceneWidth, sceneHeight - 10);
		horizontalLine.setStroke(Color.BLACK);
		horizontalLine.setStrokeWidth(1);
		
		square1.setX((int) square1PrecisePos);
		square1.setY(sceneHeight - square1.getHeight() - 10);
		
		square2.setX((int) square2PrecisePos);
		square2.setY(sceneHeight - square2.getHeight() - 10);
		
		squareLabel1.setTextFill(Color.WHITE);
		squareLabel1.setAlignment(Pos.CENTER);
		squareLabel1.setMinSize(square1.getWidth(), square1.getHeight());
		squareLabel1.layoutXProperty().bind(square1.xProperty());
		squareLabel1.layoutYProperty().bind(square1.yProperty());
		
		squareLabel2.setTextFill(Color.WHITE);
		squareLabel2.setAlignment(Pos.CENTER);
		squareLabel2.setMinSize(square2.getWidth(), square2.getHeight());
		squareLabel2.layoutXProperty().bind(square2.xProperty());
		squareLabel2.layoutYProperty().bind(square2.yProperty());
		
		collisions.setLayoutY(10);
		collisions.setMinWidth(sceneWidth);
		collisions.setAlignment(Pos.CENTER);
		collisions.setStyle("-fx-font: 24 arial;");
		
		Label sliderLabel = new Label("# of digits of Pi to look for:");
		
		Slider slider = new Slider(1, 5, 1);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setBlockIncrement(1);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setSnapToTicks(true);
		slider.setMaxWidth(150);
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> source, Number oldValue, Number newValue) {
				if(newValue.intValue() == 5) {
					QuickAlert.show(AlertType.WARNING, "This will probably break", "Looking for 5 digits of Pi will almost certainly break the program.\nContinue at your own risk!");
				}
				
				square1.setMass(16 * Math.pow(100, (newValue.intValue() - 1)));
				squareLabel1.setText(square1.getMass() + "");
			}
		});
		
		VBox sliderVBox = new VBox(5, sliderLabel, slider);
		sliderVBox.setAlignment(Pos.CENTER);
		
		Button startButton = new Button("Start");
		startButton.setPrefWidth(50);
		
		Button resetButton = new Button("Reset");
		resetButton.setPrefWidth(50);
		
		Button whatButton = new Button("?");
		whatButton.setPrefWidth(50);
		
		startButton.setOnAction(e -> {
			if(gameLoopRunning) {
				gameLoop.stop();
				
				startButton.setText("Start");
				
				whatButton.setDisable(false);
		        
				gameLoopRunning = false;
			} else {
				gameLoop.start();
		        
		        startButton.setText("Stop");
		        
		        whatButton.setDisable(true);
		        
				gameLoopRunning = true;
			}
		});
		
		resetButton.setOnAction(e -> {
			if(gameLoopRunning) {
				startButton.fire();
			}
			
			numCollisions = 0;
			
			collisions.setText("Collisions: 0");
			
			square1.setVelocity(square1Velocity);
			square1.setPrecisePos(square1PrecisePos);
			square1.setX((int) square1PrecisePos);
			
			square2.setVelocity(square2Velocity);
			square2.setPrecisePos(square2PrecisePos);
			square2.setX((int) square2PrecisePos);
		});
		
		whatButton.setOnAction(e -> {
			QuickAlert.show(AlertType.INFORMATION, "What is this program:", "This program calculates digits of Pi by colliding squares with mass and velocity and counting the number of collisions in the negative direction.\nThis may seem arbitrary but it's a graphical representation of a simple mathamatical proof.\nThe slider allows you to choose how many digits of Pi you will get.\nThe numbers on the squares show their mass.\nThe animation speed value controls the delay between graphical updates.\nThis doesn't affect the result but slower values will produce less visual glitches.");
		});
		
		HBox buttonsHBox = new HBox(20, startButton, resetButton, whatButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		
		Label speedLabel = new Label("Animation speed:");
		
		Label speedLabel2 = new Label("ms");
		
		HBox speedHBox = new HBox(5, speedLabel, speedTextField, speedLabel2);
		speedHBox.setAlignment(Pos.CENTER);
		
		Group backgroundGroup = new Group(verticalLine, horizontalLine, square1, square2, squareLabel1, squareLabel2, collisions);
		
		VBox foregroundVBox = new VBox(20, buttonsHBox, sliderVBox, speedHBox);
		foregroundVBox.setAlignment(Pos.CENTER);
		
		StackPane mainStackPane = new StackPane(backgroundGroup, foregroundVBox);
		
		Scene scene = new Scene(mainStackPane, sceneWidth, 500);
		
		QuickAlert.show(AlertType.WARNING, "This program is not very stable", "The program may become unresponseive, crash, or freeze.\nThe squares may jolt around, dissapear, or leave artifacts from one run to the next.\nPlease keep that in mind and enjoy.");
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Calculating Pi");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	private void collide() {
		double v1 = (((square1.getMass() - square2.getMass()) * square1.getVelocity()) + (2 * square2.getMass() * square2.getVelocity())) / (square1.getMass() + square2.getMass());
		double v2 = ((2 * square1.getMass() * square1.getVelocity()) + ((square2.getMass() - square1.getMass()) * square2.getVelocity())) / (square1.getMass() + square2.getMass());
		
		square1.setVelocity(v1);
		square2.setVelocity(v2);
	}
}

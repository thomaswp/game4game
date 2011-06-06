package edu.elon.honors.price.pong2;

import java.util.Random;


import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.KeyEvent;

public class PongLogic implements Logic {

	//width and height of the score sprite
	private final int SCORE_WIDTH = 200; 
	private final int SCORE_HEIGHT = 100;
	//name of the game used for saving
	private final String NAME = "pong"; 
	
	//the sprites
	private Sprite paddle1, paddle2, ball, score;
	//the Y of the paddle when a touch sequence occurs
	private float startTouchY;
	
	//is the game paused
	private boolean paused;

	private Random rand;
	
	//when the gae starts, do we need to reset?
	//if no, then we've loaded data
	private boolean needReset;
	
	//instance data
	private int player1Score, player2Score;
	private float paddle1X, paddle1Y, paddle2X, paddle2Y;
	private float ballVelX, ballVelY, ballX, ballY;
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	//the amount the paddle moves per frame if
	//the player is using a key
	private float getPlayerMove() {
		return 1.0f / 5;
	}

	//the amound the ai's paddle moves per frame
	private float getAIMove() {
		return 1.0f / 5;
	}

	private int getPaddleWidth() {
		return Graphics.getWidth() / 50;
	}

	private int getPaddleHeight() {
		return Graphics.getHeight() / 3;
	}

	private int getBallRadius() {
		return getPaddleWidth();
	}

	public PongLogic() {
		//be default, we assume we need a reset
		needReset = true;
	}

	@Override
	public void initialize() {
		paused = true;
		if (needReset) 
			newGame();
		createSprites();
	}

	private void createSprites() {
		//initialize the sprites
		paddle1 = new Sprite(Viewport.DefaultViewport, 0, 0, getPaddleWidth(), getPaddleHeight());
		paddle1.getBitmapCanvas().drawColor(Color.WHITE);

		paddle2 = new Sprite(Viewport.DefaultViewport, 0, 0, getPaddleWidth(), getPaddleHeight());
		paddle2.getBitmapCanvas().drawColor(Color.WHITE);

		ball = new Sprite(Viewport.DefaultViewport, 0, 0, getBallRadius() * 2, getBallRadius() * 2);
		ball.getBitmapCanvas().drawCircle(getBallRadius(), getBallRadius(), getBallRadius(), Sprite.paintFromColor(Color.WHITE));
		ball.setOriginX(getBallRadius());
		ball.setOriginY(getBallRadius());

		score = new Sprite(Viewport.DefaultViewport, 
				(Graphics.getWidth() - SCORE_WIDTH) / 2, 
				(Graphics.getHeight() - SCORE_HEIGHT) / 2, 
				SCORE_WIDTH, SCORE_HEIGHT);
		drawScore();
	}

	private void drawScore() {
		//draw the score
		score.getBitmap().eraseColor(Color.TRANSPARENT);
		String text = player1Score +  " - Paused - " + player2Score;

		Paint linePaint = new Paint();

		linePaint.setTextAlign(Align.LEFT);
		linePaint.setTextSize(24);
		linePaint.setColor(Color.GRAY);
		int length = (int)linePaint.measureText(text);

		float x = (score.getWidth() - length) / 2;
		float y = (score.getHeight()) / 2 - 16;

		score.getBitmapCanvas().drawText(text, x, y, linePaint);
	}

	private void updateAI(long elapsedTime) {
		//follow the ball!
		paddle2Y += Math.signum(ballY - (paddle2Y + getPaddleHeight() / 2)) * getAIMove() * elapsedTime;
	}

	private void updatePlayer(long elapsedTime) {
		//if they're using buttons, move the paddle
		if (Input.isDown(KeyEvent.KEYCODE_DPAD_RIGHT)) {
			paddle1Y += getPlayerMove() * elapsedTime;
		}
		if (Input.isDown(KeyEvent.KEYCODE_DPAD_LEFT)) {
			paddle1Y -= getPlayerMove() * elapsedTime;
		}
		//if they're touching the screen, make the paddle's Y
		//the original Y when they started touching plus
		//the Y distance their touch has moved
		if (Input.isTouchDown()) {
			paddle1Y = startTouchY + Input.getDistanceTouchY();
		}
	}

	@Override
	public void update(long elapsedTime) {

		if (Input.isTapped()) {
			//start the game if it's paused
			paused = false;
			//record the paddle's Y when they tapped
			startTouchY = paddle1Y;
		}

		this.score.setVisible(Input.isTouchDown());

		if (paused) {
			//don't update logic if we're paused
			updateSprites();
			return;
		}

		updateAI(elapsedTime);
		updatePlayer(elapsedTime);

		int height = Graphics.getHeight();
		int width = Graphics.getWidth();

		//don't let the paddle go off the screen
		paddle1Y = Math.max(0, Math.min(height - getPaddleHeight(), paddle1Y));
		paddle2Y = Math.max(0, Math.min(height - getPaddleHeight(), paddle2Y));

		//move the ball
		ballX += ballVelX * elapsedTime;
		ballY += ballVelY * elapsedTime;

		//check for score
		if (ballX - getBallRadius() < 0) {
			paused = true;
			player2Score++;
			drawScore();
		} else if (ballX + getBallRadius() > width) {
			paused = true;
			player1Score++;
			drawScore();
		}

		//if we scored, reset the paddles
		if (paused) {
			reset();
		}

		//bounce the ball off walls
		if (ballY - getBallRadius() < 0) {
			ballY = getBallRadius();
			ballVelY *= -1;
		} else if (ballY + getBallRadius() > height) {
			ballY = height - getBallRadius();
			ballVelY *= -1;
		}

		//bounce the ball off paddles
		if (ballX - getBallRadius() < paddle1X + getPaddleWidth() &&
				ballY + getBallRadius() > paddle1Y && 
				ballY - getBallRadius() < paddle1Y + getPaddleHeight()) {
			ballX = paddle1X + getPaddleWidth() + getBallRadius();
			ballVelX *= -1;
			//speed it up for fun
			ballVelX += 0.03 * Math.signum(ballVelX);
		}

		if (ballX + getBallRadius() > paddle2X &&
				ballY + getBallRadius() > paddle2Y && 
				ballY - getBallRadius() < paddle2Y + getPaddleHeight()) {
			ballX = paddle2X - getBallRadius();
			ballVelX *= -1;
			ballVelX += 0.03 * Math.signum(ballVelX);
		}

		//update the sprites
		updateSprites();
	}

	private void updateSprites() {
		//make them where they should be
		paddle1.setX(paddle1X);
		paddle1.setY(paddle1Y);

		paddle2.setX(paddle2X);
		paddle2.setY(paddle2Y);

		ball.setX(ballX);
		ball.setY(ballY);

		score.setVisible(paused);
	}

	public void newGame() {
		//reset the score and the paddles
		player1Score = 0;
		player2Score = 0;
		if (score != null) {
			drawScore();
		}
		reset();
	}
	
	private void reset() {
		//make sure things are paused
		paused = true;

		//reset the paddles and the ball
		int width = Graphics.getWidth();
		int height = Graphics.getHeight();

		ballX = width / 2;
		ballY = height / 2;

		rand = new Random();
		//float angle = rand.nextfloat() * Math.PI / 6 + rand.nextInt(4) * Math.PI / 4 + Math.PI / 6;
		float angle = (float) (rand.nextInt(4) * Math.PI / 2 + Math.PI / 4);
		ballVelX = (float) (Math.cos(angle) / 3);
		ballVelY = (float) (Math.sin(angle) / 3);

		paddle1X = getPaddleWidth() * 2;
		paddle1Y = (height - getPaddleHeight()) / 2;

		paddle2X = width - paddle1X - getPaddleWidth();
		paddle2Y = paddle1Y;
	}

	@Override
	public void save(Activity parent) {
		//save the relevant data
		PongData data = new PongData();
		data.setBallVelX(ballVelX);
		data.setBallVelY(ballVelY);
		data.setBallX(ballX);
		data.setBallY(ballY);
		data.setPaddle1X(paddle1X);
		data.setPaddle1Y(paddle1Y);
		data.setPaddle2X(paddle2X);
		data.setPaddle2Y(paddle2Y);
		data.setPlayer1Score(player1Score);
		data.setPlayer2Score(player2Score);
		
		Data.saveObject(NAME, parent, data);
	}

	@Override
	public void load(Activity parent) {
		try {
			//load data
			PongData data = (PongData)Data.loadObject(NAME, parent);
			this.ballVelX = data.getBallVelX();
			this.ballVelY = data.getBallVelY();
			this.ballX = data.getBallX();
			this.ballY = data.getBallY();
			this.paddle1X = data.getPaddle1X();
			this.paddle1Y = data.getPaddle1Y();
			this.paddle2X = data.getPaddle2X();
			this.paddle2Y = data.getPaddle2Y();
			this.player1Score = data.getPlayer1Score();
			this.player2Score = data.getPlayer2Score();
			
			needReset = false;
		} catch (Exception ex) {
			System.out.println("Data improperly stored:");
			ex.printStackTrace();
		}
	}
}

package com.twp.pong2;

import java.util.HashMap;
import java.util.Random;

import com.twp.graphics.Graphics;
import com.twp.graphics.Sprite;
import com.twp.graphics.Viewport;
import com.twp.input.Input;
import com.twp.game.Game;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;

public class PongLogic implements Game {
	
	private int player1Score, player2Score;
	private double paddle1X, paddle1Y, paddle2X, paddle2Y;
	
	private double ballVelX, ballVelY, ballX, ballY;
	
	private long lastTime;
	
	private boolean paused;
	
	private Random rand;
	
	private double getPlayerMove() {
		return 1.0 / 5;
	}
	
	private double getAIMove() {
		return 1.0 / 5;
	}
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public int getPaddleWidth() {
		return Graphics.getWidth() / 50;
	}
	
	public int getPaddleHeight() {
		return Graphics.getHeight() / 3;
	}
	
	public int getBallRadius() {
		return getPaddleWidth();
	}
	
	public int getPaddle1X() {
		return (int)paddle1X;
	}

	public int getPaddle1Y() {
		return (int)paddle1Y;
	}

	public int getPaddle2X() {
		return (int)paddle2X;
	}

	public int getPaddle2Y() {
		return (int)paddle2Y;
	}

	public int getPlayer1Score() {
		return player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public int getBallX() {
		return (int)ballX;
	}

	public int getBallY() {
		return (int)ballY;
	}
	
	private Sprite paddle1, paddle2, ball, score;

	public void initialize() {
		
		player1Score = 0;
		player2Score = 0;
		lastTime = System.currentTimeMillis();
		
		createSprites();
		
		reset();
		
		paused = true;
	}
	
	private void createSprites() {
		paddle1 = new Sprite(Viewport.DefaultViewport, 0, 0, getPaddleWidth(), getPaddleHeight());
		paddle1.getBitmap().eraseColor(Color.WHITE);
		
		paddle2 = new Sprite(Viewport.DefaultViewport, 0, 0, getPaddleWidth(), getPaddleHeight());
		paddle2.getBitmap().eraseColor(Color.WHITE);
		
		ball = new Sprite(Viewport.DefaultViewport, 0, 0, getBallRadius(), getBallRadius());
		ball.getBitmap().eraseColor(Color.WHITE);
		ball.setOriginX(getBallRadius() / 2);
		ball.setOriginY(getBallRadius() / 2);
	}
	
	public void updateAI(long elapsedTime) {
		paddle2Y += Math.signum(ballY - (paddle2Y + getPaddleHeight() / 2)) * getAIMove() * elapsedTime;
	}
	
	public void updatePlayer(long elapsedTime) {
		if (Input.isDown(KeyEvent.KEYCODE_DPAD_RIGHT)) {
			paddle1Y += getPlayerMove() * elapsedTime;
		}
		if (Input.isDown(KeyEvent.KEYCODE_DPAD_LEFT)) {
			paddle1Y -= getPlayerMove() * elapsedTime;
		}
	}
	
	public void update() {
		long elapsedTime = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		paddle1Y = Input.getLastTouchY();
		if (paddle1Y != 0) paused = false;
		
		if (paused) {
			updateSprites();
			return;
		}
		
		updateAI(elapsedTime);
		updatePlayer(elapsedTime);
		
		int height = Graphics.getHeight();
		int width = Graphics.getWidth();
		
		paddle1Y = Math.max(0, Math.min(height - getPaddleHeight(), paddle1Y));
		paddle2Y = Math.max(0, Math.min(height - getPaddleHeight(), paddle2Y));
		
		ballX += ballVelX * elapsedTime;
		ballY += ballVelY * elapsedTime;
		
		if (ballX - getBallRadius() < 0) {
			paused = true;
			player2Score++;
		} else if (ballX + getBallRadius() > width) {
			paused = true;
			player1Score++;
		}
		
		if (paused) {
			reset();
		}
		
		if (ballY - getBallRadius() < 0) {
			ballY = getBallRadius();
			ballVelY *= -1;
		} else if (ballY + getBallRadius() > height) {
			ballY = height - getBallRadius();
			ballVelY *= -1;
		}
		
		if (ballX - getBallRadius() < paddle1X + getPaddleWidth() &&
				ballY + getBallRadius() > paddle1Y && 
				ballY - getBallRadius() < paddle1Y + getPaddleHeight()) {
			ballX = paddle1X + getPaddleWidth() + getBallRadius();
			ballVelX *= -1;
			ballVelX += 0.03 * Math.signum(ballVelX);
		}
		
		if (ballX + getBallRadius() > paddle2X &&
				ballY + getBallRadius() > paddle2Y && 
				ballY - getBallRadius() < paddle2Y + getPaddleHeight()) {
			ballX = paddle2X - getBallRadius();
			ballVelX *= -1;
			ballVelX += 0.03 * Math.signum(ballVelX);
		}
		
		updateSprites();
	}
	
	private void updateSprites() {
		paddle1.setX(paddle1X);
		paddle1.setY(paddle1Y);
		
		paddle2.setX(paddle2X);
		paddle2.setY(paddle2Y);
		
		ball.setX(ballX);
		ball.setY(ballY);
	}
	
	public void reset() {
		int width = Graphics.getWidth();
		int height = Graphics.getHeight();
		
		ballX = width / 2;
		ballY = height / 2;
		
		rand = new Random();
		//double angle = rand.nextDouble() * Math.PI / 6 + rand.nextInt(4) * Math.PI / 4 + Math.PI / 6;
		double angle = rand.nextInt(4) * Math.PI / 2 + Math.PI / 4;
		ballVelX = Math.cos(angle) / 3;
		ballVelY = Math.sin(angle) / 3;
		
		paddle1X = getPaddleWidth() * 2;
		paddle1Y = (height - getPaddleHeight()) / 2;
		
		paddle2X = width - paddle1X - getPaddleWidth();
		paddle2Y = paddle1Y;
	}
	
	public void save(Bundle map) {
		map.putDouble("p1y", paddle1Y);
		map.putDouble("p2y", paddle2Y);
		map.putDouble("ballvx", ballVelX);
		map.putDouble("ballvy", ballVelY);
		map.putInt("p1s", player1Score);
		map.putInt("p2s", player2Score);
	}
	
	public void load(Bundle map) {
		reset();
		
		paddle1Y = map.getDouble("p1y");
		paddle2Y = map.getDouble("p2y");
		ballVelX = map.getDouble("ballvx");
		ballVelY = map.getDouble("ballvy");
		player1Score = map.getInt("p1s");
		player2Score = map.getInt("p2s");
		paused = true;
	}
}

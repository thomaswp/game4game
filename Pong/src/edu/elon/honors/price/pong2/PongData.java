package edu.elon.honors.price.pong2;

import java.io.Serializable;

//Holds all the serializable data for the game
public class PongData implements Serializable {

	private static final long serialVersionUID = 1L;
	private int player1Score, player2Score;
	private float paddle1X, paddle1Y, paddle2X, paddle2Y;

	private float ballVelX, ballVelY, ballX, ballY;
	
	public int getPlayer1Score() {
		return player1Score;
	}

	public void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	public float getPaddle1X() {
		return paddle1X;
	}

	public void setPaddle1X(float paddle1x) {
		paddle1X = paddle1x;
	}

	public float getPaddle1Y() {
		return paddle1Y;
	}

	public void setPaddle1Y(float paddle1y) {
		paddle1Y = paddle1y;
	}

	public float getPaddle2X() {
		return paddle2X;
	}

	public void setPaddle2X(float paddle2x) {
		paddle2X = paddle2x;
	}

	public float getPaddle2Y() {
		return paddle2Y;
	}

	public void setPaddle2Y(float paddle2y) {
		paddle2Y = paddle2y;
	}

	public float getBallVelX() {
		return ballVelX;
	}

	public void setBallVelX(float ballVelX) {
		this.ballVelX = ballVelX;
	}

	public float getBallVelY() {
		return ballVelY;
	}

	public void setBallVelY(float ballVelY) {
		this.ballVelY = ballVelY;
	}

	public float getBallX() {
		return ballX;
	}

	public void setBallX(float ballX) {
		this.ballX = ballX;
	}

	public float getBallY() {
		return ballY;
	}

	public void setBallY(float ballY) {
		this.ballY = ballY;
	}
}

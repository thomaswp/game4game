package edu.elon.honors.price.pong2;

import java.io.Serializable;

//Holds all the serializable data for the game
public class PongData implements Serializable {

	private static final long serialVersionUID = 1L;
	private int player1Score, player2Score;
	private double paddle1X, paddle1Y, paddle2X, paddle2Y;

	private double ballVelX, ballVelY, ballX, ballY;
	
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

	public double getPaddle1X() {
		return paddle1X;
	}

	public void setPaddle1X(double paddle1x) {
		paddle1X = paddle1x;
	}

	public double getPaddle1Y() {
		return paddle1Y;
	}

	public void setPaddle1Y(double paddle1y) {
		paddle1Y = paddle1y;
	}

	public double getPaddle2X() {
		return paddle2X;
	}

	public void setPaddle2X(double paddle2x) {
		paddle2X = paddle2x;
	}

	public double getPaddle2Y() {
		return paddle2Y;
	}

	public void setPaddle2Y(double paddle2y) {
		paddle2Y = paddle2y;
	}

	public double getBallVelX() {
		return ballVelX;
	}

	public void setBallVelX(double ballVelX) {
		this.ballVelX = ballVelX;
	}

	public double getBallVelY() {
		return ballVelY;
	}

	public void setBallVelY(double ballVelY) {
		this.ballVelY = ballVelY;
	}

	public double getBallX() {
		return ballX;
	}

	public void setBallX(double ballX) {
		this.ballX = ballX;
	}

	public double getBallY() {
		return ballY;
	}

	public void setBallY(double ballY) {
		this.ballY = ballY;
	}
}

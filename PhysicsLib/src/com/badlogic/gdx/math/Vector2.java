/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.badlogic.gdx.math;

import java.io.Serializable;
import java.util.LinkedList;


/**
 * Encapsulates a 2D vector. Allows chaining methods by returning a reference to itself
 * @author badlogicgames@gmail.com
 * 
 */
public class Vector2 implements Serializable {	
	private static final long serialVersionUID = 913902788239530931L;

	/** static temporary vector **/
	private final static Vector2 tmp = new Vector2();

	private final static LinkedList<Vector2> freeTemps = new LinkedList<Vector2>();
	private final static LinkedList<Vector2> usedTemps = new LinkedList<Vector2>();
	
	/** the x-component of this vector **/
	public float x;
	/** the y-component of this vector **/
	public float y;

	/**
	 * Constructs a new vector at (0,0)
	 */
	public Vector2 () {

	}

	/**
	 * Constructs a vector with the given components
	 * @param x The x-component
	 * @param y The y-component
	 */
	public Vector2 (float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a vector from the given vector
	 * @param v The vector
	 */
	public Vector2 (Vector2 v) {
		set(v);
	}

	/**
	 * @return a copy of this vector
	 */
	public Vector2 cpy () {
		return new Vector2(this);
	}

	/**
	 * @return The euclidian length
	 */
	public float len () {
		return (float)Math.sqrt(x * x + y * y);
	}

	/**
	 * @return The squared euclidian length
	 */
	public float len2 () {
		return x * x + y * y;
	}

	/**
	 * Sets this vector from the given vector
	 * @param v The vector
	 * @return This vector for chaining
	 */
	public Vector2 set (Vector2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	/**
	 * Sets the components of this vector
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining
	 */
	public Vector2 set (float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Substracts the given vector from this vector.
	 * @param v The vector
	 * @return This vector for chaining
	 */
	public Vector2 sub (Vector2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	/**
	 * Normalizes this vector
	 * @return This vector for chaining
	 */
	public Vector2 nor () {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	/**
	 * Adds the given vector to this vector
	 * @param v The vector
	 * @return This vector for chaining
	 */
	public Vector2 add (Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	/**
	 * Adds the given components to this vector
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining
	 */
	public Vector2 add (float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * @param v The other vector
	 * @return The dot product between this and the other vector
	 */
	public float dot (Vector2 v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Multiplies this vector by a scalar
	 * @param scalar The scalar
	 * @return This vector for chaining
	 */
	public Vector2 mul (float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/**
	 * @param v The other vector
	 * @return the distance between this and the other vector
	 */
	public float dst (Vector2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/**
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the distance between this and the other vector
	 */
	public float dst (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/**
	 * @param v The other vector
	 * @return the squared distance between this and the other vector
	 */
	public float dst2 (Vector2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public String toString () {
		return "[" + x + ":" + y + "]";
	}

	/**
	 * Substracts the other vector from this vector.
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return This vector for chaining
	 */
	public Vector2 sub (float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * @return a temporary copy of this vector. Use with care as this is backed by a single static Vector2 instance. v1.tmp().add(
	 *         v2.tmp() ) will not work!
	 */
	public Vector2 tmp () {
		return tmp.set(this);
	}

	public Vector2 tempCopy() {
		if (freeTemps.size() == 0) {
			freeTemps.add(new Vector2());
		}
		Vector2 temp = freeTemps.remove();
		usedTemps.add(temp);
		if (usedTemps.size() > 1000) {
			throw new RuntimeException("Memory Leak in temp variables!");
		}
		return temp.set(this);
	}
	
	public void releaseTemp() {
		usedTemps.remove(this);
		freeTemps.add(this);
	}
	
	public static void releaseTemps() {
		freeTemps.addAll(usedTemps);
		usedTemps.clear();
	}
	
	/**
	 * Multiplies this vector by the given matrix
	 * @param mat the matrix
	 * @return this vector
	 */
	public Vector2 mul (Matrix3 mat) {
		float x = this.x * mat.vals[0] + this.y * mat.vals[3] + mat.vals[6];
		float y = this.x * mat.vals[1] + this.y * mat.vals[4] + mat.vals[7];
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Calculates the 2D cross product between this
	 * and the given vector.
	 * @param v the other vector
	 * @return the cross product
	 */
	public float crs(Vector2 v) {		
		return this.x * v.y - this.y * v.x;
	}
	
	/**
	 * Calculates the 2D cross product between this
	 * and the given vector.
	 * @param x the x-coordinate of the other vector
	 * @param y the y-coordinate of the other vector
	 * @return the cross product
	 */
	public float crs(float x, float y) {
		 return this.x * y - this.y * x;
	}
	
	/**
	 * @return the angle in degrees of this vector (point) relative to the x-axis. Angles are counter-clockwise and between 0 and 360.
	 */
	public float angle() {
      float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
      if(angle < 0)
          angle += 360;
      return angle;
  }
	
	/**
	 * Rotates the Vector2 by the given angle, counter-clockwise.
	 * @param angle the angle in degrees
	 * @return the 
	 */
   public Vector2 rotate(float angle) {
      float rad = angle * MathUtils.degreesToRadians;
      float cos = (float)Math.cos(rad);
      float sin = (float)Math.sin(rad);
      
      float newX = this.x * cos - this.y * sin;
      float newY = this.x * sin + this.y * cos;
      
      this.x = newX;
      this.y = newY;
      
      return this;
  }
   
	/**
	 * Linearly interpolates between this vector and the target vector by alpha which is in the range [0,1]. The result is stored
	 * in this vector.
	 * 
	 * @param target The target vector
	 * @param alpha The interpolation coefficient
	 * @return This vector for chaining.
	 */
	public Vector2 lerp (Vector2 target, float alpha) {
		Vector2 r = this.mul(1.0f - alpha);
		r.add(target.tmp().mul(alpha));
		return r;
	}
}
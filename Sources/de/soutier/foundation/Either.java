package de.soutier.foundation;

/**
 * Either as known from Haskell, Scala etc.
 * Left is the error or exception, Right is the correct ("right") value.
 */
public class Either<Left, Right> {
	private final Left left;
	private final Right right;
	
	private Either(Left left, Right right) {
		this.left = left;
		this.right = right;
	}
	
	public static <Left, Right> Either<Left, Right> left(Left value) {
		return new Either<Left, Right>(value, null);
	}
	
	public static <Left, Right> Either<Left, Right> right(Right value) {
		return new Either<Left, Right>(null, value);
	}
	
	public boolean isLeft() { return left != null; };
	public boolean isRight() { return right != null; };
	
	public Left left() {
		return left;
	}
	
	public Right right() {
		return right;
	}
	
	// Object is unfortunate
	public Object value() {
		if (left != null) return left; else return right;
	}
	
	// As seen in Scala
	public Either<Right, Left> swap() {
		return new Either<Right, Left>(right, left);
	}
}

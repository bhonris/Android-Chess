package com.kdoherty.chess;

import java.util.ArrayList;
import java.util.List;

import com.kdoherty.engine.KingEval;

/**
 * 
 * @author Kevin Doherty
 * 
 *         This class represents a King chess piece. This Piece can move one
 *         square in any direction unless a piece of the opposing color is
 *         attacking that square. It can also castle if certain conditions are
 *         met. This Piece can never still be in check after a Piece of this
 *         King's color moves.
 */

public final class King extends Piece {

	/** The home row of this King */
	private int homeRow;

	private boolean hasCastled;

	/**
	 * Constructor for King sets the color and initializes hasMoved, and homeRow
	 * 
	 * @param color
	 *            The color of this King
	 */
	public King(Color color) {
		super(color);
		hasCastled = false;
		homeRow = color == Color.WHITE ? 7 : 0;
	}

	/**
	 * Can this piece move on the input board to the input square?
	 * 
	 * @param b
	 *            The Board the piece is checking if it can move on
	 * @param r
	 *            The row we are checking if the piece can move to
	 * @param c
	 *            The column we are checking if the piece can move to
	 * 
	 * @param testCheck
	 *            boolean
	 * @return true if the Piece can move to the input square and false
	 *         otherwise
	 */
	public boolean canMove(Board b, int r, int c, boolean testCheck) {
		if (Board.isNeighbor(row, col, r, c)) {
			return Board.isInbounds(r, c)
					&& (b.isEmpty(r, c) || isTaking(b, r, c))
					&& (!testCheck || !stillInCheck(b, r, c));
		}
		return canCastle(b, r, c);
	}

	/**
	 * Is this Piece attacking the input square? Note it is still attacking the
	 * square even if it is pinned to it's king
	 * 
	 * @param b
	 *            The Board we are checking if the Piece is attacking a square
	 *            on
	 * @param r
	 *            The row we are checking if this Piece is attacking
	 * @param c
	 *            The row we are checking if this Piece is attacking
	 * 
	 * @return true if this Piece is attacking the input square on the input
	 *         Board and false otherwise
	 */
	public boolean isAttacking(Board b, int r, int c) {
		return Board.isNeighbor(row, col, r, c)
				&& (b.isEmpty(r, c) || b.getOccupant(r, c).getColor() != color);
	}

	/**
	 * Is this Piece defending the input square? Note it is not defending the
	 * square if it is pinned to it's king, or if the input square contains a
	 * Piece of the opposite color
	 * 
	 * @param b
	 *            The Board we are checking if this Piece is defending a square
	 *            on
	 * @param r
	 *            The row we are checking if it is defended by this piece
	 * @param c
	 *            The column we are checking if it is defended by this piece
	 * 
	 * @return true if this Piece is defending the input square on the input
	 *         Board and false otherwise
	 */
	public boolean isDefending(Board b, int r, int c) {
		return Board.isNeighbor(row, col, r, c)
				&& (b.isEmpty(r, c) || b.getOccupant(r, c).getColor() == color);
	}

	/**
	 * Moves this Piece to the input square if it can move there
	 * 
	 * @param b
	 *            The Board we are moving this Piece on
	 * @param r
	 *            The row we are moving this Piece to
	 * @param c
	 *            The column we are moving this Piece to
	 */
	@Override
	public void moveTo(Board b, int r, int c) {
		incrementMoveCount();
		b.setEnPoissantSq(null);
		if (Board.isNeighbor(row, col, r, c)) {
			b.movePiece(row, col, r, c);
		} else {
			if (c == 6) {
				castle(b, true);
			} else {
				castle(b, false);
			}

		}
	}

	/**
	 * Finds all moves this Piece can make on the input Board
	 * 
	 * @param b
	 *            The Board on which we are getting all moves of this Piece
	 * 
	 * @return All moves this Piece can make on the input Board
	 */
	@Override
	public List<Move> getMoves(Board b) {
		List<Move> moves = new ArrayList<Move>();
		for (int i = row - 1; i < row + 2; i++) {
			for (int j = col - 1; j < col + 2; j++) {
				if (!(i == row && j == col)) {
					if (canMove(b, i, j) && !canCastle(b, i, j)) {
						moves.add(new Move(b, this, i, j, Move.Type.NORMAL));
					}
				}
			}
		}
		if (color == Color.WHITE) {
			if (canCastle(b, 7, 6))
				moves.add(new Move(b, this, 7, 6, Move.Type.WHITE_SHORT));
			if (canCastle(b, 7, 2))
				moves.add(new Move(b, this, 7, 2, Move.Type.WHITE_LONG));
		} else {
			if (canCastle(b, 0, 6))
				moves.add(new Move(b, this, 0, 6, Move.Type.BLACK_SHORT));
			if (canCastle(b, 0, 2))
				moves.add(new Move(b, this, 0, 2, Move.Type.BLACK_LONG));
		}

		return moves;
	}

	/**
	 * This will represent the piece as a String If the piece is white a
	 * lowerCase letter will be used If the piece is black an upperCase letter
	 * will be used
	 * 
	 * 
	 * @return A String representation of this Piece
	 */
	public String toString() {
		return color == Color.WHITE ? "k" : "K";
	}

	/**
	 * Can this King castle to the input row/column
	 * 
	 * @param b
	 *            The Board this King is checking if it can castle on
	 * @param r
	 *            The row this King is trying to castle to
	 * @param c
	 *            The column this King is trying to castle to
	 * 
	 * @return true if this King can castle to the sepcified row/column
	 */
	public boolean canCastle(Board b, int r, int c) {
		if (hasMoved() || col != 4 || isInCheck(b) || r != homeRow || row != homeRow) {
			return false;
		}
		if (c == 6) {
			return canCastleShort(b);
		}
		if (c == 2) {
			return canCastleLong(b);
		}
		return false;
	}

	/**
	 * Helper method for canCastle Can this King castle short on the input
	 * Board?
	 * 
	 * @param b
	 *            The Board this King is checking if it can castle on
	 * 
	 * @return true if this King can castle short on the input Board
	 */
	private boolean canCastleShort(Board b) {
		// No need to check hasMoved or is in check as they
		// are already checked in canCastle
		int col = 6;
		int rookCol = 7;
		Piece rook = b.getOccupant(homeRow, rookCol);
		return rook instanceof Rook && !rook.hasMoved()
				&& b.isEmpty(homeRow, col) && b.isEmpty(homeRow, 5)
				&& !b.isAttacked(homeRow, col, color.opp())
				&& !b.isAttacked(homeRow, 5, color.opp());
	}

	/**
	 * Helper method for canCastle Can this King castle long on the input Board?
	 * 
	 * @param b
	 *            The Board this King is checking if it can castle on
	 * 
	 * @return true if this King can castle short on the input Board
	 */
	private boolean canCastleLong(Board b) {
		int col = 2;
		int rookCol = 0;
		Piece rook = b.getOccupant(homeRow, rookCol);
		return rook instanceof Rook && !rook.hasMoved()
				&& b.isEmpty(homeRow, col) && b.isEmpty(homeRow, 3)
				&& b.isEmpty(homeRow, 1)
				&& !b.isAttacked(homeRow, col, color.opp())
				&& !b.isAttacked(homeRow, 3, color.opp());
	}

	/**
	 * Carries out the action of castling on the input Board
	 * 
	 * @param b
	 *            The Board this King is attempting to castle on
	 * @param shortCastle
	 *            boolean flag if this method should castle the king short or
	 *            long
	 */
	public void castle(Board b, boolean shortCastle) {
		int rookTo, kingTo, rookFrom;
		if (shortCastle) {
			rookTo = 5; // where to move the rook
			kingTo = 6; // where to move the king
			rookFrom = 7; // where the rook used to be
		} else {
			rookTo = 3; // where to move the rook
			kingTo = 2; // where to move the king
			rookFrom = 0; // where the rook used to be
		}
		b.movePiece(row, col, row, kingTo);
		b.getOccupant(homeRow, rookFrom).moveTo(b, homeRow, rookTo);
		hasCastled = true;
	}

	/**
	 * Is this King in check on the input Board
	 * 
	 * @param b
	 *            The Board we are checking if this King is in check on
	 * 
	 * @return true if this King is in check on the input Board
	 */
	public boolean isInCheck(Board b) {
		return b.isAttacked(row, col, color.opp());
	}

	public void setHasCastled(boolean hasCastled) {
		this.hasCastled = hasCastled;
	}

	public boolean hasCastled() {
		return hasCastled;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && ((King) obj).hasCastled == hasCastled;
	}

	@Override
	public int hashCode() {
		int hasCastledVal = hasCastled ? 1231 : 1237;
		return super.hashCode() + hasCastledVal;
	}
	
	@Override
	public int evaluate(Board board) {
		return new KingEval(board, this).evaluate();
	}

	@Override
	public int getStartingValue() {
		return KingEval.START_VALUE;
	}

	@Override
	public Piece clone() {
		King clone = new King(color);
		clone.row = row;
		clone.col = col;
		clone.moveCount = moveCount;
		clone.hasCastled = hasCastled;
		return clone;
	}
}

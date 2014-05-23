package com.kdoherty.engine;

import java.util.List;

import com.kdoherty.chess.Board;
import com.kdoherty.chess.Color;
import com.kdoherty.chess.Move;

public class CpuPlayer {
	
	private Color color;
	private int depth;
	
	public CpuPlayer(Color color, int depth) {
		this.color = color;
		this.depth = depth;
	}

	public Move negaMaxMove(Board board) {
		Move lastMove = board.getLastMove();
		if (board.getMoveCount() == 1) {
			if (lastMove.toString().equals("pe4")) {
				return new Move(board.getOccupant(1, 4), 3, 4);
			} else if (lastMove.toString().equals("pd4")) {
				return new Move(board.getOccupant(1, 3), 3, 3);
			}
		}
		int max = Integer.MIN_VALUE;
		Move bestMove = null;
		int mateDepth = Evaluate.queenCloseToKing(board, color) ? 5 : 2;
		List<Move> mateMoves = MateSolver.findMateUpToN(board, color, mateDepth);
		if (mateMoves != null && !mateMoves.isEmpty()) {
			return mateMoves.get(0);
		}
		for (Move move : MoveSorter.sort(board, board.getMoves(color))) {
			move.make(board);
			int score = -negaMaxWithPruning(board, color.opp(),
					Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
			if (score > max) {
				max = score;
				bestMove = move;
			}
			move.unmake(board);
		}
		return bestMove;
	}

	int negaMaxWithPruning(Board board, Color color, int alpha, int beta,
			int moveDepth) {
		if (board.isGameOver()) {
			return Evaluate.evaluate(board, color, false);
		}
		if (moveDepth == 0) {
			return Evaluate.evaluate(board, color, false);
		}
		
		int max = Integer.MIN_VALUE;
		for (Move move : MoveSorter.sort(board, board.getMoves(color))) {
			move.make(board);
			int score = -negaMaxWithPruning(board, color.opp(), -beta, -alpha,
					moveDepth - 1);
			move.unmake(board);
			max = Math.max(max, score);
			alpha = Math.max(alpha, score);
			if (alpha >= beta) {
				return alpha;
			}
		}
		return max;
	}
}

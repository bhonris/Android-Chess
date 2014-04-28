package com.kdoherty.androidchess;

import java.util.HashMap;

import com.example.androidchess.R;
import com.kdoherty.chess.Piece;

public final class PieceImages {

	private static HashMap<String, Integer> instance = new HashMap<String, Integer>();

	private PieceImages() {
	}

	static {
		// Note: Not using Pieces for keys because they are mutable
		instance.put("p", R.drawable.whitepawn);
		instance.put("r", R.drawable.whiterook);
		instance.put("n", R.drawable.whiteknight);
		instance.put("b", R.drawable.whitebishop);
		instance.put("k", R.drawable.whiteking);
		instance.put("q", R.drawable.whitequeen);

		instance.put("P", R.drawable.blackpawn);
		instance.put("R", R.drawable.blackrook);
		instance.put("N", R.drawable.blackknight);
		instance.put("B", R.drawable.blackbishop);
		instance.put("K", R.drawable.blackking);
		instance.put("Q", R.drawable.blackqueen);
	}

	public static Integer getId(Piece piece) {
		if (piece == null) {
			return R.color.transparent;
		}
		return instance.get(piece.toString());
	}
}

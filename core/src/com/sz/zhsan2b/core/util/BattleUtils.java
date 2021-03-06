package com.sz.zhsan2b.core.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sz.zhsan2b.core.Constants;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.StepAction.FaceDirection;

public class BattleUtils {
	private static Logger logger = LoggerFactory.getLogger(BattleUtils.class);

	/*
	 * return damage, 用最简单的计算公式
	 */
	public static int calculateDamage(Troop victim, Troop generate) {
		// 先简单模拟一下最简单的计算公式
		return generate.getBattleProperties().ack
				- victim.getBattleProperties().def;
	}

	public static boolean isObjectInAttackRange(Troop object, Troop orgin) {

		return isObjectInAttackRange(object.getPosition(), orgin.getPosition(),
				orgin.getBattleProperties().range,
				orgin.getBattleProperties().isXie);
	}

	public static boolean isObjectInAttackRange(Position object,
			Position origin, int range, boolean isXie) {
		List<Position> rangeList = getAttackRangeList(origin, range, isXie);
		// 返回是否在队列中
		boolean returnBool= contains(rangeList, object);
		return returnBool;

	}

	public static List<Position> getAttackRangeList(Position origin,
			int range, boolean isXie) {
		List<Position> rangeList = new ArrayList<Position>(range * 10);
		int minX = Math.max(0, origin.x - range);
		int maxX = Math
				.min(Constants.BATTLE_FIELD_XCOUNT - 1, origin.x + range);
		int minY = Math.max(0, origin.y - range);
		int maxY = Math
				.min(Constants.BATTLE_FIELD_YCOUNT - 1, origin.y + range);
		// 构造斜向list
		if (isXie) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					Position p = new Position(x, y);
					rangeList.add(p);
					//logger.debug(p.toString());
				}
			}
		} else {// 构造非斜向list
			int half = range;
			boolean change = false;
			for (int y = origin.y - range; y <= origin.y + range; y++) {
				for (int x = origin.x - range + half; x <= origin.x + range
						- half; x++) {
					Position p = new Position(x, y);
					rangeList.add(p);
					//logger.debug(p.toString());
				}
				if (half == 0)
					change = true;
				if (!change) {
					half--;
				} else {
					half++;
				}

			}
			// remove一个gdx array ，必须从后往前remove.否则会造成索引混乱
			for (int index = rangeList.size() - 1; index >= 0; index--) {
				Position p = rangeList.get(index);
				if (isOutOfBound(p.x, 0, Constants.BATTLE_FIELD_XCOUNT - 1)
						|| isOutOfBound(p.y, 0,
								Constants.BATTLE_FIELD_YCOUNT - 1)) {
					rangeList.remove(index);
					//logger.debug(p.toString() + " is removed.");

				}
			}
		}
		return rangeList;
	}

	public static boolean contains(List<Position> rangeList, Position object) {
		boolean isContain = false;
		if(rangeList==null||object==null){ return isContain;}
		else if(rangeList.size()==0){
			return isContain;
		}
		for (Position p : rangeList) {
			if (p.compareTo(object) == 0) {
				isContain = true;
				break;
			}
		}
		return isContain;
	}

	public static boolean isOutOfBound(int i, int min, int max) {
		if (i < min || i > max)
			return true;
		else
			return false;
	}

	public static FaceDirection calculateFaceDirection(FaceDirection curFaceDi,
			Position self, Position object) {
		int ox = object.x;
		int oy = object.y;
		int sx = self.x;
		int sy = self.y;
		if (ox > sx && oy == sy) {
			curFaceDi = FaceDirection.RIGHT;
		} else if (ox > sx && oy > sy) {
			if (curFaceDi != FaceDirection.UP)
				curFaceDi = FaceDirection.RIGHT;
		} else if (ox == sx && oy > sy) {
			curFaceDi = FaceDirection.UP;
		} else if (ox < sx && oy > sy) {
			if (curFaceDi != FaceDirection.UP)
				curFaceDi = FaceDirection.LEFT;
		} else if (ox < sx && oy == sy) {
			curFaceDi = FaceDirection.LEFT;
		} else if (ox < sx && oy < sy) {
			if (curFaceDi != FaceDirection.DOWN)
				curFaceDi = FaceDirection.LEFT;
		} else if (ox == sx && oy < sy) {
			curFaceDi = FaceDirection.DOWN;
		} else if (ox > sx && oy < sy) {
			if (curFaceDi != FaceDirection.DOWN)
				curFaceDi = FaceDirection.RIGHT;
		}
		return curFaceDi;
	}
	//already consider the border problem.
	public static List<Position> getEightDirectionPosition(Position orgin) {
		
		return getAttackRangeList(orgin, 1, true);
	}

}

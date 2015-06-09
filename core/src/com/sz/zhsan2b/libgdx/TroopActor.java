package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;

import org.jfree.ui.Align;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.StepAction;
import com.sz.zhsan2b.core.Troop;


public class TroopActor extends AnimatedImage {
	
	public enum TROOP_ANIMATION_TYPE {
		WALK,ATTACK,BE_ATTACKED,CAST,BE_CAST
	}

	public static final String TAG = TroopActor.class.getName();
	private static Logger logger = LoggerFactory.getLogger(TroopActor.class);
	//game logic properties
	private Troop troop;
	private Array<TroopActor> affectedTroopList;
	private boolean isDestoryed;
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	//for integration
	private Vector2 position = new Vector2();

	
	//troop title
	
	private Table troopTitle;
	private int hpVisual; 
	private Image troopState;
	private Label hpVisualLabel;
	private Skin skinTroopTitle= Assets.instance.assetSkin.skinTroopTitle;
	

	public TroopActor(Troop troop) {
	
		this.troop = troop;
		hpVisual=troop.getHp();
		troopState = new Image(Assets.instance.assetTroop.actionUnDone);
		hpVisualLabel=new Label(String.valueOf(hpVisual), skinLibgdx);
		setWidth(Constants.WANGGE_UNIT_WIDTH);
		setHeight(Constants.WANGGE_UNIT_HEIGHT);
		affectedTroopList = null;
		isDestoryed = false;			
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(RenderUtils.getTroopAnimationBy(troop.getMilitaryKind().getId(), com.sz.zhsan2b.core.StepAction.FaceDirection.UP, TROOP_ANIMATION_TYPE.WALK));
		//build troopTitle
		troopTitle=buildTroopTitle();
	}

	private Table buildTroopTitle() {
		Table returnTable = new Table();
		returnTable.setLayoutEnabled(false);
		Image back =new Image(Assets.instance.assetTroop.background);
		back.setSize(120, 70);
		back.setPosition(30, 0);
		returnTable.add(back);
		Table infoTable = new Table();
		returnTable.add(infoTable);
		infoTable.setSize(150f, 70f);
		infoTable.setPosition(0, 0);
		Image faction = new Image(Assets.instance.assetTroop.faction);
		if(getTroop().getOwner()==PLAYER_TYPE.PLAYER){
			faction.setColor(Color.BLUE);
		}else{
			faction.setColor(Color.RED);
		}
		infoTable.add(faction).width(30f).height(40f).top();
		Image portrait = new Image(Assets.instance.assetPerson.smallPersonPortraits.firstValue());
		infoTable.add(portrait).width(50f).left();
		Table hpTable = new Table();
		hpTable.add(hpVisualLabel).width(50).left();
		hpTable.add(troopState).width(14f);
		hpTable.row();
		Label name = new Label("zhangfei",skinLibgdx);
		hpTable.add(name).colspan(2).center();
		infoTable.add(hpTable).width(70);
		infoTable.row();
		infoTable.add();
		Table shiqiTable = new Table();
		infoTable.add(shiqiTable).colspan(2).height(8f).width(120f);
		Image shiqicao = new Image(Assets.instance.assetTroop.shiqicao);
		Image shiqitiao = new Image(Assets.instance.assetTroop.shiqitiao);
		shiqiTable.setLayoutEnabled(false);
		shiqiTable.add(shiqicao);
		shiqiTable.add(shiqitiao);
		shiqicao.setAlign(Align.LEFT); 
		shiqicao.setWidth(120f);
		shiqitiao.setPosition(2f,2f);
		shiqitiao.setWidth(50f);

		returnTable.setX(getX()+30f);
		returnTable.setY(getY()+60f);
		return returnTable;
	}

	public Troop getTroop() {
		return troop;
	}

	public void setTroop(Troop troop) {
		this.troop = troop;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Array<TroopActor> getAffectedTroopList() {
		return affectedTroopList;
	}

	public void setAffectedTroopList(Array<TroopActor> affectedTroopList) {
		this.affectedTroopList = affectedTroopList;
	}

	public Table getTroopTitle() {
		return troopTitle;
	}

	public boolean isDestoryed() {
		return isDestoryed;
	}

	public void setDestoryed(boolean isDestoryed) {
		this.isDestoryed = isDestoryed;
	}

	public void parseStepAction(
			final BattleFieldAnimationStage battleFieldAnimationStage) {
		final StepAction currentStepAction = battleFieldAnimationStage.getCurrentStepAction();
		switch(currentStepAction.actionKind){
		case ATTACK:
		{
			//所有部队的动画都写在这里，不再用观察者模式了，没必要了。内容简单。
			
			setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.ATTACK));
			TileEffectActor effectActor = new TileEffectActor(currentStepAction.effects.get(currentStepAction.actionTroopId));
			effectActor.setPosition(getX(), getY());
			battleFieldAnimationStage.getLayerAnimation().add(effectActor);
			Integer tempInt=currentStepAction.damageMap.get(currentStepAction.actionTroopId);
			CombatNumberLabel damageLabel = new CombatNumberLabel(tempInt, true);
			if(tempInt.equals(0)){
				damageLabel.setVisible(false);
			}
			//add animation for damage hint
			damageLabel.addAction(sequence(Actions.color(Color.RED),moveTo(getX()+50,getY()+50),parallel(Actions.moveBy(0f,50f,Constants.ONE_STEP_TIME,Interpolation.linear))));
			battleFieldAnimationStage.getLayerAnimation().add(damageLabel);				
			

			TroopActor affectedTroopActor = null;
			final Array<TroopActor> affectedTroopActors = new Array<TroopActor>(currentStepAction.affectedTroopList.size);
			for(long i:currentStepAction.affectedTroopList){
				affectedTroopActor= battleFieldAnimationStage.getTroopActorByTroopId(i);
				affectedTroopActor.setAnimation(RenderUtils.getTroopAnimationBy(affectedTroopActor.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.BE_ATTACKED));
				affectedTroopActors.add(affectedTroopActor);
				effectActor=new TileEffectActor(currentStepAction.effects.get(i));
				effectActor.setPosition(affectedTroopActor.getX(), affectedTroopActor.getY());
				battleFieldAnimationStage.getLayerAnimation().add(effectActor);
				damageLabel = new CombatNumberLabel(currentStepAction.damageMap.get(i), true);
				damageLabel.addAction(sequence(Actions.color(Color.RED),moveTo(affectedTroopActor.getX()+50,affectedTroopActor.getY()+50),parallel(Actions.moveBy(0f,50f,Constants.ONE_STEP_TIME,Interpolation.linear))));
				battleFieldAnimationStage.getLayerAnimation().add(damageLabel);
			}
			float x = RenderUtils.translate(currentStepAction.orginPosition.x);
			float y = RenderUtils.translate(currentStepAction.orginPosition.y);
			RunnableAction runAction = run(new Runnable() {
				public void run() {
					setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.WALK));		
					modifyHpVisual(currentStepAction.damageMap.get(currentStepAction.actionTroopId));
					for(TroopActor trA:affectedTroopActors){
						trA.setAnimation(RenderUtils.getTroopAnimationBy(trA.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.WALK));
					    trA.modifyHpVisual(currentStepAction.damageMap.get(trA.getTroop().getId()));
					}
					//remove layerAnimation
					battleFieldAnimationStage.getLayerAnimation().clear();
					battleFieldAnimationStage.setPlanning(true);
				}
			});			
			addAction(sequence(moveTo(x, y),delay(Constants.ONE_STEP_TIME,runAction)));
		}	
			break;
		case CAST:
			break;
		case MOVE:
		{
			float x = RenderUtils.translate(currentStepAction.orginPosition.x);
			float y = RenderUtils.translate(currentStepAction.orginPosition.y);
			float toX = RenderUtils.translate(currentStepAction.objectPosition.x);
			float toY = RenderUtils.translate(currentStepAction.objectPosition.y);
			
			RunnableAction runAction = run(new Runnable() {
				public void run() {
	
						battleFieldAnimationStage.setPlanning(true);


				}
			});
			
			//分析stepAction 处理朝向
			addAction(sequence(moveTo(x,y),moveTo(toX, toY, Constants.ONE_STEP_TIME, Interpolation.linear),runAction));
//			logger.debug(currentStepAction.orginPosition.toString());
//			logger.debug(currentStepAction.objectPosition.toString());
			Animation temp = RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId,currentStepAction.faceDirection,TROOP_ANIMATION_TYPE.WALK);
			setAnimation(temp);
		}
			break;
		case NONE:
			break;
		default:
			break;
		
		}
		
		
		
	}

	protected void modifyHpVisual(int damage) {
		hpVisual-=damage;
		hpVisualLabel.setText(String.valueOf(hpVisual));	
		
	}

	@Override
	public void act(float delta) {
		position.set(getX(), getY());
		super.act(delta);
	}

}

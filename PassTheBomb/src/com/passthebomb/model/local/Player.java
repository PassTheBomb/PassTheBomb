package com.passthebomb.model.local;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.passthebomb.view.gui.Background;

public class Player extends Character {
	private final int VEL = 50;
	private final double DELTA_TIME = 0.17;
	private final int BOUND_OFFSET = 100;
	private int RADIUS = 32;
	
	private Vector3 tgtPos, tgtPosCpy;
	
	private boolean isMoving;
	private boolean collide;
	private boolean bombPassed;
	
	private int collideCtr;
	private int bombPassCtr;
	
	public Player(Vector2 absStartPos, Texture[] imgSet, boolean bomb, Background bg) {
		super(absStartPos, imgSet, bomb, bg);
		isMoving = false;
		collide = false;
		collideCtr = 0;
		bombPassed = false;
		bombPassCtr = 0;
		tgtPos = new Vector3(absStartPos.x+bg.getBackgroundPos().x, absStartPos.y+bg.getBackgroundPos().y,0);
		System.out.println("Playercreation"+getCharBox());
	}

	@Override
	public void update() {
		if(isAlive()){
			if(isMoving){
				if (bombPassed){
					bombPassCtr++;
					if (bombPassCtr > 12){
						bombPassed = false;
						bombPassCtr = 0;
					}
				}
				else{
					tgtPosCpy = tgtPos.cpy();
					tgtPosCpy.add(new Vector3(-getCharBox().x,-getCharBox().y,0));
					if (tgtPosCpy.len()> VEL){
						tgtPosCpy.scl(VEL/tgtPosCpy.len());
						getCharBox().x += tgtPosCpy.x * DELTA_TIME;
						getCharBox().y += tgtPosCpy.y * DELTA_TIME;
						System.out.println("RUN");
					}
					else if (tgtPosCpy.len()> 0.1){
						getCharBox().x += tgtPosCpy.x*DELTA_TIME;
						getCharBox().y += tgtPosCpy.y*DELTA_TIME;
						System.out.println("SLOW");
					}
					else{
						isMoving = false;
						System.out.println("STOP");
					}
				}
				
			}
			if(collide){
				tgtPosCpy = tgtPos.cpy();
				getCharBox().x += tgtPosCpy.x*DELTA_TIME;
				getCharBox().y += tgtPosCpy.y*DELTA_TIME;
				System.out.println("COLLIDE");
				collideCtr++;
				if (collideCtr > 2){
					collideCtr = 0;
					collide = false;
				}
			}
			this.scrollBG();
		}
		
	}

	@Override
	public void move(Vector3 tgtPos) {
		if (!collide){
			this.tgtPos = tgtPos;
			this.isMoving = true;
		}
		
	}

	@Override
	public void collide(Character c) {
		Circle thisHitBox = getCharBox();
		Circle otherHitBox = c.getCharBox();
		if(thisHitBox.overlaps(otherHitBox)){
			collide = true;
			isMoving = false;
			tgtPos = new Vector3((thisHitBox.x - otherHitBox.x),(thisHitBox.y - otherHitBox.y), 0);
			tgtPos.scl(VEL/tgtPos.len());
			collideCtr = 0;
			if ((c.isCarryingBomb() && !this.isCarryingBomb()) || (!c.isCarryingBomb() && this.isCarryingBomb())){
				c.changeBombState();
				this.changeBombState();
				bombPassed = true;
				bombPassCtr = 0;
			}
		}
		
	}
	
	private void scrollBG(){
		float diffX = 0;
		float diffY = 0;
		Circle charBox = super.getCharBox();
		if(charBox.x<BOUND_OFFSET+RADIUS){
			diffX = BOUND_OFFSET - charBox.x + RADIUS;
			charBox.x = BOUND_OFFSET+RADIUS;
			
		}
		else if(charBox.x>800 - BOUND_OFFSET - RADIUS){
			diffX = 800 - BOUND_OFFSET - charBox.x - RADIUS;
			charBox.x = 800-BOUND_OFFSET-RADIUS;
		}
		if(charBox.y<BOUND_OFFSET+RADIUS){
			diffY = BOUND_OFFSET - charBox.y + RADIUS;
			charBox.y = BOUND_OFFSET+RADIUS;
		}
		else if (charBox.y>480-BOUND_OFFSET-RADIUS){
			diffY = 480 - BOUND_OFFSET - charBox.y - RADIUS;
			charBox.y = 480 - BOUND_OFFSET-RADIUS;
		}
		getBg().scrollX(diffX);
		getBg().scrollY(diffY);
		tgtPos.x+=diffX;
		tgtPos.y+=diffY;
	}
	
	/*private final int VEL = 50;
	private final double DELTA_TIME = 0.17;
	private final int RADIUS = 32;
	private final int BOUNDARY_OFFSET = 100;

	private Circle circle;
	private Texture circleImg;
	private Background bg;
	private Vector3 targetPos, targetPosCopy;
	
	private boolean isMoving;
	private boolean collide;
	private boolean carryingBomb;
	boolean alive;
	
	private int collideCounter;
	
	protected Player(Vector2 absStartPos, Texture img, boolean bomb, Background bg){
		this.circleImg = img;
		absStartPos.x -= RADIUS;
		absStartPos.y -= RADIUS;
		this.circle = new Circle(bg.getBackgroundPos().add(absStartPos), RADIUS);
		this.bg = bg;
		this.isMoving = false;
		this.carryingBomb = bomb;
		this.alive = true;
	}
	
	protected void update(){
		System.out.println(targetPos);
		System.out.println(new Vector3(circle.x-RADIUS, circle.y-RADIUS, 0));
		if(alive){
			if(isMoving){
				targetPosCopy = targetPos.cpy();
				targetPosCopy.add(new Vector3(-circle.x-RADIUS,-circle.y-RADIUS,0));
				if (targetPosCopy.len()> VEL){
					targetPosCopy.scl(VEL/targetPosCopy.len());
					circle.x += targetPosCopy.x * DELTA_TIME;
					circle.y += targetPosCopy.y * DELTA_TIME;
					System.out.println("RUN");
				}
				else if (targetPosCopy.len()> 0.1){
					circle.x += targetPosCopy.x*DELTA_TIME;
					circle.y += targetPosCopy.y*DELTA_TIME;
					System.out.println("SLOW");
				}
				else{
					isMoving = false;
					System.out.println("STOP");
				}
			}
			if(collide){
				targetPosCopy = targetPos.cpy();
				circle.x += targetPosCopy.x*DELTA_TIME;
				circle.y += targetPosCopy.y*DELTA_TIME;
				System.out.println("COLLIDE");
				collideCounter++;
				if (collideCounter > 2){
					collideCounter = 0;
					collide = false;
				}
			}
			this.scrollBG();
		}
	}
	
	protected void move(Vector3 pointer){
		if (!collide){
			this.targetPos = pointer;
			this.isMoving = true;
		}
		
	}
	
	protected void collidePlayer(Player p){
		Circle c = p.getPlayerBox();
		if(circle.overlaps(c)){
			collide = true;
			isMoving = false;
			targetPos = new Vector3((circle.x - c.x),(circle.y - c.y), 0);
			targetPos.scl(VEL/targetPos.len());
			collideCounter = 0;
		}
	}
	
	private void scrollBG(){
		float diffX = 0;
		float diffY = 0;
		if(circle.x<BOUNDARY_OFFSET-RADIUS){
			diffX = BOUNDARY_OFFSET - circle.x - RADIUS;
			circle.x = BOUNDARY_OFFSET-RADIUS;
			
		}
		else if(circle.x>800-BOUNDARY_OFFSET-RADIUS){
			diffX = 800 - BOUNDARY_OFFSET - circle.x - RADIUS;
			circle.x = 800-BOUNDARY_OFFSET-RADIUS;
		}
		if(circle.y<BOUNDARY_OFFSET-RADIUS){
			diffY = BOUNDARY_OFFSET - circle.y - RADIUS;
			circle.y = BOUNDARY_OFFSET-RADIUS;
		}
		else if (circle.y>480-BOUNDARY_OFFSET-RADIUS){
			diffY = 480 - BOUNDARY_OFFSET - circle.y - RADIUS;
			circle.y = 480 - BOUNDARY_OFFSET-RADIUS;
		}
		bg.scrollX(diffX);
		bg.scrollY(diffY);
	}
	
	protected Texture getPlayerImg(){
		return this.circleImg;
	}
	
	protected Circle getPlayerBox(){
		return this.circle;
	}
	
	protected void dispose(){
		circleImg.dispose();
	}*/
}

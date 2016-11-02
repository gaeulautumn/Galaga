import java.awt.Image;

public class ShotSprite extends Sprite{
	private GalagaGame game;	
	
	public ShotSprite(GalagaGame game, Image image, int x, int y){ //생성자
		super(image, x, y);
		this.game = game;
		dy = -3;
	}
	
	@Override
	public void move(){
		super.move();
		if(y < -100){
			//불꽃이 화면 위로 올라가면 사라지도록
			game.removeShotSprite(this);
		}
	}
	@Override
	public void handleCollision(Sprite other){
	    //불꽃으로 외계인을 맞추면 둘다 소멸
		if(other instanceof AlienSprite){
			game.removeShotSprite(this);
			game.removeAlienSprite(other);
		}
	}
}



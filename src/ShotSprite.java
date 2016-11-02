import java.awt.Image;

public class ShotSprite extends Sprite{
	private GalagaGame game;	
	
	public ShotSprite(GalagaGame game, Image image, int x, int y){ //������
		super(image, x, y);
		this.game = game;
		dy = -3;
	}
	
	@Override
	public void move(){
		super.move();
		if(y < -100){
			//�Ҳ��� ȭ�� ���� �ö󰡸� ���������
			game.removeShotSprite(this);
		}
	}
	@Override
	public void handleCollision(Sprite other){
	    //�Ҳ����� �ܰ����� ���߸� �Ѵ� �Ҹ�
		if(other instanceof AlienSprite){
			game.removeShotSprite(this);
			game.removeAlienSprite(other);
		}
	}
}



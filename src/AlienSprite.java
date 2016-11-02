import java.awt.Image;

public class AlienSprite extends Sprite{
	private GalagaGame game;
	
	//������
	public AlienSprite(GalagaGame game, Image image, int x, int y){
		super(image, x, y);
		this.game = game;
		dx = -3;  //�ʱ⿡�� �������� �̵�
		
	}
	
	@Override
	public void move(){
		if (((dx < 0) && (x < 10)) || ((dx > 0) && (x > 780))){
			dx = -dx;  //����ٲ�
			y += 20;   //�Ʒ��γ�����
			if(y > 600){
				game.endGame(); //������ �������� ��������
			}
		}//end if
		super.move();
	}
}



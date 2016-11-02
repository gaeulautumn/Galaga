import java.awt.Image;

public class AlienSprite extends Sprite{
	private GalagaGame game;
	
	//생성자
	public AlienSprite(GalagaGame game, Image image, int x, int y){
		super(image, x, y);
		this.game = game;
		dx = -3;  //초기에는 왼쪽으로 이동
		
	}
	
	@Override
	public void move(){
		if (((dx < 0) && (x < 10)) || ((dx > 0) && (x > 780))){
			dx = -dx;  //방향바꿈
			y += 20;   //아래로내려옴
			if(y > 600){
				game.endGame(); //끝까지 내려오면 게임종료
			}
		}//end if
		super.move();
	}
}



import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Sprite {

	protected int x;     //현재위치의 x좌표
	protected int y;     //현재위치의 y좌표
	protected int dx;    //단위시간당 움직이는 x거리
	protected int dy;    //단위시간당 움직이는 y거리
	private Image image; //스프라이트가 가지고있는 이미지
	
	//생성자
	public Sprite(Image image, int x, int y){
		this.image = image;
		this.x = x;
		this.y = y;
	}
	
	public int getWidth(){//가로길이반환
		return image.getWidth(null);
	} 
	
	public int getHeight(){//세로길이반환
		return image.getHeight(null);
	}
	
	public void draw(Graphics g){//화면에그림
		g.drawImage(image, x, y, null);
	}
	
	public void move(){ //스프라이트를움직임
		x += dx;
		y += dy;
	}
	
	public void setDx(int dx){ //dx를설정
		this.dx = dx;
	}
	
	public void setDy(int dy){ //dy를설정
		this.dy = dy;
	}
	
	public int getDx(){ //dx를 반환
		return dx;
	}
	
	public int getDy(){ //dy를 반환
		return dy;
	}
	
	public int getX(){ //x를 반환
		return x;
	}
	
	public int getY(){ //y를 반환
		return y;
	}
	
	//충돌여부검사. 충돌이면 true를 리턴
	public boolean checkCollision(Sprite other){
		Rectangle myRect = new Rectangle();
		Rectangle otherRect = new Rectangle();
		myRect.setBounds(x, y, getWidth(), getHeight());
		otherRect.setBounds(other.getX(), other.getY(), other.getWidth(), other.getHeight());
		
		return myRect.intersects(otherRect);
	}
	
	//충돌을 처리한다 
	public void handleCollision(Sprite other){
		
	}
}

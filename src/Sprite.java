import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Sprite {

	protected int x;     //������ġ�� x��ǥ
	protected int y;     //������ġ�� y��ǥ
	protected int dx;    //�����ð��� �����̴� x�Ÿ�
	protected int dy;    //�����ð��� �����̴� y�Ÿ�
	private Image image; //��������Ʈ�� �������ִ� �̹���
	
	//������
	public Sprite(Image image, int x, int y){
		this.image = image;
		this.x = x;
		this.y = y;
	}
	
	public int getWidth(){//���α��̹�ȯ
		return image.getWidth(null);
	} 
	
	public int getHeight(){//���α��̹�ȯ
		return image.getHeight(null);
	}
	
	public void draw(Graphics g){//ȭ�鿡�׸�
		g.drawImage(image, x, y, null);
	}
	
	public void move(){ //��������Ʈ��������
		x += dx;
		y += dy;
	}
	
	public void setDx(int dx){ //dx������
		this.dx = dx;
	}
	
	public void setDy(int dy){ //dy������
		this.dy = dy;
	}
	
	public int getDx(){ //dx�� ��ȯ
		return dx;
	}
	
	public int getDy(){ //dy�� ��ȯ
		return dy;
	}
	
	public int getX(){ //x�� ��ȯ
		return x;
	}
	
	public int getY(){ //y�� ��ȯ
		return y;
	}
	
	//�浹���ΰ˻�. �浹�̸� true�� ����
	public boolean checkCollision(Sprite other){
		Rectangle myRect = new Rectangle();
		Rectangle otherRect = new Rectangle();
		myRect.setBounds(x, y, getWidth(), getHeight());
		otherRect.setBounds(other.getX(), other.getY(), other.getWidth(), other.getHeight());
		
		return myRect.intersects(otherRect);
	}
	
	//�浹�� ó���Ѵ� 
	public void handleCollision(Sprite other){
		
	}
}

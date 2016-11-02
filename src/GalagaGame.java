import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
		

public class GalagaGame extends JPanel implements KeyListener{

	private boolean running = true;   //true�� �� ������ ����, false�� �� �����������Ѵ�
	
	private ArrayList alienSprites = new ArrayList(); //�ܰ��ε��� ��ü�� ���� ��̸���Ʈ
	private ArrayList shotSprites = new ArrayList();  //�Ҳ��� ��ü�� ���� ��̸���Ʈ
	private Sprite starship;   //���ּ���ü
	
	//�̹����� �����ϱ� ���� ����
	private BufferedImage alienImage;   //�ܰ����̹���
	private BufferedImage shotImage;    //�Ҳ��̹���
	private BufferedImage shipImage;    //���ּ��̹���
	
	//������ �̹����� �ҷ���
	Image startImg = Toolkit.getDefaultToolkit().getImage("images/start.png"); //����ȭ���� ����̹���
	Image spaceImg = Toolkit.getDefaultToolkit().getImage("images/space.png"); //����ȭ���� ����̹���
	Image gameoverImg = Toolkit.getDefaultToolkit().getImage("images/gameover.png"); //���ӿ���ȭ�� �̹���
	
	static final int MAX_SHOT = 10; //�ѹ��� �ִ� �� �� �ִ� �Ҳ��Ǽ�
	
	//ȭ����ȯ�� ���� ��弳��
	static final int MAIN_MODE = 0;  //��ó��ȭ��
	static final int PLAY_MODE = 1;  //����������
	static final int GAME_OVER = 2;  //���ӿ���
	
	int currentMode = MAIN_MODE;     //������ ��带 ��Ÿ��. ������ ���θ��� ����
	
	static int ALIEN_WIDTH = 10;   //�����࿡ �ִ� �� �ܰ����� ����
	static int ALIEN_HEIGHT = 5;   //�����࿡ �ִ� �� �ܰ����� ����
                                   //�� �ܰ����� ������ ALIEN_WIDTH x ALIEN_HEIGHT �̴�
	static int ALIEN_SPEED = -3;   //�ܰ����� ������ �����̴� �ӵ�
	
	//������ ����
	int score = 0;
	int level = 1;
	
	//����ȭ���� ��¦�̴� ���ڸ� ���� ��۽�ų ������
	byte flag = 1;
	byte toggle = 1;
	
	//������
	public GalagaGame(){
		
		//������ ����
		JFrame frame = new JFrame("Galaga Game");
			
		//������ ����
		frame.setSize(800, 600);
		frame.add(this);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		//������ ��ü�� �̹����� �ҷ���
		try{
			shotImage = ImageIO.read(new File("images/fire.png"));
			shipImage = ImageIO.read(new File("images/starship.png"));
			alienImage = ImageIO.read(new File("images/alien.png"));
		} catch (IOException e){
			e.printStackTrace();
		}
		
		this.requestFocus();   //Ű�Է��� �ޱ����� ��Ŀ���� ������
		this.initSprites();    //���ӿ� �ʿ��� ��ü���� �ʱ�ȭ��
		addKeyListener(this);  //Ű�̺�Ʈ������ �߰�
	}
	
	//ȭ�鿡 ���� ��ü(���ּ�, �ܰ���)���� �ʱ�ȭ
	private void initSprites(){
		Random random = new Random(); //����Ŭ�����κ��� ��ü����
		
		starship = new StarShipSprite(this, shipImage, 370, 500); //���ּ��ʱ�ȭ
		
		//�� �ܰ����� ������ŭ �ܰ��� �������ʱ�ȭ
		for(int y=0; y<ALIEN_HEIGHT; y++){
			for(int x=0; x<ALIEN_WIDTH; x++){
				ALIEN_SPEED = 3 - random.nextInt(7);   //-3���� 3������ �ӵ��� �������� ��´�
				if(ALIEN_SPEED == 0) ALIEN_SPEED = -3; //�ӵ���0�̵Ǿ� �����ִ� ���� ����
				//�ܰ��� ��ü�� �� ������ŭ �����Ѵ�
				Sprite alien = new AlienSprite(this, alienImage, 100+(x*50), 50+(y*30));
				alien.setDx(ALIEN_SPEED);  //�������� ���� �ӵ����� ����
				alienSprites.add(alien);   //��̸���Ʈ�� ��ü�� �߰�
			}//end for
		}//end for
	}
	
	private void startGame(){ //���ӽ���
		alienSprites.clear();
		shotSprites.clear();   //��ü�� ����ִ� ��̸���Ʈ�� �����
		initSprites();         //��ü�� �ʱ�ȭ
	} 
	
	public void endGame(){ //��������
		//System.exit(0);
	}
	
	//�ش� �ܰ��ΰ�ü�� ��̸���Ʈ���� ����
	public void removeAlienSprite(Sprite sprite){
		alienSprites.remove(sprite); 
	}
	
	//�ش� �Ҳɰ�ü�� ��̸���Ʈ���� ����
	public void removeShotSprite(Sprite sprite){
		shotSprites.remove(sprite);
	}
	
	
	//�Ҳ��� �� �� ȣ��Ǵ� �޼ҵ�
	public void fire(){
		//�Ҳ��� �ִ� ����(MAX_SHOT)��ŭ�� �� �� �ִ�
		if(shotSprites.size() < MAX_SHOT){
			//�Ҳ� ��ü�� �����Ѵ�
			ShotSprite shot = new ShotSprite(this, shotImage, 
					                        starship.getX()+15, starship.getY()-30);
			shotSprites.add(shot);   //��ü�� ������ �� ��̸���Ʈ�� ����
		}
		else return;  //�ִ밳�� �̻��ϰ�� �������� �ʴ´�
	}

	//���� ��� �׿��� ��� ���� ������ �ö󰣴�
	//������ �ö� �� ȣ��Ǵ� �޼ҵ�
	public void nextLevel(){
		level++;         //�������÷���
		ALIEN_HEIGHT++;  //������ �÷���
		initSprites();   //��ü �ʱ�ȭ
	}
	
	//���ν����� ������ ������ 1�� �ʱ�ȭ���ִ� �޼ҵ�
	public void initLevel(){
		ALIEN_HEIGHT = 5;  //������ ������ �ʱⰪ���� ����
		ALIEN_SPEED = -3;  //������ �ӵ��� �ʱⰪ���� ����
 
		score = 0;
		level = 1; //�����ͷ����ʱ�ȭ
	}
	
	//��忡 ���� ȭ���� �ٲ㼭 �׷��ִ� �޼ҵ�
	@Override
	public void paint(Graphics g){
		//currentMode �� ���� ����ġ��
		switch(currentMode){
		case MAIN_MODE :  //����ȭ��
			//����� �� �׸��� �׷���
			g.drawImage(startImg, 0, 0, this.getWidth(), this.getHeight(), this);
			g.setColor(Color.WHITE);                         //�۾� ���� ������� ����
			g.setFont(new Font("Consolas", Font.BOLD, 30));  //��Ʈ�� ũ�⼳��
			if(flag == 1)                 //�÷��װ��� 1�� �� �۾��� ������(�����̴� ȿ���� ����)
				g.drawString("Press Space Key To Start",190,400);
			break;
			
		case PLAY_MODE :  //������ �÷������� ��
			//��� �׷���
			g.drawImage(spaceImg, 0, 0, this.getWidth(), this.getHeight(), this);
			for(int i=0; i<alienSprites.size(); i++){
				Sprite sprite = (Sprite) alienSprites.get(i);
				sprite.draw(g);  //�ܰ��� ��ü�� �ϳ��� ������ �׸���
			}
			for(int j=0; j<shotSprites.size(); j++){
				Sprite sprite2 = (Sprite) shotSprites.get(j);
				sprite2.draw(g); //�Ҳ� ��ü�� �ϳ��� ������ �׸���
			}
			starship.draw(g); //���ּ��� �׸���
			g.setColor(Color.WHITE);
			g.setFont(new Font("Consolas", Font.PLAIN, 20));
			
			//������ ������ ǥ�����ش�
			g.drawString("SCORE : " + score,5,17);   //score ������ �������� ���
			g.drawString("LEVEL " + level, 350,17);  //level ������ �������� ���
			g.drawString("ENTER: pause/restart ", 570,17);
			break;
			
		case GAME_OVER :  //���ӿ���
			g.drawImage(gameoverImg, 0, 0, this.getWidth(), this.getHeight(), this);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Consolas", Font.PLAIN, 20));	
			g.drawString("SCORE : " + score,300,340);       //�� ������ �˷��ش�
			g.setFont(new Font("Consolas", Font.BOLD, 25));
			g.drawString("Press Space Key To Restart",210,380);
			break;		
			
		}//end switch	
	}

	//������ ����Ǵ� ����
	public void gameLoop(){

			//����ȭ��
			while(currentMode == MAIN_MODE){
				
				//�÷��׸� ��� ��۽��������ν� ���ڰ� �����̵��� ��
				flag = (byte)(flag ^ toggle);
				
				repaint(); //�ٽñ׸���
				try{
					Thread.sleep(100);  //0.1�ʸ��� ���ڰ� �����̵���
				} catch (Exception e){
				}
			}
		
			//��������ȭ��
			while(currentMode == PLAY_MODE && running){
				for(int i=0; i<alienSprites.size(); i++){ //�ܰ��ε��� ��������
					Sprite sprite = (Sprite) alienSprites.get(i);
					sprite.move();
				} 
				for(int j=0; j<shotSprites.size(); j++){ //�Ҳ��� ��������
					Sprite sprite2 = (Sprite) shotSprites.get(j);
					sprite2.move();
				}
				starship.move(); //���ּ��� ��������
				
				//�߻��� �Ҳɰ� �ܰ����� �浹 �˻�
				for(int p=0; p<alienSprites.size(); p++){
					//��̸���Ʈ�κ��� �ܰ��� ��ü�� �ϳ��� ����
					Sprite alien = (Sprite) alienSprites.get(p);
					
					if(starship.checkCollision(alien)){
						currentMode = GAME_OVER; //���ּ��� �ܰ����� �浹�ϸ� ���ӿ�����尡 �ȴ�.
						break;
					}
					for(int s=0; s<shotSprites.size(); s++){	
						//��̸���Ʈ�κ��� �Ҳ� ��ü�� �ϳ��� ����
						Sprite shot = (Sprite) shotSprites.get(s);
									
						//�ܰ��ΰ� �Ҳ��� �浹�˻�
						if(shot.checkCollision(alien)){
							shot.handleCollision(alien);
							alien.handleCollision(shot);
							score += 100;	 //���� ���߸� ���� 100�� ����
						}//end if		
					}//end for				   
				}//end for

				//���� ��� ���̸� ����������
				if(alienSprites.size() == 0){
					nextLevel();
				}
				
				repaint(); //�ٽñ׸���
				
				try{
					Thread.sleep(10);
				} catch (Exception e){
				}
			}//end while
			
			//���ӿ���ȭ��
			while(currentMode == GAME_OVER){
				repaint();
			}
	}//end gameLoop
	
	//Ű �̺�Ʈ ó���κ�
	@Override
	public void keyPressed(KeyEvent e){
		switch(currentMode){
		case MAIN_MODE :
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				currentMode = PLAY_MODE; //���θ�忡�� �����̽��ٸ� ������ �÷��̸��� �ٲ�
			}
			break;
		case PLAY_MODE :
			//Ű���尪�� ���� ��ü���� ������
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				starship.setDx(-3);
			if(e.getKeyCode() == KeyEvent.VK_RIGHT)
				starship.setDx(+3);
			if(e.getKeyCode() == KeyEvent.VK_SPACE)
				fire();
			//����Ű�� ������ ������ �����ϰų� �ٽý����Ҽ��ִ�
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				if(running) running = false;  
				else running = true;
			}
			break;
		case GAME_OVER :
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				currentMode = MAIN_MODE;  //����ȭ�����ε��ư�
				initLevel();   //������ �ʱ�ȭ����
				startGame();   //���ӽ���
			}
			break;	
		}//end switch
	}
	
	//Ű���� ���� ���� ��
	@Override
	public void keyReleased(KeyEvent e){
		//Ű���忡�� ���� ���� ���ּ��� �����ֵ��� �ӵ��� 0 ���� ����
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			starship.setDx(0);
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			starship.setDx(0);
	}
	
	@Override
	public void keyTyped(KeyEvent arg0){
		
	}
	
	//���������޼ҵ�
	public void Sound(String file, boolean Loop){	
		//���������� �޾Ƶ鿩 �ش���带 �����Ų��.
		Clip clip;
		try {
		AudioInputStream ais = AudioSystem.getAudioInputStream
				              (new BufferedInputStream(new FileInputStream(file)));
		clip = AudioSystem.getClip();
		clip.open(ais);
		clip.start();
		if (Loop) clip.loop(-1);
		//Loop ����true�� ��������� ���ѹݺ���
		//false�� �ѹ������
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	//���θ޼ҵ�
	public static void main(String argv[]){

		GalagaGame g = new GalagaGame(); //���ӻ���

		g.Sound("cheerup.wav", true);  //�����
		
		//������ ��� ����ǵ��� ���ѷ���
		while(true){
			g.gameLoop();  //���ӷ����� ����
		}
	}
}

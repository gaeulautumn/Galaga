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

	private boolean running = true;   //true일 땐 게임을 진행, false일 땐 게임을정지한다
	
	private ArrayList alienSprites = new ArrayList(); //외계인들의 객체를 담을 어레이리스트
	private ArrayList shotSprites = new ArrayList();  //불꽃의 객체를 담을 어레이리스트
	private Sprite starship;   //우주선객체
	
	//이미지를 저장하기 위한 공간
	private BufferedImage alienImage;   //외계인이미지
	private BufferedImage shotImage;    //불꽃이미지
	private BufferedImage shipImage;    //우주선이미지
	
	//각각의 이미지를 불러옴
	Image startImg = Toolkit.getDefaultToolkit().getImage("images/start.png"); //메인화면의 배경이미지
	Image spaceImg = Toolkit.getDefaultToolkit().getImage("images/space.png"); //게임화면의 배경이미지
	Image gameoverImg = Toolkit.getDefaultToolkit().getImage("images/gameover.png"); //게임오버화면 이미지
	
	static final int MAX_SHOT = 10; //한번에 최대 쏠 수 있는 불꽃의수
	
	//화면전환을 위한 모드설정
	static final int MAIN_MODE = 0;  //맨처음화면
	static final int PLAY_MODE = 1;  //게임진행중
	static final int GAME_OVER = 2;  //게임오버
	
	int currentMode = MAIN_MODE;     //현재의 모드를 나타냄. 시작은 메인모드로 설정
	
	static int ALIEN_WIDTH = 10;   //가로축에 있는 총 외계인의 개수
	static int ALIEN_HEIGHT = 5;   //세로축에 있는 총 외계인의 개수
                                   //총 외계인의 개수는 ALIEN_WIDTH x ALIEN_HEIGHT 이다
	static int ALIEN_SPEED = -3;   //외계인이 옆으로 움직이는 속도
	
	//점수와 레벨
	int score = 0;
	int level = 1;
	
	//메인화면의 반짝이는 글자를 위해 토글시킬 변수들
	byte flag = 1;
	byte toggle = 1;
	
	//생성자
	public GalagaGame(){
		
		//프레임 생성
		JFrame frame = new JFrame("Galaga Game");
			
		//프레임 설정
		frame.setSize(800, 600);
		frame.add(this);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		//각각의 객체에 이미지를 불러옴
		try{
			shotImage = ImageIO.read(new File("images/fire.png"));
			shipImage = ImageIO.read(new File("images/starship.png"));
			alienImage = ImageIO.read(new File("images/alien.png"));
		} catch (IOException e){
			e.printStackTrace();
		}
		
		this.requestFocus();   //키입력을 받기위해 포커스를 맞춰줌
		this.initSprites();    //게임에 필요한 객체들을 초기화함
		addKeyListener(this);  //키이벤트리스너 추가
	}
	
	//화면에 나올 객체(우주선, 외계인)들을 초기화
	private void initSprites(){
		Random random = new Random(); //랜덤클래스로부터 객체생성
		
		starship = new StarShipSprite(this, shipImage, 370, 500); //우주선초기화
		
		//총 외계인의 개수만큼 외계인 생성및초기화
		for(int y=0; y<ALIEN_HEIGHT; y++){
			for(int x=0; x<ALIEN_WIDTH; x++){
				ALIEN_SPEED = 3 - random.nextInt(7);   //-3부터 3까지의 속도를 랜덤으로 얻는다
				if(ALIEN_SPEED == 0) ALIEN_SPEED = -3; //속도가0이되어 멈춰있는 것을 방지
				//외계인 객체를 총 개수만큼 생성한다
				Sprite alien = new AlienSprite(this, alienImage, 100+(x*50), 50+(y*30));
				alien.setDx(ALIEN_SPEED);  //랜덤으로 얻은 속도값을 설정
				alienSprites.add(alien);   //어레이리스트에 객체를 추가
			}//end for
		}//end for
	}
	
	private void startGame(){ //게임시작
		alienSprites.clear();
		shotSprites.clear();   //객체가 들어있는 어레이리스트를 비워줌
		initSprites();         //객체를 초기화
	} 
	
	public void endGame(){ //게임종료
		//System.exit(0);
	}
	
	//해당 외계인객체를 어레이리스트에서 삭제
	public void removeAlienSprite(Sprite sprite){
		alienSprites.remove(sprite); 
	}
	
	//해당 불꽃객체를 어레이리스트에서 삭제
	public void removeShotSprite(Sprite sprite){
		shotSprites.remove(sprite);
	}
	
	
	//불꽃을 쏠 때 호출되는 메소드
	public void fire(){
		//불꽃의 최대 개수(MAX_SHOT)만큼만 쏠 수 있다
		if(shotSprites.size() < MAX_SHOT){
			//불꽃 객체를 생성한다
			ShotSprite shot = new ShotSprite(this, shotImage, 
					                        starship.getX()+15, starship.getY()-30);
			shotSprites.add(shot);   //객체를 생성한 뒤 어레이리스트에 저장
		}
		else return;  //최대개수 이상일경우 생성하지 않는다
	}

	//적을 모두 죽였을 경우 다음 레벨로 올라간다
	//레벨이 올라갈 때 호출되는 메소드
	public void nextLevel(){
		level++;         //레벨을올려줌
		ALIEN_HEIGHT++;  //적들을 늘려줌
		initSprites();   //객체 초기화
	}
	
	//새로시작할 때마다 레벨을 1로 초기화해주는 메소드
	public void initLevel(){
		ALIEN_HEIGHT = 5;  //적들의 개수를 초기값으로 설정
		ALIEN_SPEED = -3;  //적들의 속도를 초기값으로 설정
 
		score = 0;
		level = 1; //점수와레벨초기화
	}
	
	//모드에 따라 화면을 바꿔서 그려주는 메소드
	@Override
	public void paint(Graphics g){
		//currentMode 에 따른 스위치문
		switch(currentMode){
		case MAIN_MODE :  //시작화면
			//배경이 될 그림을 그려줌
			g.drawImage(startImg, 0, 0, this.getWidth(), this.getHeight(), this);
			g.setColor(Color.WHITE);                         //글씨 색을 흰색으로 설정
			g.setFont(new Font("Consolas", Font.BOLD, 30));  //폰트및 크기설정
			if(flag == 1)                 //플래그값이 1일 때 글씨를 보여줌(깜빡이는 효과를 위해)
				g.drawString("Press Space Key To Start",190,400);
			break;
			
		case PLAY_MODE :  //게임을 플레이중일 때
			//배경 그려줌
			g.drawImage(spaceImg, 0, 0, this.getWidth(), this.getHeight(), this);
			for(int i=0; i<alienSprites.size(); i++){
				Sprite sprite = (Sprite) alienSprites.get(i);
				sprite.draw(g);  //외계인 객체를 하나씩 꺼내서 그린다
			}
			for(int j=0; j<shotSprites.size(); j++){
				Sprite sprite2 = (Sprite) shotSprites.get(j);
				sprite2.draw(g); //불꽃 객체를 하나씩 꺼내서 그린다
			}
			starship.draw(g); //우주선을 그린다
			g.setColor(Color.WHITE);
			g.setFont(new Font("Consolas", Font.PLAIN, 20));
			
			//점수와 레벨을 표시해준다
			g.drawString("SCORE : " + score,5,17);   //score 변수의 점수값을 출력
			g.drawString("LEVEL " + level, 350,17);  //level 변수의 레벨값을 출력
			g.drawString("ENTER: pause/restart ", 570,17);
			break;
			
		case GAME_OVER :  //게임오버
			g.drawImage(gameoverImg, 0, 0, this.getWidth(), this.getHeight(), this);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Consolas", Font.PLAIN, 20));	
			g.drawString("SCORE : " + score,300,340);       //총 점수를 알려준다
			g.setFont(new Font("Consolas", Font.BOLD, 25));
			g.drawString("Press Space Key To Restart",210,380);
			break;		
			
		}//end switch	
	}

	//게임이 진행되는 루프
	public void gameLoop(){

			//시작화면
			while(currentMode == MAIN_MODE){
				
				//플래그를 계속 토글시켜줌으로써 글자가 깜빡이도록 함
				flag = (byte)(flag ^ toggle);
				
				repaint(); //다시그린다
				try{
					Thread.sleep(100);  //0.1초마다 글자가 깜빡이도록
				} catch (Exception e){
				}
			}
		
			//게임진행화면
			while(currentMode == PLAY_MODE && running){
				for(int i=0; i<alienSprites.size(); i++){ //외계인들을 움직여줌
					Sprite sprite = (Sprite) alienSprites.get(i);
					sprite.move();
				} 
				for(int j=0; j<shotSprites.size(); j++){ //불꽃을 움직여줌
					Sprite sprite2 = (Sprite) shotSprites.get(j);
					sprite2.move();
				}
				starship.move(); //우주선을 움직여줌
				
				//발사한 불꽃과 외계인의 충돌 검사
				for(int p=0; p<alienSprites.size(); p++){
					//어레이리스트로부터 외계인 객체를 하나씩 꺼냄
					Sprite alien = (Sprite) alienSprites.get(p);
					
					if(starship.checkCollision(alien)){
						currentMode = GAME_OVER; //우주선과 외계인이 충돌하면 게임오버모드가 된다.
						break;
					}
					for(int s=0; s<shotSprites.size(); s++){	
						//어레이리스트로부터 불꽃 객체를 하나씩 꺼냄
						Sprite shot = (Sprite) shotSprites.get(s);
									
						//외계인과 불꽃의 충돌검사
						if(shot.checkCollision(alien)){
							shot.handleCollision(alien);
							alien.handleCollision(shot);
							score += 100;	 //적을 맞추면 점수 100점 증가
						}//end if		
					}//end for				   
				}//end for

				//적을 모두 죽이면 다음레벨로
				if(alienSprites.size() == 0){
					nextLevel();
				}
				
				repaint(); //다시그린다
				
				try{
					Thread.sleep(10);
				} catch (Exception e){
				}
			}//end while
			
			//게임오버화면
			while(currentMode == GAME_OVER){
				repaint();
			}
	}//end gameLoop
	
	//키 이벤트 처리부분
	@Override
	public void keyPressed(KeyEvent e){
		switch(currentMode){
		case MAIN_MODE :
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				currentMode = PLAY_MODE; //메인모드에서 스페이스바를 누르면 플레이모드로 바뀜
			}
			break;
		case PLAY_MODE :
			//키보드값에 따라 객체들이 움직임
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				starship.setDx(-3);
			if(e.getKeyCode() == KeyEvent.VK_RIGHT)
				starship.setDx(+3);
			if(e.getKeyCode() == KeyEvent.VK_SPACE)
				fire();
			//엔터키를 누르면 게임을 정지하거나 다시시작할수있다
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				if(running) running = false;  
				else running = true;
			}
			break;
		case GAME_OVER :
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				currentMode = MAIN_MODE;  //시작화면으로돌아감
				initLevel();   //레벨을 초기화해줌
				startGame();   //게임시작
			}
			break;	
		}//end switch
	}
	
	//키에서 손을 뗐을 때
	@Override
	public void keyReleased(KeyEvent e){
		//키보드에서 손을 떼면 우주선이 멈춰있도록 속도를 0 으로 설정
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			starship.setDx(0);
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			starship.setDx(0);
	}
	
	@Override
	public void keyTyped(KeyEvent arg0){
		
	}
	
	//사운드재생용메소드
	public void Sound(String file, boolean Loop){	
		//사운드파일을 받아들여 해당사운드를 재생시킨다.
		Clip clip;
		try {
		AudioInputStream ais = AudioSystem.getAudioInputStream
				              (new BufferedInputStream(new FileInputStream(file)));
		clip = AudioSystem.getClip();
		clip.open(ais);
		clip.start();
		if (Loop) clip.loop(-1);
		//Loop 값이true면 사운드재생을 무한반복함
		//false면 한번만재생
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	//메인메소드
	public static void main(String argv[]){

		GalagaGame g = new GalagaGame(); //게임생성

		g.Sound("cheerup.wav", true);  //배경음
		
		//게임이 계속 진행되도록 무한루프
		while(true){
			g.gameLoop();  //게임루프를 진행
		}
	}
}

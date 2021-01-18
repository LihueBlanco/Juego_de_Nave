import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Juego_de_nave extends PApplet {

//declaramos la cantidad de esferas
int much = 150;
//array para la posicion y ejes de cada esfera
float[] ballx = new float[much];
float[] bally = new float[much];
float[] ballz = new float[much];

//objetos externos
PShape vi;
int vida;
//variables de movimiento y entrada del mouse
int mx, my;
float nmx, nmy, speedx, speedy;
float speedz=0;
float vspz;
float speedlimit = 25;
float rot = 0;
//variables para la direccion de la camara
float cx, cy;
float lmx1, lmy1, lmx2, lmy2, lcy, lcx;
float x, y, z, fx, fy, fz;
int time = 50000;
float fov = PI/3.0f;
float cameraZ = (height/2.0f) / tan(fov/2.0f);
int lvl = 0;
//creamos un objeto sobre la clase
//con la que vamos a trabajar
Balls ball1 = new Balls(-3500, 3500, -3500, 3500, -15000, 1000);

//para conectar a travez de osc
// aun no lo utilizamos

/*
import oscP5.*;
 import netP5.*;
 OscP5 oscP5;
 */

public void setup() {
  frustum(0, 0, 0, 0, 1, 10000);
  
  vi =loadShape("vida.obj");
  mx = 500;
  my = 500;
  vida = 50;
}
public void draw() {
  perspective(fov, PApplet.parseFloat(width)/PApplet.parseFloat(height), cameraZ/50000.0f, cameraZ*50000.0f);
  leveleo();
  println(vida + "  " + speedlimit + "  "+fx+" "+fy+" "+fz);
}



public void leveleo() {
  if (lvl == 0) {
    background(4, 0, 10);
    cabine();
    ball1.dibujar(255, 100, 0, -3500, 3500, -3500, 3500, -15000, 1000);
    ball1.vida(-3500, 3500, -3500, 3500, -15000, 1000, 0);
    ball1.fin();
  }
}


//la clase donde vamos a generar y dibujar las esferas
class Balls {
  //variables para determinar los limites de las esferas
  Balls(int x0, int x1, int y0, int y1, int z0, int z1) {
    //le asignamos un valor random a las cordenadas
    //X Y Z donde estaran hubicadas las esferas
    for (int i=0; i<ballz.length; i++) {
      ballx[i]=random(x0, x1);
      bally[i]=random(y0, y1);
      ballz[i]=random(z0, z1);

      x = random(x0, x1);
      y = random(y0, y1);
      z = random(z0, z1);

      fx = random(x0, x1);
      fy = random(y0, y1);
      fz = -1000000;
    }
  }
  //en este void dibujaremos y moveremos a las esferas
  public void dibujar(int c1, int c2, int c3, int x0, int x1, int y0, int y1, int z0, int z1) {


    //dentro de este for vamos a estar dibujando y actualizando la
    //posicion de cada una de las esferas
    for (int i=0; i<ballz.length; i++) {
      //utilizamos push y pop matrix para que la posicion 
      //se mantenga unica en cada una de las esferas
      pushMatrix();

      //ajustamos el translate a las coordinadas random ya generadas
      translate(ballx[i], bally[i], ballz[i]);

      //definimos como se veran las esferas y el tamaño
      noStroke();
      fill(c1, c2, c3);
      sphere(170);

      popMatrix();


      nmx=mx-mouseX;
      nmy=my-mouseY;

      //haremos que se muevan unicamente cuando apretamos el mouse
      if (mousePressed==true) {



        speedz = speedz + 0.01f;

        if (speedz >= speedlimit) {
          speedz = speedlimit;
        }

        //en mx y my vamos a guardar la posicion del mouse mientras
        //las esferas no se esten moviendo
        //cuando apretemos el mouse vamos a restar el valor del mouse actual
        //por el valor de donde estaba aljoado antes(mx y my)
        //ese valor lo guardaremos en nmx y nmy y utilizaremos ese valor
        //para generar la direccion del movimiento en X y en Y
        //la velocidad que usaremos va a ser la mima que en el eje Z
        speedx=speedz/2*(nmx/100);
        speedy=speedz/2*(nmy/100);

        //haremos esta cuenta para que la velocidad en x y en y
        //no sea mucho mayor que la velocidad en z
        /* vspz va a ser igual a speedz mas su mitad
         si speedz es 10 vspz va a ser 15  */
        vspz=speedz;

        if (speedx>=vspz) {
          speedx = PApplet.parseInt(vspz);
        } else if (speedx <= -vspz) {
          speedx = PApplet.parseInt((-vspz));
        }
        if (speedy>=vspz) {
          speedy = PApplet.parseInt(vspz);
        } else if (speedy <= -vspz) {
          speedy = PApplet.parseInt((-vspz));
        }
      } else if (mousePressed==false) {

        //si no estamos apretando el mouse la idea es que no se siga moviendo nada
        if (speedz >= 2) {
          speedz = speedz - 0.01f;
        } else {
          speedz = speedz + 2;
        }

        speedx=speedz/2*(nmx/100);
        speedy=speedz/2*(nmy/100);

        //aca es donde guardamos la huvicacion del mouse
      }

      //generamos el movimiento
      bally[i] = bally[i] + speedy;
      ballx[i] = ballx[i] + speedx;
      ballz[i] = ballz[i] + speedz;

      //generamos el sistema de choque
      if (ballz[i]>=600 && ballx[i] < mx + 200 && ballx[i] > mx - 200 && bally[i] < my + 200 && bally[i] > my - 200 ) {
        background(100, 10, 10);
        speedz = speedz - (speedlimit);
        if (speedz <= -speedlimit) {
          speedz = -speedlimit;
        }
        vida = vida - 1;
      }



      //y aca es donde definimos los limites
      //si una esfera sobrepasa el limite positivo 
      //se va a volver a generar en el limite negativo
      //y si Z sobrepasa su limite se regenerara en su negativo 
      //pero volveremos a definir un X e Y random
      if (ballz[i]>=z1+1) {
        ballz[i]=z0;
        ballx[i]=random(x0, x1);
        bally[i]=random(y0, y1);
        speedz=speedz+0.03f;
      }
      if (ballz[i]<=z0-1) {
        ballz[i]=z1;
        speedz=speedz-0.03f;
      }
      if (ballx[i]>=x1+1) {
        ballx[i]=x0;
      }
      if (bally[i]>=y1+1) {
        bally[i]=y0;
      }
      if (ballx[i]<=x0-1) {
        ballx[i]=x1;
      }
      if (bally[i]<=y0-1) {
        bally[i]=y1;
      }
    }
  }

  public void vida(int x0, int x1, int y0, int y1, int z0, int z1, float t) {


    fill(50, 100, 255, 150);
    pushMatrix();
    translate(x, y, z);
    pushMatrix();
    translate(0, 0, 0);
    rotateY(rot);
    //rotateX();
    lights();
    vi.setFill(color(0, 255, 0));
    shape(vi, 0, 0);
    popMatrix();

    rot = rot+0.1f;
    y = y + speedy;
    x = x + speedx;
    z = z + speedz;


    if (z>=400 && x < mx + 300 && x > mx - 300 && y < my + 300 && y > my - 300 ) {
      background(10, 100, 10);
      vida = vida + 5;
      if (vida >= 150) {
        vida = 150;
      }
      speedlimit = speedlimit + 8;
      z=z0;
      x=random(x0, x1);
      y=random(y0, y1);
    }



    if (z>=z1+1000) {
      z=z0-1000;
      x=random(x0, x1);
      y=random(y0, y1);
      speedz=speedz+0.03f;
    }

    fill(50, 100, 255, 150);
    sphere(200);
    popMatrix();
  }


  public void fin() {
    pushMatrix();
    translate(fx, fy, fz);
    lights();
    fill(255, 233, 59);
    sphere(8000);
    popMatrix();
    fy = fy + (speedy);
    fx = fx + (speedx);
    fz = fz + (speedz);
  }
}


public void cabine() {
  noFill();
  stroke(255);
  ellipse(500, 500, 25, 25);
  translate(width/2, height/2, 0);
  cx = map(nmx, -500, 500, 0.8f, -0.8f);
  cy = map(nmy, -500, 500, -0.8f, 0.8f);
  rotateZ(cx);
  rotateX(cy);


  //generamos un spot de luz
  //con lo que iluminaremos a las esferas
  //para dar la sensacion de que estamos dentro de una cabina

  lmx1 = 0;
  lmy1= 0;    

  spotLight(200, 200, 255, lmx1, lmy1, 1000, 0, -nmy/750, -1, PI/2, 100);
  translate(-width/2, -height/2, 0);
}
  public void settings() {  size(1000, 1000, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Juego_de_nave" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

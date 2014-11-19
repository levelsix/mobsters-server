package com.lvl6.info;

import java.io.Serializable;

public class NotificationTitleColor implements Serializable {
	
	private double red;
	private double green;
	private double blue;
  
  public NotificationTitleColor(double red, double green, double blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public double getRed() {
    return red;
  }

  public double getGreen() {
    return green;
  }

  public double getBlue() {
    return blue;
  }

  @Override
  public String toString()
  {
	  return "NotificationTitleColor [red="
		  + red
		  + ", green="
		  + green
		  + ", blue="
		  + blue
		  + "]";
  }

}

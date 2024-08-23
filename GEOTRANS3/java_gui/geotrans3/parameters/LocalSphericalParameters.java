// CLASSIFICATION: UNCLASSIFIED

/*
 * LocalSphericalParameters.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package geotrans3.parameters;

/**
 *
 * @author comstam
 */
public class LocalSphericalParameters extends CoordinateSystemParameters 
{
  private double longitude;
  private double latitude;
  private double height;
  private double orientation;

      
  /** Creates a new instance of LocalSphericalParameters */
  public LocalSphericalParameters(int coordinateType, double _longitude, double _latitude, double _height, double _orientation) 
  {
     super(coordinateType);
    
    longitude = _longitude;
    latitude = _latitude;
    height = _height;
    orientation = _orientation;
 }
  
  
  /**
   * Tests if this object contains the same information as another  
   * LocalSphericalParameters object.
   * .
   * @param    parameters    LocalSphericalParameters object to compare 
   * @return   true if the information is the same, otherwise false 
   */
  public boolean equal(LocalSphericalParameters parameters)
  {
    if(super.equal(parameters) && longitude == parameters.getLongitude() && latitude == parameters.getLatitude() && height == parameters.getHeight() && orientation == parameters.getOrientation())
      return true;
    else
      return false;
  }
  
  
  public double getLongitude()
  {
    return longitude;
  }


  public double getLatitude()
  {
    return latitude;
  }


  public double getHeight()
  {
    return height;
  }


  public double getOrientation()
  {
    return orientation;
  }  
}

// CLASSIFICATION: UNCLASSIFIED

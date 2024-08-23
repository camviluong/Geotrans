// CLASSIFICATION: UNCLASSIFIED

/*
 * SphericalCoordinates.java
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package geotrans3.coordinates;

/**
 *
 * @author c.wendling
 */
public class SphericalCoordinates extends CoordinateTuple
{
  private double azimuth;
  private double elevAngle;
  private double radius;
  
  
  /** Creates a new instance of SphericalCoordinates */
  public SphericalCoordinates(int coordinateType) 
  {
    super(coordinateType);
    
    azimuth = 0;
    elevAngle = 0;
    radius = 0;
  }
  
  
  public SphericalCoordinates(int coordinateType, double _azimuth, double _elevAngle, double _radius) 
  {
    super(coordinateType);
    
    azimuth = _azimuth;
    elevAngle = _elevAngle;
    radius = _radius;
  }
  
  
  public SphericalCoordinates(int coordinateType, String _warningMessage, double _azimuth, double _elevAngle, double _radius) 
  {
    super(coordinateType, _warningMessage);
    
    azimuth = _azimuth;
    elevAngle = _elevAngle;
    radius = _radius;
  }
  
  
  public double getAzimuth()
  {
    return azimuth;
  }
  
  
  public double getElevAngle()
  {
    return elevAngle;
  }
  
  
  public double getRadius()
  {
    return radius;
  }
}

// CLASSIFICATION: UNCLASSIFIED

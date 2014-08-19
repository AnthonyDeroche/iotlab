
package collect;
import java.util.Arrays;

/**
 *
 */
public class SensorData{

  private final int[] values;
  private final long systemTime;
  private boolean isDuplicate;

  public SensorData(int[] values, long systemTime) {
    this.values = values;
    this.systemTime = systemTime;
  }


  public boolean isDuplicate() {
    return isDuplicate;
  }

  public void setDuplicate(boolean isDuplicate) {
    this.isDuplicate = isDuplicate;
  }

  public int getValue(int index) {
    return values[index];
  }

  public int getValueCount() {
    return values.length;
  }

  public long getSystemTime() {
    return systemTime;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (systemTime > 0L) {
      sb.append(systemTime).append(' ');
    }
    for (int i = 0, n = values.length; i < n; i++) {
      if (i > 0) sb.append(' ');
      sb.append(values[i]);
    }
    return sb.toString();
  }

  public static SensorData parseSensorData(Gateway server, String line) {
    return parseSensorData(server, line, 0);
  }

  public static SensorData parseSensorData(Gateway server, String line, long systemTime) {
    String[] components = line.trim().split("[ \t]+");
   
   if (components[0].length() > 8) {
      // Sensor data prefixed with system time
      try {
        systemTime = Long.parseLong(components[0]);
        components = Arrays.copyOfRange(components, 1, components.length);
      } catch (NumberFormatException e) {
        //First column does not seem to be system time
      }
    }
   
    // Sensor data line (probably)
 
    int[] data = parseToInt(components);
    
    if (data == null) {
      //System.err.println("Failed to parse data line: '" + line + "'");
      return null;
    }

    return new SensorData(data, systemTime);
  }

  private static int[] parseToInt(String[] text) {
    try {
      int[] data = new int[text.length];
      for (int i = 0, n = data.length; i < n; i++) {
        data[i] = Integer.parseInt(text[i]);
      }
      return data;
    } catch (NumberFormatException e) {
      return null;
    }
  }
}

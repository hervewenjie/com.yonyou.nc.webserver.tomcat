package ex05.pyrmont.core;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

/**
 * 一个简单pipeline的实现
 */
public class SimplePipeline implements Pipeline {

  public SimplePipeline(Container container) {
    setContainer(container);
  }

  // The basic Valve (if any) associated with this Pipeline.
  protected Valve basic = null;
  // The Container with which this Pipeline is associated.
  protected Container container = null;
  // the array of Valves
  protected Valve valves[] = new Valve[0];

  public void setContainer(Container container) {
    this.container = container;
  }

  public Valve getBasic() {
    return basic;
  }

  public void setBasic(Valve valve) {
    this.basic = valve;
    ((Contained) valve).setContainer(container);
  }

  /**
   * 把新的valve加入valve数组
   */
  public void addValve(Valve valve) {
    if (valve instanceof Contained)
      ((Contained) valve).setContainer(this.container);

    synchronized (valves) {
      Valve results[] = new Valve[valves.length +1];
      System.arraycopy(valves, 0, results, 0, valves.length);
      results[valves.length] = valve;
      valves = results;
    }
  }

  public Valve[] getValves() {
    return valves;
  }

  public void invoke(Request request, Response response)
    throws IOException, ServletException {
    // invoke当前pipeline中的第一个valve
    (new SimplePipelineValveContext()).invokeNext(request, response);
  }

  public void removeValve(Valve valve) {
  }

  // this class is copied from org.apache.catalina.core.StandardPipeline class's
  // StandardPipelineValveContext inner class.
  protected class SimplePipelineValveContext implements ValveContext {

    protected int stage = 0;

    public String getInfo() {
      return null;
    }

    // 调用pipeline中的valve
    public void invokeNext(Request request, Response response)
      throws IOException, ServletException {
      int subscript = stage;
      stage = stage + 1;
      // Invoke the requested Valve for the current request thread
      if (subscript < valves.length) {
        valves[subscript].invoke(request, response, this);
      }
      // 到最后一个valve了再调用basic的invoke
      else if ((subscript == valves.length) && (basic != null)) {
        basic.invoke(request, response, this);
      }
      else {
        throw new ServletException("No valve");
      }
    }
  } // end of inner class

}
/* explains Tomcat's default container */
package ex04.pyrmont.startup;

import ex04.pyrmont.core.SimpleContainer;
import org.apache.catalina.connector.http.HttpConnector;

/**
 * 说明默认的HttpConnector怎么使用
 */
public final class Bootstrap {

  public static void main(String[] args) {
    // 默认的HttpConnector
    HttpConnector connector = new HttpConnector();
    // 实现的简单Container
    SimpleContainer container = new SimpleContainer();
    connector.setContainer(container);
    try {
      // 运行connector
      connector.initialize();
      connector.start();

      // Java程序等待, 直到按下一个键
      System.in.read();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}